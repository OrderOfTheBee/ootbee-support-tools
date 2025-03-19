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

<@page title=msg("log-tail.title" ) dialog=true customJSFiles=["ootbee-support-tools/js/log-tail.js"] customCSSFiles=["ootbee-support-tools/css/log-tail.css"]>

    <script type="text/javascript">//<![CDATA[
        AdminTL.setServiceUrl('${url.service}');
        AdminTL.setAppenderUUID('${uuid}');
    //]]></script>

	<div id="loggrid" class="datagrid" >
        <table>
            <thead>
                <tr>
                    <th>${msg("log-tail.timestamp")?html}</th>
                    <th>${msg("log-tail.level")?html}</th>
                    <th>${msg("log-tail.logger")?html}</th>
                    <th>${msg("log-tail.message")?html}</th>
                </tr>
            </thead>
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
