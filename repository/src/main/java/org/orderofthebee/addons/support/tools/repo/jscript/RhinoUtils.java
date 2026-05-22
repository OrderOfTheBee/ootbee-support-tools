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
package org.orderofthebee.addons.support.tools.repo.jscript;

import org.alfresco.repo.jscript.ScriptNode;
import org.apache.commons.lang3.StringUtils;
import org.mozilla.javascript.*;

import java.util.*;

/**
 * Utility methods for converting data between Mozilla Rhino
 * representation and standard Java collections.
 *
 * @author Bulat Yaminov
 */
public class RhinoUtils
{

    public static Map<String, Object> convertToMap(ScriptableObject o)
    {
        Map<String, Object> map = new HashMap<>();
        Object[] propIds = o.getIds();
        for (Object propId : propIds)
        {
            if (propId instanceof String)
            {
                String key = (String) propId;
                Object value = o.get(key, o);
                map.put(key, value);
            }
        }
        return map;
    }

    public static int getInteger(Map<String, Object> map, String key, int defaultValue)
    {
        int result = defaultValue;
        Object val = map.get(key);
        if (val != null)
        {
            if (val instanceof NativeJavaObject)
            {
                val = ((NativeJavaObject) val).unwrap();
            }
            if (val instanceof Number)
            {
                result = ((Number) val).intValue();
                if (result < 0)
                {
                    throw new IllegalArgumentException(key + " must be a positive number, but is instead: " + val);
                }
            }
            else
            {
                throw new IllegalArgumentException(key + " must be an integer, but is instead: " + val);
            }
        }
        return result;
    }

    public static String getString(Map<String, Object> map, String key, String defaultValue)
    {
        String result = defaultValue;
        Object val = map.get(key);
        if (val != null)
        {
            if (val instanceof NativeJavaObject)
            {
                val = ((NativeJavaObject) val).unwrap();
            }
            if (val instanceof String)
            {
                String value = ((String) val);
                if (StringUtils.isNotBlank(value))
                {
                    result = value;
                }
            }
            else
            {
                throw new IllegalArgumentException(key + " must be an string, but is instead: " + val);
            }
        }
        return result;
    }

    public static Function getFunction(Map<String, Object> map, String key)
    {
        Function result = null;
        if (map.get(key) != null)
        {
            if (map.get(key) instanceof Function)
            {
                result = (Function) map.get(key);
            }
            else
            {
                throw new IllegalArgumentException(key + " must be a function, but is instead: " + map.get(key));
            }
        }
        return result;
    }

    public static List<Object> getArray(Map<String, Object> map, String key)
    {
        List<Object> result = null;
        Object value = map.get(key);
        if (value != null)
        {
            if (value instanceof NativeArray)
            {
                NativeArray array = (NativeArray) value;
                Object[] propIds = array.getIds();
                result = new ArrayList<>(propIds.length);
                for (Object propId : propIds)
                {
                    if (propId instanceof Integer)
                    {
                        result.add(array.get((Integer) propId, array));
                    }
                }
            }
            else if (value instanceof Object[])
            {
                result = Arrays.asList((Object[]) value);
            }
            else if (value instanceof NativeJavaArray)
            {
                result = Arrays.asList((Object[]) ((NativeJavaArray) value).unwrap());
            }
            else
            {
                throw new IllegalArgumentException(key +
                                                   " must be a NativeArray or Object[], but is instead: " + value);
            }
        }
        return result;
    }

    public static boolean getBoolean(Map<String, Object> map, String key, boolean defaultValue)
    {
        boolean result = defaultValue;
        Object val = map.get(key);
        if (val != null)
        {
            if (val instanceof NativeJavaObject)
            {
                val = ((NativeJavaObject) val).unwrap();
            }
            if (val instanceof Boolean)
            {
                result = (Boolean) val;
            }
            else
            {
                throw new IllegalArgumentException(key + " must be a boolean, but is instead: " + val);
            }
        }
        return result;
    }

    public static ScriptNode getScriptNode(Map<String, Object> map, String key)
    {
        ScriptNode result = null;
        if (map.get(key) != null)
        {
            if (map.get(key) instanceof NativeJavaObject)
            {
                Object o = ((NativeJavaObject) map.get(key)).unwrap();
                if (o instanceof ScriptNode)
                {
                    result = (ScriptNode) o;
                }
                else
                {
                    throw new IllegalArgumentException(key +
                                                       " must have a ScriptNode as Java object, but has instead: " + o);
                }
            }
            else
            {
                throw new IllegalArgumentException(key + " must be a JavaObject, but is instead: " + map.get(key));
            }
        }
        return result;
    }
}
