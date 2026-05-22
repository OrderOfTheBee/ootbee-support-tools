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
/**
 *
 */
package org.orderofthebee.addons.support.tools.repo.jscript.dictionary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptableHashMap;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

import com.google.common.base.Preconditions;

/**
 * script object for dictionary
 *
 * some parts are copied from https://github.com/bluedolmen/App-blue-courrier/blob/ba0fa1e8119419fdb3bb3ccf844e60ad9df9a736/
 * alfresco-extensions/src/main/java/org/bluedolmen/repo/jscript/DictionaryScript.java
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types=ScriptClassType.JavaScriptRootObject, code= "dictionary",
             help="the root object for the de.jgoldhammer.alfresco.jscript.dictionary service")
public class ScriptDictionaryService extends BaseScopableProcessorExtension
{
    private DictionaryService dictionaryService;

    private NamespaceService namespaceService;

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    @ScriptMethod(
        help = "",
        output = "void",
        code = "",
        type = ScriptMethodType.READ)
    public Collection<QName> getAllTypes()
    {
        return dictionaryService.getAllTypes();
    }

    @ScriptMethod(
        help = "checks if the given ofType is subtype of the type",
        output = "void",
        code = "",
        type = ScriptMethodType.READ)
    public boolean isSubType(String type, String ofType)
    {
        Preconditions.checkNotNull(type, "type cannot be null here");
        Preconditions.checkNotNull(ofType, "ofType cannot be null here");
        return dictionaryService.isSubClass(QName.resolveToQName(namespaceService, type),
                                            QName.resolveToQName(namespaceService, ofType));
    }

    @ScriptMethod(
        help = "get type definition by name",
        output = "void",
        code = "",
        type = ScriptMethodType.READ)
    public TypeDefinition getType(String type)
    {
        return dictionaryService.getType(QName.resolveToQName(namespaceService, type));
    }

    public AspectDefinition getAspect(String aspectName)
    {
        final QName qname = QName.resolveToQName(namespaceService, aspectName);
        return dictionaryService.getAspect(qname);
    }

    /**
     * Get the property-names of the provided class-name
     *
     * @param className the name of the searched class formatted as an Alfresco
     *                  {@link QName}
     * @return an array of property-names as {@link String}s formatted as
     * Alfresco prefixed {@link QName}-s
     */
    public Scriptable getPropertyNames(String className)
    {

        final Map<QName, PropertyDefinition> propertyDefinitions = _getPropertyDefinitions(className);

        final List<String> propertyNames = new ArrayList<String>();
        for (QName propertyQName : propertyDefinitions.keySet())
        {
            propertyNames.add(propertyQName.toPrefixString());
        }

        return Context.getCurrentContext().newArray(
                   getScope(), propertyNames.toArray()
               );

    }

    private Map<QName, PropertyDefinition> _getPropertyDefinitions(String className)
    {

        final TypeDefinition typeDefinition = getType(className);
        if (typeDefinition == null) return null;

        final Map<QName, PropertyDefinition> propertyDefinitions = new HashMap<QName, PropertyDefinition>(typeDefinition.getProperties());

        final List<AspectDefinition> aspectDefinitions = typeDefinition.getDefaultAspects(true);
        for (AspectDefinition aspectDefinition : aspectDefinitions)
        {
            propertyDefinitions.putAll(aspectDefinition.getProperties());
        }

        return propertyDefinitions;
    }

    /**
     * Get the property-definitions of the provided class-name
     *
     * @param className the name of the searched class formatted as an Alfresco
     *                  {@link QName}
     * @return a {@link Scriptable} (Object) of {@link PropertyDefinition}-s indexed by their
     * prefixed {@link QName} (as strings)
     */
    public Scriptable getPropertyDefinitions(String className)
    {

        final Map<QName, PropertyDefinition> propertyDefinitions = _getPropertyDefinitions(className);
        final ScriptableHashMap<String, PropertyDefinition> result = new ScriptableHashMap<String, PropertyDefinition>();

        for (QName propertyQName : propertyDefinitions.keySet())
        {
            final String propertyName = propertyQName.toPrefixString();
            result.put(propertyName, propertyDefinitions.get(propertyQName));
        }

        return result;
    }

    /**
     * Get the {@link PropertyDefinition} of the provided name
     *
     * @param propertyName the name of the searched property formatted as an Alfresco
     *                     {@link QName}
     * @return the {@link PropertyDefinition} of the provided property-name (or null if
     * it does not exist)
     */
    public PropertyDefinition getPropertyDefinition(String propertyName)
    {

        final QName qname = QName.resolveToQName(namespaceService, propertyName);
        if (null == qname) return null;

        return dictionaryService.getProperty(qname);

    }

    /**
     * checks if a property is multivalued
     *
     * @param propertyName the name of the property
     * @return true if multivalued, false if single valued
     */
    public boolean isMultivalued(String propertyName)
    {
        PropertyDefinition property = dictionaryService.getProperty(QName.createQName(propertyName, namespaceService));
        return property.isMultiValued();
    }

    /**
     * checks if the given property has a listconstrained applied.
     *
     * @param propertyName
     * @return true if yes, false if not.
     */
    public boolean hasListConstaint(String propertyName)
    {
        PropertyDefinition property = dictionaryService.getProperty(QName.createQName(propertyName, namespaceService));
        List<ConstraintDefinition> constraints = property.getConstraints();
        for (ConstraintDefinition constraintDefinition : constraints)
        {
            String type = constraintDefinition.getConstraint().getType();
            if (type != null && type.equals("LIST"))
            {
                return true;
            }
        }
        return false;
    }

}
