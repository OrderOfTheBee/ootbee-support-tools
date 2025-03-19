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
Copyright (C) 2005 - 2021 Alfresco Software Limited.
 
 -->
{
    <#if applicableRenditions??>
        "applicableRenditions": [
            <#list applicableRenditions as rendition>
                "${rendition?json_string}"<#if rendition_has_next>,</#if>
            </#list>
        ]
    <#elseif probeSuccessful??>
        "localTransformName": "${localTransformName?json_string}",
        "localTransformUrl": "${localTransformUrl?json_string}",
        "probeSuccessful": ${probeSuccessful?string("true", "false")}
        <#if probeResponse??>,
            "probeResponse": "${probeResponse?json_string}"
        <#elseif errorMessage??>,
            "errorMessage": "${errorMessage?json_string}"
        </#if>
    <#else>
        "transformers": [
            <#if transformers??><#list transformers as transformer>{
                "name": "${transformer.name?json_string}",
                "priority": ${transformer.priority?c},
                "maxSourceSizeBytes": ${transformer.maxSourceSizeBytes?c}
            }<#if transformer_has_next>,</#if>
            </#list></#if>
        ]<#if transformerName??>,
        "transformerName": "${transformerName?json_string}"
        </#if><#if maxSourceSizeBytes??>,
        "maxSourceSizeBytes": ${maxSourceSizeBytes?c}
        </#if>
    </#if>
}
</#compress>