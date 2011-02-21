package org.activiti.spring.components.config.xml;

import org.activiti.engine.ProcessEngine;
import org.activiti.spring.components.ActivitiContextUtils;
import org.activiti.spring.components.registry.ActivitiStateHandlerRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.logging.Logger;

/**
 * this class is responsible for registering the other {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}s
 * and {@link BeanFactoryPostProcessor}s.
 * <p/>
 * Particularly, this will register the {@link ActivitiStateHandlerRegistry} which is used to react to states.
 *
 * @author Josh Long
 */
public class StateHandlerAnnotationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private ProcessEngine processEngine ;

	public void setProcessEngine(ProcessEngine processEngine) {
		this.processEngine = processEngine;
	}

	private Logger log = Logger.getLogger(getClass().getName());

	private void configureDefaultActivitiRegistry(String registryBeanName, BeanDefinitionRegistry registry) {


		if (!beanAlreadyConfigured(registry, registryBeanName, ActivitiStateHandlerRegistry.class)) {
			String registryName =ActivitiStateHandlerRegistry.class.getName();
			log.info( "registering a " + registryName + " instance under bean name "+ ActivitiContextUtils.ACTIVITI_REGISTRY_BEAN_NAME+ ".");

			RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
			rootBeanDefinition.setBeanClassName( registryName );
			rootBeanDefinition.getPropertyValues().addPropertyValue("processEngine", this.processEngine);

			BeanDefinitionHolder holder = new BeanDefinitionHolder(rootBeanDefinition, registryBeanName);
			BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
		}
	}

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			configureDefaultActivitiRegistry(ActivitiContextUtils.ACTIVITI_REGISTRY_BEAN_NAME, registry);


		} else {
			log.info("BeanFactory is not a BeanDefinitionRegistry. The default '"
					+ ActivitiContextUtils.ACTIVITI_REGISTRY_BEAN_NAME + "' cannot be configured.");
		}
	}

	private boolean beanAlreadyConfigured(BeanDefinitionRegistry registry, String beanName, Class clz) {
		if (registry.isBeanNameInUse(beanName)) {
			BeanDefinition bDef = registry.getBeanDefinition(beanName);
			if (bDef.getBeanClassName().equals(clz.getName())) {
				return true; // so the beans already registered, and of the right type. so we assume the user is overriding our configuration
			} else {
				throw new IllegalStateException("The bean name '" + beanName + "' is reserved.");
			}
		}
		return false;
	}
}
