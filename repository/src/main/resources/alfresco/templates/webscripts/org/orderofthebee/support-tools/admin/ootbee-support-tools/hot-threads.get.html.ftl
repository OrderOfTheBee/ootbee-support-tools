<#-- 
Copyright (C) 2016 Axel Faust / Markus Joos
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
  
<#include "../admin-template.ftl" />

<@page title=msg("hotthreads.title") readonly=true customCSSFiles=["ootbee-support-tools/css/threads.css"] customJSFiles=["ootbee-support-tools/js/threads.js"]>

    <script type="text/javascript">//<![CDATA[
        AdminTD.setServiceContext('${url.serviceContext}');
        AdminTD.setToolName('hot-threads');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("hotthreads.intro-text")?html}</p>

        <@button label=msg("hotthreads.get-another") onclick="AdminTD.getDump();"/>
        <@button id="savecurrent" class="save" label=msg("hotthreads.savecurrent") onclick="AdminTD.saveTextAsFile('current');"/>
        <@button class="save" label=msg("hotthreads.saveall") onclick="AdminTD.saveTextAsFile('all');"/>

        <@section label="" />
        <div id="control" class="tab-controls buttons"></div>
        <div id="viewer"></div>
    </div>

</@page>