<#-- 
Copyright (C) 2017 Axel Faust / Markus Joos / Michael Bui / Bindu Wavell
Copyright (C) 2017 Order of the Bee

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
Copyright (C) 2005-2017 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<@page title=msg("log-settings.title") controller="/ootbee/admin" readonly=true customJSFiles=["ootbee-support-tools/js/log-settings.js"] customCSSFiles=["ootbee-support-tools/css/log-settings.css"]>
<#-- close the dummy form -->
</form>

<script type="text/javascript">//<![CDATA[
    AdminLS.setServiceContext('${url.serviceContext}');
//]]></script>

<div class="column-full">
    <p class="intro">${msg("log-settings.intro-text")?html}</p>
    <@section label=msg("log-settings.logging") />

    <div class="column-full">
        <div>${msg("log-settings.column.addLogger")}:
            <form id="addPackageForm" action="${url.service}" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
                <input type="text" name="logger" size="35" placeholder="logger-name"></input>
                <input type="hidden" name="showUnconfiguredLoggers" value="${(showUnconfiguredLoggers!false)?string}" />
                <select name="level">
                    <option                       value="UNSET">${msg("log-settings.level.UNSET")?html}</option>
                    <option class="setting-OFF"   value="OFF">${msg("log-settings.level.OFF")?html}</option>
                    <option class="setting-TRACE" value="TRACE">${msg("log-settings.level.TRACE")?html}</option>
                    <option class="setting-DEBUG" value="DEBUG">${msg("log-settings.level.DEBUG")?html}</option>
                    <option                       value="INFO">${msg("log-settings.level.INFO")?html}</option>
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
                    <@button id="toggleView" label=msg(showUnconfiguredLoggers?string('log-settings.hideUnconfigured', 'log-settings.showUnconfigured')) onclick=("window.location.href = '" + url.serviceContext + "/ootbee/admin/log4j-loggers?showUnconfiguredLoggers="+ (showUnconfiguredLoggers!false)?string('false','true') + "';")/>
                </div>
                <div class="column-right">
                    <@button id="startLogSnapshot" label=msg("log-settings.startLogSnapshot") onclick=("AdminLS.startLogSnapshot();")/>
                    <@button id="stopLogSnapshot" style="display:none" label=msg("log-settings.stopLogSnapshot") onclick=("AdminLS.stopLogSnapshot();")/>
                    <@button id="lapLogSnapshot" style="display:none" label=msg("log-settings.lapLogSnapshot") onclick=("AdminLS.lapLogSnapshot();")/>
                    <input id="lapMessageLogSnapshot" type="text" size="35" style="display:none" placeholder="${msg("log-settings.lapMessageLogSnapshot")}" onkeyup="return AdminLS.handleLogMessageLogSnapshotKeyUp(event);"></input>
                </div>
            </div>
        <#if statusMessage?? && statusMessage != "">
            <div id="statusmessage" class="message ${messageStatus!""}">${.now?string("HH:mm:ss")} - ${statusMessage?html!""} <a href="#" onclick="this.parentElement.style.display='none';" title="${msg("admin-console.close")}">[X]</a></div>
        </#if>
        </div>
      
        <table class="results log4jsettings">
            <tr>
                <th><b>${msg("log-settings.column.loggerName")}</b></th>
                <th><b>${msg("log-settings.column.parentLoggerName")}</b></th>
                <th><b>${msg("log-settings.column.additivity")}</b></th>
                <th><b>${msg("log-settings.column.setting")}</b></th>
                <th><b>${msg("log-settings.column.effectiveValue")}</b></th>
                <th></th>
                <th></th>
            </tr>
            <#list loggerStates as loggerState>
                <tr>
                    <td title="<#if loggerState.isRoot>${msg('log-settings.rootLogger')?html}<#else>${loggerState.name?html}</#if>">${compressName(loggerState.name, loggerState.isRoot)?html}</td>
                    <td title="<#if loggerState.parentIsRoot>${msg('log-settings.rootLogger')?html}<#else>${loggerState.parent!''?html}</#if>">${compressName(loggerState.parent!'', loggerState.parentIsRoot)?html}</td>
                    <td>${loggerState.additivity?string(msg("log-settings.column.additivity.true"), msg("log-settings.column.additivity.false"))?html}</td>
                    <td>
                        <form action="${url.service}/<#if loggerState.isRoot>-root-<#else>${loggerState.name?replace('.', '%dot%')?url('UTF-8')}</#if>" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
                            <input type="hidden" name="showUnconfiguredLoggers" value="${showUnconfiguredLoggers?string}" />
                            <select name="level" onchange="this.form.submit();">
                                <option value="" <#if loggerState.level?? == false>selected</#if>>${msg("log-settings.level.UNSET")?html}</option>
                                <option class="setting-OFF"   value="OFF" <#if loggerState.level?? && loggerState.level == "OFF">selected</#if>>${msg("log-settings.level.OFF")?html}</option>
                                <option class="setting-TRACE" value="TRACE" <#if loggerState.level?? && loggerState.level == "TRACE">selected</#if>>${msg("log-settings.level.TRACE")?html}</option>
                                <option class="setting-DEBUG" value="DEBUG" <#if loggerState.level?? && loggerState.level == "DEBUG">selected</#if>>${msg("log-settings.level.DEBUG")?html}</option>
                                <option                       value="INFO" <#if loggerState.level?? && loggerState.level == "INFO">selected</#if>>${msg("log-settings.level.INFO")?html}</option>
                                <option class="setting-WARN"  value="WARN" <#if loggerState.level?? && loggerState.level == "WARN">selected</#if>>${msg("log-settings.level.WARN")?html}</option>
                                <option class="setting-ERROR" value="ERROR" <#if loggerState.level?? && loggerState.level == "ERROR">selected</#if>>${msg("log-settings.level.ERROR")?html}</option>
                                <option class="setting-FATAL" value="FATAL" <#if loggerState.level?? && loggerState.level == "FATAL">selected</#if>>${msg("log-settings.level.FATAL")?html}</option>
                            </select>
                        </form>
                    </td>
                    <td class="effectiveLevel setting-${loggerState.effectiveLevel}">${msg("log-settings.level." + loggerState.effectiveLevel)?html}</td>
                    <td><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/log4j-appenders?logger=<#if loggerState.isRoot>-root-<#else>${loggerState.name?url('UTF-8')?js_string}</#if>');">${msg("log-settings.appenderDetails")?html}</a></td>
                    <td><#if loggerState.canBeReset><a href="#" onclick="AdminLS.resetLogLevel('<#if loggerState.isRoot>-root-<#else>${loggerState.name?js_string}</#if>');">${msg("log-settings.reset")?html}</a></#if></td>
                </tr>
            </#list>
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