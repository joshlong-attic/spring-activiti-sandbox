package org.activiti.spring.components.config.xml;


import org.activiti.spring.components.ActivitiContextUtils;
import org.activiti.spring.components.aop.ProcessStartAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.Conventions;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * registers support for handling the annotations in the org.activiti.engine.annotations package.
 * <p/>
 * The first major component is the state handlers. For this to work, a BeanFactoryPostProcessor is registered which in turn registers a
 * {@link org.activiti.spring.components.registry.ActivitiStateHandlerRegistry} if none exists.
 *
 * @author Josh Long
 * @since 5.3
 */
public class ActivitiAnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

	private final String processEngineAttribute = "process-engine";

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		registerStateHandlerAnnotationBeanFactoryPostProcessor(element, parserContext);
		registerProcessStartAnnotationBeanPostProcessor(element, parserContext);
		return null;
	}

	private void configureProcessEngine(AbstractBeanDefinition abstractBeanDefinition, Element element) {
		String procEngineRef = element.getAttribute(processEngineAttribute);
		if (StringUtils.hasText(procEngineRef))
			abstractBeanDefinition.getPropertyValues().add(Conventions.attributeNameToPropertyName(processEngineAttribute), new RuntimeBeanReference(procEngineRef));
	}

	private void registerStateHandlerAnnotationBeanFactoryPostProcessor(Element element, ParserContext context) {
		Class clz = StateHandlerAnnotationBeanFactoryPostProcessor.class;
		BeanDefinitionBuilder postProcessorBuilder = BeanDefinitionBuilder.genericBeanDefinition(clz.getName());

		BeanDefinitionHolder postProcessorHolder = new BeanDefinitionHolder(
				postProcessorBuilder.getBeanDefinition(),
				ActivitiContextUtils.ANNOTATION_STATE_HANDLER_BEAN_FACTORY_POST_PROCESSOR_BEAN_NAME);
		configureProcessEngine(postProcessorBuilder.getBeanDefinition(), element);
		BeanDefinitionReaderUtils.registerBeanDefinition(postProcessorHolder, context.getRegistry());

	}

	private void registerProcessStartAnnotationBeanPostProcessor(Element element, ParserContext parserContext) {
		Class clz = ProcessStartAnnotationBeanPostProcessor.class;
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(clz.getName());
		rootBeanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

		configureProcessEngine(rootBeanDefinition, element);

		String beanName = baseBeanName(clz);
		parserContext.getRegistry().registerBeanDefinition(beanName, rootBeanDefinition);
	}

	private String baseBeanName(Class cl) {
		return cl.getName().toLowerCase();
	}
}

