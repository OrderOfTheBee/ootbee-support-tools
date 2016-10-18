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

<@page title=msg("admin.log.settings.tail.title" ) dialog=true customJSFiles=["ootbee-support-tools/js/log-tail.js"] customCSSFiles=["ootbee-support-tools/css/log4j-tail.css"]>

    <script type="text/javascript">//<![CDATA[
        AdminTL.setServiceUrl('${url.service}');
        AdminTL.setAppenderUUID('${uuid}');
    //]]></script>

	<div id="loggrid" class="datagrid" >
        <table>
            <tbody id="loggridBody">			 
            </tbody>
        </table>
    </div>

	<div id="textonlybox" class="textonlybox" style="display: none;" >
        <textarea id="textareaLog" cols=150 rows=60 font=small class="log" wrap="logical" readonly=true >
        </textarea>		  
	</div>

	<div class="datagrid">
        <table width="100%">
            <tbody>
                <tr>
                    <td><@button class="cancel" label=msg("admin-console.close") onclick="AdminTL.closeDialog();" /></td>	
                    <td width="25%">Autorefresh: 
                        <@button id="starttimer" label="Start" description="" onclick="AdminTL.startTimer();" />
                        <@button id="stoptimer" label="Stop" description="" onclick="AdminTL.stopTimer();" />
                    </td>				
                    <td>Refreshing interval:<input name="myinterval" id="myinterval" size="3" placeholder="myinterval" value=3></td>
                    <td> Timer:<input name="countdown" id="countdown" size="3" placeholder="countdown" value=3 disabled = true /></td>
                    <td><img src="${url.context}/images/filetypes/txt.gif" onclick="AdminTL.stopTimer();AdminTL.switchMode();"></td>
	           </tr>
           </tbody>
       </table>
    </div>

</@page>
