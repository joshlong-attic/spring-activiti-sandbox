package org.activiti.spring.components.config.xml;


import org.activiti.spring.components.aop.ProcessStartAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * registers support for handling the annotations in the org.activiti.engine.annotations package.
 *
 * @author Josh Long
 * @since 5.3
 */
public class ActivitiAnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

	private final String processEngineAttribute = "process-engine";

	private void registerProcessStartAnnotationBeanPostProcessor(Element element, ParserContext parserContext) {
		Class clz = ProcessStartAnnotationBeanPostProcessor.class;
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(clz.getName());
		rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		String procEngineRef = element.getAttribute(processEngineAttribute);

		if (StringUtils.hasText(procEngineRef)) {

			rootBeanDefinition.getPropertyValues().add(
					Conventions.attributeNameToPropertyName(processEngineAttribute),
					new RuntimeBeanReference(procEngineRef));
		}

		String beanName = baseBeanName(clz);
		parserContext.getRegistry().registerBeanDefinition(beanName, rootBeanDefinition);
	}

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		registerProcessStartAnnotationBeanPostProcessor(element, parserContext);
		return null;
	}

	private String baseBeanName(Class cl) {
		return cl.getName().toLowerCase();
	}
}

