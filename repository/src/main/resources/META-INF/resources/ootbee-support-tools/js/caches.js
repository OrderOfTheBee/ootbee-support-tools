/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

/* global Admin: false, $: false*/

/**
 * System information Component
 */
var AdminCA = AdminCA || {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    AdminCA.setupTables();
});

(function()
{
    var serviceContext, dataTable, messages = {};

    AdminCA.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminCA.addMessages = function addMessage(oMessages)
    {
        var key;
        if (oMessages !== undefined && oMessages !== null)
        {
            for (key in oMessages)
            {
                if (oMessages.hasOwnProperty(key))
                {
                    messages[key] = oMessages[key];
                }
            }
        }
    };

    AdminCA.refreshCaches = function refreshCaches()
    {
        dataTable.ajax.reload();
    };

    AdminCA.setupTables = function setupTables()
    {
        var dataTableConfig;

        dataTableConfig = {
            ajax : {
                url : serviceContext + '/ootbee/admin/caches',
                dataSrc : 'caches',
                dataType : 'json'
            },
            paging : false,
            searching : false,
            autoWidth : false,
            columnDefs : [
                    {
                        orderable : false,
                        render : function renderClearanceLink(data, type, row)
                        {
                            var rendered = '';
                            if (data === true)
                            {
                                rendered = '<a href="#" onclick="AdminCA.clearCache(\'' + row.name + '\');" title="'
                                        + Admin.html(messages['caches.clearCache.title']) + '">'
                                        + Admin.html(messages['caches.clearCache.label']) + '</a>';
                            }
                            return rendered;
                        },
                        targets : [ 11 ]
                    }, {
                        className : 'numericalCellValue',
                        targets : [ 3, 4, 5, 6, 7, 8, 9, 10 ]
                    }, {
                        render : function renderName(data)
                        {
                            var renderedName = data;
                            if (/Cache$/.test(renderedName))
                            {
                                renderedName = renderedName.substring(0, renderedName.length - 5);
                            }
                            if (/Shared$/.test(renderedName))
                            {
                                renderedName = renderedName.substring(0, renderedName.length - 6);
                            }
                            if (renderedName !== data)
                            {
                                renderedName = '<div title="' + Admin.html(data) + '">' + Admin.html(renderedName) + '</div>';
                            }
                            return renderedName;
                        },
                        targets : [ 0 ]
                    }, {
                        render : function renderType(data, type, row)
                        {
                            return '<div title="' + Admin.html(row.type) + '">' + Admin.html(data) + '</div>';
                        },
                        targets : [ 2 ]
                    } ],
            columns : [ {
                data : 'name'
            }, {
                data : 'definedType'
            }, {
                data : 'shortType'
            }, {
                data : 'size'
            }, {
                data : 'maxSize',
                defaultContent : ''
            }, {
                data : 'cacheGets',
                defaultContent : ''
            }, {
                data : 'cacheHits',
                defaultContent : ''
            }, {
                data : 'cacheHitRate',
                defaultContent : ''
            }, {
                data : 'cacheMisses',
                defaultContent : ''
            }, {
                data : 'cacheMissRate',
                defaultContent : ''
            }, {
                data : 'cacheEvictions',
                defaultContent : ''
            }, {
                data : 'clearable'
            } ]
        };

        dataTable = $('#caches-table').DataTable(dataTableConfig);
    };

    AdminCA.clearCache = function clearCache(cacheName)
    {
        if (cacheName !== undefined && cacheName !== null)
        {
            Admin.request({
                url : serviceContext + '/ootbee/admin/caches/' + encodeURI(String(cacheName).replace(/\./, '%dot%')) + '/clear',
                method : 'POST',
                fnSuccess : function clearCache_success()
                {
                    AdminCA.refreshCaches();
                }
            });
        }
    };

})();
