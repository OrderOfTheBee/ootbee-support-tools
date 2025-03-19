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

<@page title=msg("transform.localTransform.logs.title") dialog=true customCSSFiles=["ootbee-support-tools/css/transform.css"]>
    <@dialogbuttons />
    <div class="column-full">
        <@section label=msg("transform.localTransform.logs.heading", localTransformName)?html/>
        <p class="intro">${msg("transform.localTransform.logs.intro", localTransformName, localTransformUrl)?html}</p>
        <#if logTable??>
            <div class="localTransform-logs">
                ${logTable}
            </div>
        <#else>
            <p>
                <#if errorMessage??>
                ${msg("transform.localTransform.logs.error", errorMessage)?html}
                <#else>
                ${msg("transform.localTransform.logs.noLogs")?html}
                </#if>
            </p>
        </#if>
    </div>
    <@dialogbuttons />
</@page>