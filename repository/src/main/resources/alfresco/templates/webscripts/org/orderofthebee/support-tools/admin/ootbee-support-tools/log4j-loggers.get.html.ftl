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

<@page title=msg("log-settings.title") controller="/ootbee/admin" readonly=true customJSFiles=["ootbee-support-tools/js/log-settings.js"] customCSSFiles=["ootbee-support-tools/css/log-settings.css"]>
<#-- close the dummy form -->
</form>

<script type="text/javascript">//<![CDATA[
    AdminLS.setServiceContext('${url.serviceContext}');
    AdminLS.addMessages({
        'log-settings.level.UNSET': '${msg("log-settings.level.UNSET")?js_string}',
        'log-settings.level.OFF': '${msg("log-settings.level.OFF")?js_string}',
        'log-settings.level.TRACE': '${msg("log-settings.level.TRACE")?js_string}',
        'log-settings.level.DEBUG': '${msg("log-settings.level.DEBUG")?js_string}',
        'log-settings.level.INFO': '${msg("log-settings.level.INFO")?js_string}',
        'log-settings.level.WARN': '${msg("log-settings.level.WARN")?js_string}',
        'log-settings.level.ERROR': '${msg("log-settings.level.ERROR")?js_string}',
        'log-settings.level.FATAL': '${msg("log-settings.level.FATAL")?js_string}',
        'log-settings.column.additivity.true': '${msg("log-settings.column.additivity.true")?js_string}',
        'log-settings.column.additivity.false': '${msg("log-settings.column.additivity.false")?js_string}',
        'log-settings.appenderDetails': '${msg("log-settings.appenderDetails")?js_string}',
        'log-settings.reset': '${msg("log-settings.reset")?js_string}'
    });
//]]></script>

<div class="column-full">
    <p class="intro">${msg("log-settings.intro-text")?html}</p>
    <@section label=msg("log-settings.logging") />

    <div class="column-full">
        <div>${msg("log-settings.column.addLogger")}:
            <form onsubmit="return AdminLS.submitNewLogger(event);" accept-charset="utf-8">
                <input id="newLoggerName" type="text" name="logger" size="35" placeholder="logger-name"></input>
                <select id="newLoggerLevel" name="level">
                    <option class="setting-UNSET" value="UNSET">${msg("log-settings.level.UNSET")?html}</option>
                    <option class="setting-OFF"   value="OFF">${msg("log-settings.level.OFF")?html}</option>
                    <option class="setting-TRACE" value="TRACE">${msg("log-settings.level.TRACE")?html}</option>
                    <option class="setting-DEBUG" value="DEBUG">${msg("log-settings.level.DEBUG")?html}</option>
                    <option class="setting-INFO"  value="INFO">${msg("log-settings.level.INFO")?html}</option>
                    <option class="setting-WARN"  value="WARN">${msg("log-settings.level.WARN")?html}</option>
                    <option class="setting-ERROR" value="ERROR">${msg("log-settings.level.ERROR")?html}</option>
                    <option class="setting-FATAL" value="FATAL">${msg("log-settings.level.FATAL")?html}</option>
                </select>
                <input type="submit" value="${msg("log-settings.column.add")}" style="margin-right:1em;" />
            </form>
            <div class="buttons">
                <div class="column-left">
                    <@button id="tailRepoLog" label=msg("log-settings.tail") onclick=("Admin.showDialog('" + url.serviceContext + "/ootbee/admin/log4j-tail');")/>
                    <@button id="showLogFiles" label=msg("log-settings.logFiles") onclick=("Admin.showDialog('" + url.serviceContext + "/ootbee/admin/log4j-log-files');")/>
                    <@button id="resetLogSettings" label=msg("log-settings.resetAll") onclick=("AdminLS.resetLogLevel();")/>
                    <@button id="showUnconfiguredLoggers" label=msg('log-settings.showUnconfigured') onclick=("AdminLS.toggleShowUnconfiguredLoggers();") />
                    <@button id="hideUnconfiguredLoggers" class="hidden inline" label=msg('log-settings.hideUnconfigured') onclick=("AdminLS.toggleShowUnconfiguredLoggers();") />
                </div>
                <div class="column-right">
                    <@button id="startLogSnapshot" label=msg("log-settings.startLogSnapshot") onclick=("AdminLS.startLogSnapshot();")/>
                    <@button id="stopLogSnapshot" class="hidden inline" label=msg("log-settings.stopLogSnapshot") onclick=("AdminLS.stopLogSnapshot();")/>
                    <@button id="lapLogSnapshot" class="hidden inline" label=msg("log-settings.lapLogSnapshot") onclick=("AdminLS.lapLogSnapshot();")/>
                    <input id="lapMessageLogSnapshot" type="text" size="35" class="hidden" placeholder="${msg("log-settings.lapMessageLogSnapshot")}" onkeyup="return AdminLS.handleLogMessageLogSnapshotKeyUp(event);"></input>
                </div>
            </div>
        </div>

        <div id="loadingMessage">${msg("log-settings.loading")?html}</div>
        <table id="loggerTable" class="results log4jsettings hidden">
            <thead>
                <tr>
                    <th><b>${msg("log-settings.column.loggerName")}</b></th>
                    <th><b>${msg("log-settings.column.parentLoggerName")}</b></th>
                    <th><b>${msg("log-settings.column.additivity")}</b></th>
                    <th><b>${msg("log-settings.column.setting")}</b></th>
                    <th><b>${msg("log-settings.column.effectiveValue")}</b></th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody id="loggerTableBody"></tbody>
        </table>
    </div>
</div>
</@page>

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