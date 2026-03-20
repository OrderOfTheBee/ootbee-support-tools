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
package org.orderofthebee.addons.support.tools.repo.jscript.messages;

import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ValueConverter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 *
 * copied from https://github.com/bluedolmen/App-blue-courrier/blob/ba0fa1e8119419fdb3bb3ccf844e60ad9df9a736/
 * alfresco-extensions/src/main/java/org/bluedolmen/repo/jscript/MessageScript.java
 */
public class ScriptMessageService extends BaseScopableProcessorExtension
{

    private final ValueConverter valueConverter = new ValueConverter();

    public String get(String messageKey)
    {
        return getMessage(messageKey);
    }

    public String getMessage(String messageKey)
    {
        return messageService.getMessage(messageKey);
    }

    public String get(String messageKey, Scriptable params)
    {
        return getMessage(messageKey, params);
    }

    public String getMessage(String messageKey, Scriptable params)
    {

        final Object convertedValue = valueConverter.convertValueForJava(params);
        Preconditions.checkArgument(convertedValue instanceof List<?>);

        final List<?> paramsList = (List<?>) convertedValue;
        final Object[] paramsArray = paramsList.toArray();
        return messageService.getMessage(messageKey, paramsArray);

    }

    public Scriptable getRegisteredBundles()
    {
        Set<String> registeredBundles = messageService.getRegisteredBundles();
        return Context.getCurrentContext().newArray(getScope(),registeredBundles.toArray(new Object[registeredBundles.size()]));

    }

    private MessageService messageService;

    public void setMessageService(MessageService messageService)
    {
        this.messageService = messageService;
    }

}
