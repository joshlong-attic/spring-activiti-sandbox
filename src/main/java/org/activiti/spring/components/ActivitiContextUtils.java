package org.activiti.spring.components;

import org.activiti.spring.components.config.xml.StateHandlerAnnotationBeanFactoryPostProcessor;

/**
 * simple place to stash the constants used throughout the code
 *
 * @author Josh Long
 * @since 5.3
  */
public class ActivitiContextUtils {

	public static final String ANNOTATION_STATE_HANDLER_BEAN_FACTORY_POST_PROCESSOR_BEAN_NAME= StateHandlerAnnotationBeanFactoryPostProcessor.class.getName().toLowerCase();
	/**
	 * the name of the default registry used to store all state handling components
	 */
	public final static String ACTIVITI_REGISTRY_BEAN_NAME = "activitiComponentRegistry" ;


}
