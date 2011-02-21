package org.activiti.spring.components.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * responsible for providing <activiti:annotation-driven/> support.
 *
 * @author Josh Long
 * @since 5.3
 */
public class ActivitiNamespaceHandler extends NamespaceHandlerSupport {
	public void init() {
		registerBeanDefinitionParser("annotation-driven", new ActivitiAnnotationDrivenBeanDefinitionParser());
	}
}