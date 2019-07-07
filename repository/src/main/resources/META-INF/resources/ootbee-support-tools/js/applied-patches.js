/**
 * Copyright (C) 2017 Axel Faust
 * Copyright (C) 2017 Order of the Bee
 * 
 * This file is part of Community Support Tools
 * 
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco Copyright
 * (C) 2005-2017 Alfresco Software Limited.
 */

/* global Admin: false, $: false, moment:false*/

/**
 * Applied Patches Component
 */
var AdminAP = {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    AdminAP.setupTable();
});

(function()
{
    AdminAP.setupTable = function()
    {
        var dataTableConfig = {
            paging : false,
            order : [ [ 2, 'asc' ] ],
            columnDefs : [ {
                className : 'numericalCellValue',
                targets : [ 4, 6, 7 ]
            }, {
                // these custom may all contain technical IDs that may block dynamic sizing
                className : 'wrapAnywhere',
                targets : [ 0, 1, 10 ]
            } ],
            columns : [ {
                width : '15%'
            }, {
                width : '25%'
            }, {
                render : function renderTimestamp(data)
                {
                    var renderedDate = moment().to(data);
                    renderedDate = '<div title="' + Admin.html(data) + '">' + Admin.html(renderedDate) + '</div>';
                    return renderedDate;
                },
                orderData : 3
            }, {
                visible : false
            }, {

            }, {

            }, {

            }, {

            }, {

            }, {

            }, {
                width : '15%'
            } ]
        };

        $('#appliedPatches').DataTable(dataTableConfig);
    };

}());