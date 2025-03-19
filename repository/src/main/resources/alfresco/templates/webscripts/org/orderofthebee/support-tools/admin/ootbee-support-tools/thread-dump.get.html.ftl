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

<@page title=msg("threaddump.title") readonly=true customCSSFiles=["ootbee-support-tools/css/threads.css"] customJSFiles=["ootbee-support-tools/js/threads.js"]>

    <script type="text/javascript">//<![CDATA[
        AdminTD.setServiceContext('${url.serviceContext}');
        AdminTD.setToolName('thread-dump');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("threaddump.intro-text")?html}</p>

        <@button label=msg("threaddump.get-another") onclick="AdminTD.getDump();"/>
        <@button id="copycurrent" class="copy" label=msg("threaddump.copycurrent") onclick="AdminTD.copyToClipboard('current');"/>
        <@button id="savecurrent" class="save" label=msg("threaddump.savecurrent") onclick="AdminTD.saveTextAsFile('current');"/>
        <@button class="save" label=msg("threaddump.saveall") onclick="AdminTD.saveTextAsFile('all');"/>

        <@section label="" />
        <div id="control" class="tab-controls buttons"></div>
        <div id="viewer"></div>
    </div>

</@page>