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
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.attributes;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.service.cmr.attributes.AttributeService;

/**
 * the class wraps the attributeservice of Alfresco and allows to get, create
 * new attributes and remove them.
 *
 * @see AttributeService for more details.
 */
public class ScriptAttributeService extends BaseScopableProcessorExtension
{

    protected AttributeService attributeService;

    public void setAttributeService(AttributeService attributeService)
    {
        this.attributeService = attributeService;
    }

    public Object getAttribute(Object... keys)
    {
        return attributeService.getAttribute(Arrays.copyOf(keys, keys.length, Serializable[].class));
    }

    public boolean exists(Object... keys)
    {
        return attributeService.exists(Arrays.copyOf(keys, keys.length, Serializable[].class));
    }

    public void createAttribute(Object value, Object... keys)
    {
        attributeService.createAttribute((Serializable) value,
                                         Arrays.copyOf(keys, keys.length, Serializable[].class));
    }

    public void setAttribute(Object value, Object... keys)
    {
        attributeService.setAttribute((Serializable) value,
                                      Arrays.copyOf(keys, keys.length, Serializable[].class));
    }

    public void removeAttribute(Object... keys)
    {
        attributeService.removeAttribute(Arrays.copyOf(keys, keys.length, Serializable[].class));
    }

    public void removeAttributes(final Object... keys)
    {
        Serializable[] sKeys = Arrays.copyOf(keys, keys.length, Serializable[].class);
        // remove support for leaving the middle key empty!
        if (sKeys.length == 3 && sKeys[1] == null)
        {

            final Set<Serializable[]> keysToDelete = new HashSet<Serializable[]>();

            attributeService.getAttributes(new AttributeService.AttributeQueryCallback()
            {

                @Override
                public boolean handleAttribute(Long id, Serializable value, Serializable[] foundKeys)
                {
                    if (foundKeys.length == 3 && sKeys[0].equals(foundKeys[0])
                            && sKeys[2].equals(foundKeys[2]))
                    {
                        keysToDelete.add(foundKeys);
                    }
                    return true;
                }
            }, new Serializable[] { sKeys[0] });

            for (Serializable[] keyTuple : keysToDelete)
            {
                attributeService.removeAttribute(keyTuple);
            }
        }
        else
        {
            attributeService.removeAttributes(sKeys);
        }
    }

    public Map<String, Object> getAttributes(Object... keys)
    {
        return getAllAttributes(keys);
    }

    public Map<String, Object> getAllAttributes(Object... keys)
    {

        final Map<String, Object> root = new HashMap<>();
        final int levelSpecified = keys.length;

        attributeService.getAttributes(new AttributeService.AttributeQueryCallback()
        {

            Map<String, Map<String, Object>> subObjects = new HashMap<>();

            @Override
            public boolean handleAttribute(Long id, Serializable value, Serializable[] keys)
            {
                Serializable[] relevantKeys = Arrays.copyOfRange(keys, levelSpecified, keys.length);

                if (relevantKeys.length == 0)
                {
                    root.put("value", value);
                }
                else if (relevantKeys.length == 1)
                {
                    root.put(relevantKeys[0].toString(), value);
                }
                else if (relevantKeys.length == 2)
                {
                    Map<String, Object> inner = subObjects.get(relevantKeys[0].toString());
                    if (inner == null)
                    {
                        inner = new HashMap<>();
                        subObjects.put(relevantKeys[0].toString(), inner);
                        root.put(relevantKeys[0].toString(), inner);
                    }
                    inner.put(relevantKeys[1].toString(), value);
                }
                else
                {
                    throw new AlfrescoRuntimeException(
                        "Result object with more than one key hierarchie are currently not supported.");
                }

                return true;
            }
        }, Arrays.copyOf(keys, keys.length, Serializable[].class));

        return root;
    }

}