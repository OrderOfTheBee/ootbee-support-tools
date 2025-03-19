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
    "components": [
    <#assign first = true />
    <#list components as component>
        {
            "id" : "${component.surfComponent.id}",
            "guid" : "${component.surfComponent.getGUID()}",
            "region" : "${component.surfComponent.regionId}",
            "source" : "${component.surfComponent.sourceId}",
            "simpleSource" : "${component.simpleSource}",
            "simpleSourceDisplayName" : "${component.simpleSourceDisplayName}",
            "simpleSourceDescription" : "${component.simpleSourceDescription!""}",
            "simpleSourceType" : "${component.simpleSourceType}",
            "scope" : "${component.surfComponent.scope}",
            <#if component.surfComponent.index??>"index" : "${component.surfComponent.index}",</#if>
            "componentType" : "${component.surfComponent.componentTypeId}",
            "uri" : "${component.surfComponent.getURI()}",
            "url" : "${component.surfComponent.getURL()}"
            <#if component.surfComponent.chrome??>,"chrome" : "${component.surfComponent.chrome}"</#if>
        }<#if component_has_next>,</#if>
    </#list>
    ],
    "startIndex" : ${startIndex?c},
    "totalRecords" : ${totalRecords?c}
}
</#escape>
</#compress>