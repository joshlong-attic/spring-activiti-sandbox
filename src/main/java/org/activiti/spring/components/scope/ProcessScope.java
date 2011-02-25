/*
 * Copyright 2010 the original author or authors
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.activiti.spring.components.scope;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.runtime.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.spring.components.aop.util.Scopifier;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * binds variables to a currently executing Activiti business process (a {@link org.activiti.engine.runtime.ProcessInstance}).
 *
 * Parts of this code are lifted wholesale from Dave Syer's work on the Spring 3.1 RefreshScope.
 *
 * @author Josh Long
 * @since 5.3
 *
 */
public class ProcessScope implements Scope, InitializingBean, BeanFactoryPostProcessor, DisposableBean {

	public final static String PROCESS_SCOPE_NAME = "process";

	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

	private boolean proxyTargetClass = true;

	private Logger logger = Logger.getLogger(getClass().getName());

	private ProcessEngine processEngine;

	private RuntimeService runtimeService;

	// set through Namespace reflection if nothing else
	@SuppressWarnings("unused")
	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	public Object get(String name, ObjectFactory<?> objectFactory) {

		ExecutionEntity executionEntity = null;
		try {
			logger.fine("returning scoped object having beanName '" + name + "' for conversation ID '" + this.getConversationId() + "'. ");

			ProcessInstance processInstance = Context.getExecutionContext().getProcessInstance();
			executionEntity = (ExecutionEntity) processInstance;

			Object scopedObject = executionEntity.getVariable(name);
			if (scopedObject == null) {
				scopedObject = objectFactory.getObject();
				if (scopedObject instanceof ScopedObject) {
					ScopedObject sc = (ScopedObject) scopedObject;
					scopedObject = sc.getTargetObject();
					logger.fine("de-referencing " + ScopedObject.class.getName() + "#targetObject before persisting variable");
				}
				persistVariable(name, scopedObject);
			}
			return createDirtyCheckingProxy(name, scopedObject);
		} catch (Throwable th) {
			logger.warning("couldn't return value from process scope! " + ExceptionUtils.getFullStackTrace(th));
		} finally {
			if (executionEntity != null) {
				logger.fine("set variable '" + name + "' on executionEntity# " + executionEntity.getId());
			}
		}
		return null;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		logger.fine("no support for registering descruction callbacks implemented currently. registerDestructionCallback('" + name + "',callback) will do nothing.");
	}

	private String getExecutionId() {
		return Context.getExecutionContext().getExecution().getId();
	}

	public Object remove(String name) {
		logger.fine("remove '" + name + "'");
		return runtimeService.getVariable(getExecutionId(), name);
	}

	public Object resolveContextualObject(String key) {

		if ("processInstance".equalsIgnoreCase(key)) {
			return getExecutionId();
		}

		if ("processInstanceId".equalsIgnoreCase(key)) {
			return getExecutionId();
		}

		if ("processEngine".equalsIgnoreCase(key)) {
			return this.processEngine;
		}

		return null;
	}

	public String getConversationId() {
		return getExecutionId();
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		beanFactory.registerScope(ProcessScope.PROCESS_SCOPE_NAME, this);

		Assert.state(beanFactory instanceof BeanDefinitionRegistry, "BeanFactory was not a BeanDefinitionRegistry, so ProcessScope cannot be used.");

		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
			// Replace this or any of its inner beans with scoped proxy if it has this scope
			boolean scoped = PROCESS_SCOPE_NAME.equals(definition.getScope());
			Scopifier scopifier = new Scopifier(registry, PROCESS_SCOPE_NAME, proxyTargetClass, scoped);
			scopifier.visitBeanDefinition(definition);
			if (scoped) {
				Scopifier.createScopedProxy(beanName, definition, registry, proxyTargetClass);
			}
		}
	}

	public void destroy() throws Exception {
		logger.info(ProcessScope.class.getName() + "#destroy() called ...");
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.processEngine, "the 'processEngine' must not be null!");
		this.runtimeService = this.processEngine.getRuntimeService();
	}

	private Object createDirtyCheckingProxy(final String name, final Object scopedObject) throws Throwable {
		ProxyFactory proxyFactoryBean = new ProxyFactory(scopedObject);
		proxyFactoryBean.setProxyTargetClass(this.proxyTargetClass);
		proxyFactoryBean.addAdvice(new MethodInterceptor() {
			public Object invoke(MethodInvocation methodInvocation) throws Throwable {
				Object result = methodInvocation.proceed();
				persistVariable(name, scopedObject);
				return result;
			}
		});
		return proxyFactoryBean.getProxy(this.classLoader);
	}

	private void persistVariable(String variableName, Object scopedObject) {
		ProcessInstance processInstance = Context.getExecutionContext().getProcessInstance();
		ExecutionEntity executionEntity = (ExecutionEntity) processInstance;
		Assert.isTrue(scopedObject instanceof Serializable, "the scopedObject is not " + Serializable.class.getName() + "!");
		executionEntity.setVariable(variableName, scopedObject);
	}
}

