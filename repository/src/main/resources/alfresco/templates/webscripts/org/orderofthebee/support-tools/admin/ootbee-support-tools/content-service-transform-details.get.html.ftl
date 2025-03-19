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

<#include "../admin-template.ftl" />

<@page title=msg("transform.contentServiceTransform.detail.title") dialog=true >
    <@dialogbuttons />
    <div class="column-full">
        <#if headerKey??>
            <@section label=msg("transform.contentServiceTransform.detail." + headerKey)?html/>
        <#else>
            <@section label=header?html/>
        </#if>
        <div style="border: 1px solid #ccc; padding:0.5em; margin-top:1em;">
            <pre style="white-space: pre-wrap;">
                <#if messageKey??>
${msg("transform.contentServiceTransform.detail." + messageKey)?html}
                <#else>
${message?html}
                </#if>
            </pre>
        </div>
    <@dialogbuttons />
</@page>