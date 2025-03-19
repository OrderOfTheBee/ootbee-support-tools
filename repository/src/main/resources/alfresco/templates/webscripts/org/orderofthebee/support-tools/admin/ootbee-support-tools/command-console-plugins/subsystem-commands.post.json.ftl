<#compress>
<#-- 
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.
 
  -->
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "preformattedOutputLines": [
        <#switch command>
            <#case "help">
                "help",
                "\t${msg("ootbee-support-tools.command-console.subsystems.help.description")}",
                "",
                "listInstances",
                "\t${msg("ootbee-support-tools.command-console.subsystems.listInstances.description")}",
                "",
                "listProperties <instanceId> (withSensitiveValues)?",
                "\t${msg("ootbee-support-tools.command-console.subsystems.listProperties.description")}",
                "",
                "setProperty <instanceId> <key>=<value>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.setProperty.description")}",
                "",
                "setProperties <instanceId> <key1>=<value1> <key2>=<value2> <keyN>=<valueN>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.setProperties.description")}",
                "",
                "removeProperties <instanceId> <key1> <key2> <keyN>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.removeProperties.description")}",
                "",
                "revert <instanceId>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.revert.description")}",
                "stop <instanceId>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.stop.description")}",
                "start <instanceId>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.start.description")}",
                "restart <instanceId>",
                "\t${msg("ootbee-support-tools.command-console.subsystems.restart.description")}"
                <#break>
            <#case "listInstances">
                <#list subsystemInstances as subsystemInstance>
                    <@renderSubsystemInstanceRow subsystemInstance /><#if subsystemInstance_has_next>,</#if>
                </#list>
                <#break>
            <#case "listProperties">
                <#if requestedInstanceId??>
                    <#if subsystemInstance??>
                        <@renderSubsystemInstanceRow subsystemInstance />,
                        "",
                        "${msg("ootbee-support-tools.command-console.subsystems.mutableProperties")}",
                        "----"
                        <#list mutableProperties as property>,
                            <@renderSubsystemInstancePropertyRow property />
                        </#list>,
                        "",
                        "${msg("ootbee-support-tools.command-console.subsystems.immutableProperties")}",
                        "----"
                        <#list immutableProperties as property>,
                            <@renderSubsystemInstancePropertyRow property />
                        </#list>
                    <#else>
                        "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                    </#if>
                <#else>
                "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
            <#case "setProperty">
                <#if requestedInstanceId?? && subsystemInstance?? && propertyName?? && property??>
                    <@renderSubsystemInstanceRow subsystemInstance />,
                    "",
                    "${msg("ootbee-support-tools.command-console.subsystems.newValue")}",
                    "----",
                    <@renderSubsystemInstancePropertyRow property />
                <#elseif requestedInstanceId?? && subsystemInstance?? && propertyName??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.failedUpdate", propertyName)}"
                <#elseif requestedInstanceId?? && subsystemInstance??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.missingPropertyParameter")}"
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
            <#case "setProperties">
                <#if requestedInstanceId?? && subsystemInstance?? && propertyNames?? && properties??>
                    <@renderSubsystemInstanceRow subsystemInstance />,
                    "",
                    "${msg("ootbee-support-tools.command-console.subsystems.newValue")}",
                    "----"
                    <#list properties as property>,
                        <@renderSubsystemInstancePropertyRow property />
                    </#list>
                <#elseif requestedInstanceId?? && subsystemInstance?? && propertyNames??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.failedUpdate", propertyNames)}"
                <#elseif requestedInstanceId?? && subsystemInstance??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.missingPropertyParameter")}"
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
            <#case "removeProperties">
                <#if requestedInstanceId?? && subsystemInstance?? && propertyNames?? && properties??>
                    <@renderSubsystemInstanceRow subsystemInstance />,
                    "",
                    "${msg("ootbee-support-tools.command-console.subsystems.newValue")}",
                    "----"
                    <#list properties as property>,
                        <@renderSubsystemInstancePropertyRow property />
                    </#list>
                <#elseif requestedInstanceId?? && subsystemInstance?? && propertyNames??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.failedUpdate", propertyNames)}"
                <#elseif requestedInstanceId?? && subsystemInstance??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.missingPropertyParameter")}"
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
            <#case "revert">
                <#if requestedInstanceId?? && subsystemInstance??>
                    <@renderSubsystemInstanceRow subsystemInstance />
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
			<#case "stop">
                <#if requestedInstanceId?? && subsystemInstance??>
                    <@renderSubsystemInstanceRow subsystemInstance />
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
			<#case "start">
                <#if requestedInstanceId?? && subsystemInstance??>
                    <@renderSubsystemInstanceRow subsystemInstance />
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
			<#case "restart">
                <#if requestedInstanceId?? && subsystemInstance??>
                    <@renderSubsystemInstanceRow subsystemInstance />
                <#elseif requestedInstanceId??>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.unknownInstance", requestedInstanceId)}"
                <#else>
                    "${msg("ootbee-support-tools.command-console.subsystems.error.instanceIdRequired")}"
                </#if>
                <#break>
        </#switch>
    ]
}
</#escape>
</#compress>

<#macro renderSubsystemInstanceRow subsystemInstance><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"${msg("ootbee-support-tools.command-console.subsystems.category")}: ${subsystemInstance.category}<#if subsystemInstance.typeName??> - ${msg("ootbee-support-tools.command-console.subsystems.type")}: ${subsystemInstance.typeName}</#if> - ${msg("ootbee-support-tools.command-console.subsystems.id")}: ${subsystemInstance.id}<#if subsystemInstance.currentSourceBean??> (${msg("ootbee-support-tools.command-console.subsystems.activeInstance")}: ${subsystemInstance.currentSourceBean})</#if>"
</#escape></#compress></#macro>

<#macro renderSubsystemInstancePropertyRow property><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"${property.key}: ${property.value!""}<#if property.description??> (${property.description})</#if>"
</#escape></#compress></#macro>