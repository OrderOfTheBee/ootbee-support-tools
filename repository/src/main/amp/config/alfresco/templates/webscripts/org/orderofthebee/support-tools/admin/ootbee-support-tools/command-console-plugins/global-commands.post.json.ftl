<#compress>
<#-- 
Copyright (C) 2018 Axel Faust
Copyright (C) 2018 Order of the Bee

This file is part of Community Support Tools

Community Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Community Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005-2017 Alfresco Software Limited.
 
  -->
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "preformattedOutputLines": [
        <#switch command>
            <#case "help">
                "help",
                "\t${msg("ootbee-support-tools.command-console.global.help.description")}",
                "",
                "listPlugins",
                "\t${msg("ootbee-support-tools.command-console.global.listPlugins.description")}",
                "",
                "activatePlugin <plugin>",
                "\t${msg("ootbee-support-tools.command-console.global.activatePlugin.description")}"
                <#break>
            <#case "listPlugins">
                <#list availablePlugins as plugin>
                "${plugin}",
                "\t${msg("ootbee-support-tools.command-console." + plugin + ".description")}"<#if plugin_has_next>, "",</#if>
                </#list>
                <#break>
        </#switch>
    ]
}
</#escape>
</#compress>