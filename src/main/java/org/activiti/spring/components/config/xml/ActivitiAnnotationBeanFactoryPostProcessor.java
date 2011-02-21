package org.activiti.spring.components.config.xml;

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
 * this clas is responsible for registering the other  {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor}s
 *
 * @author Josh Long
 */
public class ActivitiAnnotationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	private Logger log = Logger.getLogger(getClass().getName());

	private void configureDefaultActivitiRegistry(String registryBeanName, BeanDefinitionRegistry registry) {

		if (!isExistingBeanRegistered(registry, registryBeanName, ActivitiStateHandlerRegistry.class)) {
			RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
			rootBeanDefinition.setBeanClassName(ActivitiStateHandlerRegistry.class.getName());
			BeanDefinitionHolder holder = new BeanDefinitionHolder(rootBeanDefinition, registryBeanName);
			BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
		}
	}

	private boolean isExistingBeanRegistered(BeanDefinitionRegistry registry, String beanName, Class clz) {
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

	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof BeanDefinitionRegistry) {

			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

			configureDefaultActivitiRegistry( ActivitiContextUtils.ACTIVITI_REGISTRY_BEAN_NAME, registry);

		} else {
			log.info("BeanFactory is not a BeanDefinitionRegistry. The default '"
					+ ActivitiContextUtils.ACTIVITI_REGISTRY_BEAN_NAME + "' cannot be configured.");
		}
	}
}
