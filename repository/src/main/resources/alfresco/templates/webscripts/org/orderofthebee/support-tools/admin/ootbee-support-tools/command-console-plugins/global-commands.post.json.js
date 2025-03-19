/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

function buildPropertyGetter(ctxt)
{
    var globalProperties, placeholderHelper, propertyGetter;

    globalProperties = ctxt.getBean('global-properties', Packages.java.util.Properties);
    placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);

    propertyGetter = function(propertyName, defaultValue)
    {
        var propertyValue;

        propertyValue = globalProperties[propertyName];
        if (propertyValue)
        {
            propertyValue = placeholderHelper.replacePlaceholders(propertyValue, globalProperties);
        }

        // native JS strings are always preferrable
        if (propertyValue !== undefined && propertyValue !== null)
        {
            propertyValue = String(propertyValue);
        }
        else if (defaultValue !== undefined)
        {
            propertyValue = defaultValue;
        }
        
        return propertyValue;
    };

    return propertyGetter;
}

function main()
{
    var service, ctxt, propertyGetter, availablePlugins;

    service = String(url.service);
    model.command = service.substring(service.lastIndexOf('/') + 1);
    if (model.command === 'listPlugins')
    {
        ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
        propertyGetter = buildPropertyGetter(ctxt);
        availablePlugins = propertyGetter('ootbee-support-tools.command-console.plugins', 'global');
        model.availablePlugins = availablePlugins.trim().split(/\s*,\s*/g);
    }
}

main();