package org.orderofthebee.addons.support.tools.repo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Created by deas on 12/16/16.
 */
public class ExtendedExceptionHackBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedExceptionHackBeanFactoryPostProcessor.class);
    private boolean enabled = false;
    private String beanDefName = "exceptionTranslator";

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setBeanDefName(String beanDefName) {
        this.beanDefName = beanDefName;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (this.enabled) {
            String hackClassName = ExtendedExceptionTranslatorMethodInterceptor.class.getName();
            // <bean id="exceptionTranslator" class="org.alfresco.repo.security.permissions.impl.ExceptionTranslatorMethodInterceptor"/>
            logger.info("Replacing bean class of {} - setting {}", beanDefName, hackClassName);
            BeanDefinition def = beanFactory.getBeanDefinition(beanDefName);
            def.setBeanClassName(hackClassName);
        } else {
            logger.debug("We are disabled");
        }

    }
}
