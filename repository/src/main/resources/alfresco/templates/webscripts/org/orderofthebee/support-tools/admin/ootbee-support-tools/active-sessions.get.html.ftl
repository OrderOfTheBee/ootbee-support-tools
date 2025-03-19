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

<@page title=msg("activesessions.title") readonly=true customCSSFiles=["ootbee-support-tools/css/jquery.dataTables.css"]
    customJSFiles=["ootbee-support-tools/js/smoothie.js", "ootbee-support-tools/js/jquery-2.2.3.js", "ootbee-support-tools/js/jquery.dataTables.js", "ootbee-support-tools/js/active-sessions.js"]>

    <script type="text/javascript">//<![CDATA[
        AdminAS.setServiceContext('${url.serviceContext}');
        
        AdminAS.setInitialDBData({
            NumActive: ${connectionPoolData.numActive?c},
            MaxActive: ${connectionPoolData.maxActive?c},
            NumIdle: ${connectionPoolData.numIdle?c},
            userCountNonExpired: ${userSessionData.userCountNonExpired?c},
            TicketCountNonExpired: ${userSessionData.ticketCountNonExpired?c}
        });
        
        AdminAS.addMessage('activesessions.users.logoff', '${msg("activesessions.users.logoff")?js_string}');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("activesessions.intro-text")?html}</p>      
    </div>
    <@section label=msg("activesessions.database.database-connection")?html>
        <div class="column-left">
            <canvas id="database" width="350" height="150"></canvas>
    
            <@options id="dbTimescale" name="dbTimescale" label=msg("activesessions.chart-timescale") value="10">
                <@option label=msg("activesessions.chart-timescale.1min") value="1" />
                <@option label=msg("activesessions.chart-timescale.10mins") value="10" />
                <@option label=msg("activesessions.chart-timescale.60mins") value="60" />
                <@option label=msg("activesessions.chart-timescale.12hrs") value="720" />
                <@option label=msg("activesessions.chart-timescale.24hrs") value="1440" />
                <@option label=msg("activesessions.chart-timescale.48hrs") value="2880" />
                <@option label=msg("activesessions.chart-timescale.7days") value="10080" />
            </@options>
        </div>
        <div class="column-right">
            <@field value=connectionPoolData.driverClassName label=msg("activesessions.database.driver")?html />
            <@field value=connectionPoolData.url label=msg("activesessions.database.url")?html />
            <@field value=connectionPoolData.initialSize?c label=msg("activesessions.database.initialSize")?html />
            <@field value=connectionPoolData.maxActive?c label=msg("activesessions.database.maxActive")?html />
            <@field value=connectionPoolData.minIdle?c label=msg("activesessions.database.minIdle")?html />
            <@field value=connectionPoolData.maxIdle?c label=msg("activesessions.database.maxIdle")?html />
          
            <div class="control field">
                <div style="background: #7fff7f; width:0.6em; height:0.7em; border:1px solid #00ff00; display:inline-block;"></div>
                <span class="label">${msg("activesessions.database.numActive")?html}:</span>
                <span class="value" id="NumActive">${connectionPoolData.numActive?c}</span>
    	   </div>
    	   <div class="control field">
                <div style="background: #ffcc00; width:0.6em; height:0.7em; border:1px solid #ff9900; display:inline-block;"></div>
                <span class="label">${msg("activesessions.database.numIdle")?html}:</span>
                <span class="value" id="NumIdle">${connectionPoolData.numIdle?c}</span>
            </div>
        </div>
        <!-- used to deal with floating graphs to ensure section is high enough to contain all content -->
        <div class="column-full"></div>
    </@>
   
    <@section label=msg("activesessions.users.active-session-users")?html>
        <div class="column-left">
            <canvas id="users" width="350" height="150"></canvas>
    
            <@options id="userTimescale" name="userTimescale" label=msg("activesessions.chart-timescale") value="10">
                <@option label=msg("activesessions.chart-timescale.1min") value="1" />
                <@option label=msg("activesessions.chart-timescale.10mins") value="10" />
                <@option label=msg("activesessions.chart-timescale.60mins") value="60" />
                <@option label=msg("activesessions.chart-timescale.12hrs") value="720" />
                <@option label=msg("activesessions.chart-timescale.24hrs") value="1440" />
                <@option label=msg("activesessions.chart-timescale.48hrs") value="2880" />
                <@option label=msg("activesessions.chart-timescale.7days") value="10080" />
            </@options>
        </div>
        <div class="column-right">
            <div class="control field">
                <span class="label">${msg("activesessions.users.active-sessions")?html}</span><span class="label">:</span>
                <span class="value" id="TicketCountNonExpired">${userSessionData.ticketCountNonExpired?c}</span>
            </div>
            <div class="control field">
                <div style="background: #7f7fff; width:0.6em; height:0.7em; border:1px solid #0000ff; display:inline-block;"></div>
                <span class="label">${msg("activesessions.users.user-count")?html}:</span>
                <span class="value" id="UserCountNonExpired">${userSessionData.userCountNonExpired?c}</span>
            </div>
        </div>
        <!-- used to deal with floating graphs to ensure section is high enough to contain all content -->
        <div class="column-full"></div>
    </@>
        
    <@section label=msg("activesessions.users.active-users")?html>
        <div class="column-full" style="margin-bottom: 1ex;">
            <@button id="refreshUsers" label=msg("activesessions.users.refresh") onclick=("AdminAS.refreshUserTable();") />
        </div>

        <table id="users-table" class="data">
            <thead>
                <tr>
                    <th>${msg("activesessions.users.user")?html}</th>
                    <th>${msg("activesessions.users.first-name")?html}</th>
                    <th>${msg("activesessions.users.last-name")?html}</th>
                    <th>${msg("activesessions.users.email")?html}</th>
                    <th></th>
                </tr>
            </thead>
            <tfoot>
                <tr>
                    <th>${msg("activesessions.users.user")?html}</th>
                    <th>${msg("activesessions.users.first-name")?html}</th>
                    <th>${msg("activesessions.users.last-name")?html}</th>
                    <th>${msg("activesessions.users.email")?html}</th>
                    <th></th>
                </tr>
            </tfoot>
            <tbody>
            </tbody>
        </table>
    </@>
</@page>