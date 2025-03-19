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

<#include "../admin-template.ftl" />

<@page title=msg("scheduled-jobs.execute.title") dialog=true >

    <div class="column-full">
        <p class="intro">${msg("scheduled-jobs.execute.intro-text")?html}</p>
        <#if success>
            <p class="success">${msg("scheduled-jobs.execute.success", args.jobName)?html}</p>
        <#else>
            <p class="failure">${msg("scheduled-jobs.execute.error")?html}</p>
        </#if>
      
        <@dialogbuttons />
    </div>
   
</@page>