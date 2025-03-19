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

<@page title=msg("command-console.title") controller="/ootbee/admin" readonly=true
    customJSFiles=["ootbee-support-tools/js/command-console.js"]
    customCSSFiles=["ootbee-support-tools/css/command-console.css"]>
<#-- close the dummy form -->
</form>
    
<script type="text/javascript">//<![CDATA[
    AdminCC.setServiceContext('${url.serviceContext}');
    
    AdminCC.addMessages({
        'command-console.error.authentication': '${msg("command-console.error.authentication")?js_string}',
        'command-console.error.unknownCommand': '${msg("command-console.error.unknownCommand")?js_string}',
        'command-console.error.unknownPlugin': '${msg("command-console.error.unknownPlugin")?js_string}',
        'command-console.error.generic': '${msg("command-console.error.generic")?js_string}'
    });
//]]></script>

    <div class="column-full">
        <p class="intro">${msg("command-console.intro")?html}</p>
        
        <form onsubmit="return AdminCC.submitConsoleCommand(event);" accept-charset="utf-8">
            <input id="command-console-command" type="text" name="command" placeholder="${msg("command-console.command")?html}"></input>
            <input type="submit" value="${msg("command-console.execute")}" />
        </form>
        <div id="command-console-info">
            <div>${msg("command-console.help")?html}</div>
            <div>${msg("command-console.activePlugin")?html}:<span id="command-console-activePlugin">global</span></div>
        </div>
    </div>
    
    <div class="column-full">
      <@section label=msg("command-console.result") />
      <div>${msg("command-console.previousCommand")?html}:<span id="command-console-previousCommand"></span></div>
      <div id="command-console-lastError" class="hidden"></div>
      <div id="command-console-result"></div>
   </div>
</@page>