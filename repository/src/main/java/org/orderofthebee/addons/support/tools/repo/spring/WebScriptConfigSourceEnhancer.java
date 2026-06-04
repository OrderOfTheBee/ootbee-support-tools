/**
 * Copyright (C) 2016 - 2026 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2026 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.repo.spring;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.extensions.config.source.UrlConfigSource;

/**
 * This Spring bean definition registry post processor enhanced the {@code webscripts.configsource} {@link UrlConfigSource} bean definition
 * to include a custom file provided by our module.
 *
 * @author Axel Faust
 */
public class WebScriptConfigSourceEnhancer implements BeanDefinitionRegistryPostProcessor
{

    private static final String REFERENCE_SOURCE_URL = "classpath:alfresco/web-client-security-config.xml";

    private static final String OOTBEE_SOURCE_URL = "classpath:alfresco/module/ootbee-support-tools-repo/web-client-security-config.xml";

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException
    {
        // NO-OP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException
    {
        if (registry.containsBeanDefinition("webscripts.configsource"))
        {
            final BeanDefinition beanDefinition = registry.getBeanDefinition("webscripts.configsource");
            final ConstructorArgumentValues constructorArgumentValues = beanDefinition.getConstructorArgumentValues();
            final List<ValueHolder> argumentValues = constructorArgumentValues.getGenericArgumentValues();
            for (final ValueHolder argumentValue : argumentValues)
            {
                final Object source = argumentValue.getValue();
                if (source instanceof List<?>)
                {
                    @SuppressWarnings("unchecked")
                    final List<Object> urls = (List<Object>) source;
                    for (int i = 0; i < urls.size(); i++)
                    {
                        final Object urlCandidate = urls.get(i);
                        // we want to add our config after the default and before any extension file
                        if (REFERENCE_SOURCE_URL.equals(urlCandidate) || (urlCandidate instanceof TypedStringValue
                                && ((TypedStringValue) urlCandidate).getValue().equals(REFERENCE_SOURCE_URL)))
                        {
                            urls.add(i + 1, OOTBEE_SOURCE_URL);
                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

}
