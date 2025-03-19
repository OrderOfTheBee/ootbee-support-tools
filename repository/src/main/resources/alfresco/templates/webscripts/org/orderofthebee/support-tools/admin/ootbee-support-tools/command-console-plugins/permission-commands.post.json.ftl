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
                "\t${msg("ootbee-support-tools.command-console.permissions.help.description")}",
                "",
                "effectivePermission user <userName> permission <permission> node <nodeRef>",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectivePermission.description")}",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectivePermissions.flexibleParameterPairs")}",
                "",
                "effectivePermissions user <userName> node <nodeRef>",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectivePermissions.description")}",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectivePermissions.flexibleParameterPairs")}",
                "",
                "effectiveAuthorisations user <userName> node <nodeRef>",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectiveAuthorisations.description")}",
                "\t${msg("ootbee-support-tools.command-console.permissions.effectiveAuthorisations.flexibleParameterPairs")}"
                <#break>
            <#case "effectivePermission">
            <#case "effectivePermissions">
                <#list checkedPermissions as checkedPermission>
                "${msg("permissionCheck.result", checkedPermission.user, checkedPermission.permission, checkedPermission.node.nodeRef, checkedPermission.node.name, checkedPermission.allowed?string(msg("permissionCheck.allowed"), msg("permissionCheck.denied")))}"<#if checkedPermission_has_next>,</#if>
                </#list>
                <#break>
            <#case "effectiveAuthorisations">
                <#list authorisations as authorisation>
                "${authorisation}"<#if authorisation_has_next>,</#if>
                </#list>
                <#break>
        </#switch>
    ]
}
</#escape>
</#compress>