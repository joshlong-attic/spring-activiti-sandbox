package org.activiti.spring.components.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * responsible for providing <activiti:annotation-driven/> support.
 *
 * @author Josh Long
 * @since 5.3
 */
public class ActivitiNamespaceHandler extends NamespaceHandlerSupport
{
	public void init() {
	  registerBeanDefinitionParser("annotation-driven" ,
				new ActivitiAnnotationDrivenBeanDefinitionParser() );
	}

	static class ActivitiAnnotationDrivenBeanDefinitionParser  extends AbstractSingleBeanDefinitionParser {

		
	}


/* implements BeanDefinitionParser {

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition messagingAnnotationPostProcessorDef = new RootBeanDefinition(
				IntegrationNamespaceUtils.BASE_PACKAGE + ".config.annotation.MessagingAnnotationPostProcessor");
		messagingAnnotationPostProcessorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		String messagingAnnotationPostProcessorName = IntegrationNamespaceUtils.BASE_PACKAGE + ".internalMessagingAnnotationPostProcessor";
		parserContext.getRegistry().registerBeanDefinition(messagingAnnotationPostProcessorName, messagingAnnotationPostProcessorDef);
		RootBeanDefinition publisherAnnotationPostProcessorDef = new RootBeanDefinition(
				IntegrationNamespaceUtils.BASE_PACKAGE + ".aop.PublisherAnnotationBeanPostProcessor");
		publisherAnnotationPostProcessorDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		String defaultPublisherChannel = element.getAttribute("default-publisher-channel");
		if (StringUtils.hasText(defaultPublisherChannel)) {
			publisherAnnotationPostProcessorDef.getPropertyValues().add("defaultChannel", new RuntimeBeanReference(defaultPublisherChannel));
		}
		String publisherAnnotationPostProcessorName = IntegrationNamespaceUtils.BASE_PACKAGE + ".internalPublisherAnnotationBeanPostProcessor";
		parserContext.getRegistry().registerBeanDefinition(publisherAnnotationPostProcessorName, publisherAnnotationPostProcessorDef);
		return null;
	}


}
*/


}