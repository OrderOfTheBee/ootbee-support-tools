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

<@page title=msg("appliedPatches.title") readonly=true customCSSFiles=["ootbee-support-tools/css/jquery.dataTables.css"]
    customJSFiles=["ootbee-support-tools/js/jquery-2.2.3.js", "ootbee-support-tools/js/jquery.dataTables.js", "ootbee-support-tools/js/moment-with-locales.min.js", "ootbee-support-tools/js/applied-patches.js"]>

    <div class="column-full">
        <p class="intro">${msg("appliedPatches.intro")?html}</p>      
  
        <div class="section">
            <table id="appliedPatches" class="data results" cellspacing="0" width="100%">
                <thead>
                    <tr>
                        <th>${msg("appliedPatches.id")?html}</th>
                        <th>${msg("appliedPatches.description")?html}</th>
                        <th>${msg("appliedPatches.appliedOnDate")?html}</th>
                        <th style="display:none;"></th>
                        <th>${msg("appliedPatches.appliedToSchema")?html}</th>
                        <th>${msg("appliedPatches.appliedToServer")?html}</th>
                        <th>${msg("appliedPatches.fixesFromSchema")?html}</th>
                        <th>${msg("appliedPatches.fixesToSchema")?html}</th>
                        <th>${msg("appliedPatches.wasExecuted")?html}</th>
                        <th>${msg("appliedPatches.succeeded")?html}</th>
                        <th>${msg("appliedPatches.report")?html}</th>
                    </tr>
                </thead>
                <tbody>
                    <#list appliedPatches as appliedPatch>
                        <tr>
                            <td>${appliedPatch.id}</td>
                            <td>${appliedPatch.description!""}</td>
                            <td>${xmldate(appliedPatch.appliedOnDate)}</td>
                            <td style="display:none;">${xmldate(appliedPatch.appliedOnDate)}</td>
                            <td>${appliedPatch.appliedToSchema?c}</td>
                            <td>${appliedPatch.appliedToServer!""}</td>
                            <td>${appliedPatch.fixesFromSchema?c}</td>
                            <td>${appliedPatch.fixesToSchema?c}</td>
                            <td>${msg(appliedPatch.wasExecuted?string("boolean.yes", "boolean.no"))}</td>
                            <td>${msg(appliedPatch.succeeded?string("boolean.yes", "boolean.no"))}</td>
                            <td>${appliedPatch.report!""}</td>
                        </tr>
                    </#list>
                </tbody>
            </table>
        </div>
    </div>
</@page>