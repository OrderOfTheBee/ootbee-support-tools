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

<@page title=msg("log-settings.title") controller="/ootbee/admin" readonly=true customCSSFiles=["ootbee-support-tools/css/log-settings.css"]>
<#-- close the dummy form -->
</form>

<div class="column-full">
    <p class="intro">${msg("log-settings.intro-text")?html}</p>
    <@section label=msg("log-settings.logging") />

    <div class="column-full">
        <div>Add logger:
            <form id="addPackageForm" action="${url.service}" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
                <input name="logger" size="35" placeholder="logger-name"></input>
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
                <input type="submit" value="Add" style="margin-right:1em;" />
            </form>
            <@button id="tailRepoLog" label=msg("log-settings.tail") onclick=("Admin.showDialog('" + url.serviceContext + "/ootbee/admin/log4j-tail');")/>
            <@button id="toggleView" label=msg(showUnconfiguredLoggers?string('log-settings.hideUnconfigured', 'log-settings.showUnconfigured')) onclick=("window.location.href = '" + url.serviceContext + "/ootbee/admin/log4j-settings?showUnconfiguredLoggers="+ (showUnconfiguredLoggers!false)?string('false','true') + "';")/>
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
            </tr>
            <#list loggerStates as loggerState>
                <tr>
                    <td><#if loggerState.isRoot>${msg('log-settings.rootLogger')?html}<#else>${loggerState.name?html}</#if></td>
                    <td><#if loggerState.parentIsRoot>${msg('log-settings.rootLogger')?html}<#else>${(loggerState.parent!"")?html}</#if></td>
                    <td>${loggerState.additivity?string(msg("log-settings.column.additivity.true"), msg("log-settings.column.additivity.false"))?html}</td>
                    <td>
                        <form action="${url.service}" method="POST" enctype="multipart/form-data" accept-charset="utf-8">
                            <input type="hidden" name="logger" value="<#if loggerState.isRoot>-root-<#else>${loggerState.name?html}</#if>" />
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
                    <td><a href="#" onclick="Admin.showDialog('${url.serviceContext}/ootbee/admin/log4j-appenders?logger=<#if loggerState.isRoot>-root-<#else>${loggerState.name?url('UTF-8')}</#if>');">${msg("log-settings.appenderDetails")?html}</a></td>
                </tr>
            </#list>
        </table>
    </div>
</div>
</@page>