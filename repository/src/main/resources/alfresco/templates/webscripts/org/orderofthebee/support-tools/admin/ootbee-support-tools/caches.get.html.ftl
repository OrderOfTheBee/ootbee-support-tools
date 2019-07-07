<#-- 
Copyright (C) 2016 - 2018 Axel Faust
Copyright (C) 2016 - 2018 Order of the Bee

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
Copyright (C) 2005-2018 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<@page title=msg("caches.title") readonly=true customCSSFiles=["ootbee-support-tools/css/jquery.dataTables.css"]
    customJSFiles=["ootbee-support-tools/js/jquery-2.2.3.js", "ootbee-support-tools/js/jquery.dataTables.js", "ootbee-support-tools/js/caches.js"]>

<script type="text/javascript">//<![CDATA[
    AdminCA.setServiceContext('${url.serviceContext}');

    AdminCA.addMessages({
        'caches.clearCache.title' : '${msg("caches.clearCache.title")?js_string}',
        'caches.clearCache.label' : '${msg("caches.clearCache.label")?js_string}'
    });
//]]></script>

    <div class="column-full">
        <p class="intro">${msg("caches.intro")?html}</p>      

        <div class="buttons">
            <div class="column-left">
                <@button id="refreshCaches" label=msg("caches.refresh") onclick=("AdminCA.refreshCaches();") />
            </div>
        </div>

        <div class="control">
            <table id="caches-table" class="data results" width="100%">
                <thead>
                    <tr>
                        <th>${msg("caches.attr.name")?html}</th>
                        <th>${msg("caches.attr.type.alfresco")?html}</th>
                        <th>${msg("caches.attr.type.class")?html}</th>

                        <th>${msg("caches.attr.size")?html}</th>
                        <th>${msg("caches.attr.maxSize")?html}</th>

                        <th>${msg("caches.attr.cacheGets")?html}</th>
                        <th>${msg("caches.attr.cacheHits")?html}</th>
                        <th>${msg("caches.attr.cacheHitPercentage")?html}</th>
                        <th>${msg("caches.attr.cacheMisses")?html}</th>
                        <th>${msg("caches.attr.cacheMissPercentage")?html}</th>

                        <th>${msg("caches.attr.cacheEvictions")?html}</th>
                        <th></th>
                    </tr>
                </thead>
                <tfoot>
                    <tr>
                        <th>${msg("caches.attr.name")?html}</th>
                        <th>${msg("caches.attr.type.alfresco")?html}</th>
                        <th>${msg("caches.attr.type.class")?html}</th>

                        <th>${msg("caches.attr.size")?html}</th>
                        <th>${msg("caches.attr.maxSize")?html}</th>

                        <th>${msg("caches.attr.cacheGets")?html}</th>
                        <th>${msg("caches.attr.cacheHits")?html}</th>
                        <th>${msg("caches.attr.cacheHitPercentage")?html}</th>
                        <th>${msg("caches.attr.cacheMisses")?html}</th>
                        <th>${msg("caches.attr.cacheMissPercentage")?html}</th>

                        <th>${msg("caches.attr.cacheEvictions")?html}</th>
                        <th></th>
                    </tr>
                </tfoot>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
</@page>