<#compress>
<#-- 
Copyright (C) 2016 Axel Faust
Copyright (C) 2016 Order of the Bee

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
Copyright (C) 2005-2016 Alfresco Software Limited.
 
  -->

<#escape x as jsonUtils.encodeJSONString(x)>
{
    "loggers": [
    <#list loggerStates as loggerState>
        {
            "name" : "<#if loggerState.isRoot>-root-<#else>${loggerState.name}</#if>",
            "urlName" : "<#if loggerState.isRoot>-root-<#else>${loggerState.name?replace('.', '%dot%')?url('UTF-8')}</#if>",
            "shortDisplayName" : "${compressName(loggerState.name, loggerState.isRoot)}",
            "displayName" : "<#if loggerState.isRoot>${msg('log-settings.rootLogger')}<#else>${loggerState.name}</#if>",
            <#if loggerState.parentIsRoot || loggerState.parent??>
            "parent" : {
                "name" : "<#if loggerState.parentIsRoot>-root-<#else>${loggerState.parent}</#if>",
                "shortDisplayName" : "${compressName(loggerState.parent!'', loggerState.parentIsRoot)}",
                "displayName" : "<#if loggerState.parentIsRoot>${msg('log-settings.rootLogger')}<#else>${loggerState.parent}</#if>"
            },
            </#if>
            "additivity" : "${loggerState.additivity?string}",
            "level" : "${loggerState.level!'UNSET'}",
            "canBeReset" : "${loggerState.canBeReset?string}",
            "effectiveLevel" : "${loggerState.effectiveLevel!'OFF'}"
        }<#if loggerState_has_next>,</#if>
    </#list>
    ]
}
</#escape>
</#compress>

<#function compressName loggerName loggerIsRoot subCall = false>
    <#local loggerCompressedName = "" />
    <#if loggerIsRoot>
        <#local loggerCompressedName = msg('log-settings.rootLogger') />
    <#elseif loggerName?contains('$')>
        <#local loggerCompressedName = compressName(loggerName?substring(0, loggerName?index_of('$')), loggerIsRoot, true) + loggerName?substring(loggerName?index_of('$')) />
    <#else>
        <#local fragments = loggerName?split(".") />
        <#list fragments as loggerNameFragment>
            <#if loggerNameFragment_index != 0>
                <#local loggerCompressedName = loggerCompressedName + "." />
            </#if>
            <#if loggerNameFragment_index &lt; fragments?size - 2 || (subCall && loggerNameFragment_index &lt; fragments?size - 1)>
                <#local loggerCompressedName = loggerCompressedName + loggerNameFragment?substring(0, 1) />
            <#else>
                <#local loggerCompressedName = loggerCompressedName + loggerNameFragment />
            </#if>
        </#list>
    </#if>
    <#return loggerCompressedName />
</#function>