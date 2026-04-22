/**
 * Copyright (C) 2016 - 2025 Order of the Bee
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
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */
package org.orderofthebee.addons.support.tools.repo.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * This class provides utilities for handling properties across different versions of Alfresco Content Services.
 *
 * @author Axel Faust
 */
public final class PropertyUtilities
{

    private PropertyUtilities()
    {
        // NO-OP
    }

    /**
     * Retrieves an instance of a property placeholder helper that works in the current version of Alfresco Content Services.
     *
     * @return the property placeholder helper instance
     * @throws Exception
     *     if an error occurs during instantiation
     */
    public static PropertyPlaceholderHelper getPropertyPlaceholderHelper() throws Exception
    {
        Object[] params;
        // Spring 7.x
        Constructor<PropertyPlaceholderHelper> ctor = lookup(String.class, String.class, String.class, Character.class, boolean.class);
        if (ctor != null)
        {
            params = new Object[] { PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX,
                    PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX, PropertyPlaceholderConfigurer.DEFAULT_VALUE_SEPARATOR, '\\',
                    false };
        }
        else
        {
            ctor = lookup(String.class, String.class, String.class, boolean.class);
            params = new Object[] { PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_PREFIX,
                    PropertyPlaceholderConfigurer.DEFAULT_PLACEHOLDER_SUFFIX, PropertyPlaceholderConfigurer.DEFAULT_VALUE_SEPARATOR,
                    false };
        }
        return ctor.newInstance(params);
    }

    private static Constructor<PropertyPlaceholderHelper> lookup(final Class<?>... types)
    {
        Constructor<PropertyPlaceholderHelper> ctor = null;
        try
        {
            ctor = PropertyPlaceholderHelper.class.getConstructor(types);
        }
        catch (final NoSuchMethodException ignore)
        {
            // ignored
        }

        return ctor;
    }
}
