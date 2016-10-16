/**
 * Copyright (C) 2016 Axel Faust / Markus Joos / Jens Goldhammer
 * Copyright (C) 2016 Order of the Bee
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
 * Linked to Alfresco Copyright (C) 2005-2016 Alfresco Software Limited.
 */

/* global Admin: false, el: false*/

// The AdminAS root object has been extracted from the Alfresco Support Tools
// admin-activesessions.get.html.ftl trim down page HTML sizes and promote clean
// separation of concerns

/**
 * Active Sessions Component
 */
var AdminSJ = AdminSJ || {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    setInterval(AdminSJ.updateStates, 2000);
});

(function()
{
    var serviceUrl, serviceContext, messages = {};

    AdminSJ.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };
    
    AdminSJ.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminSJ.addMessage = function addMessage(key, message)
    {
        messages[key] = message;
    };

    AdminSJ.updateStates = function updateStates()
    {

        Admin.request({
            method : "GET",
            url : serviceUrl + "-states",
            fnSuccess : function(res)
            {
                if (res.responseJSON)
                {
                    var json = res.responseJSON;
                    var table = el("jobs-table");
                    var runningJobs = json.runningJobs;

                    var jobRows = table.rows;

                    // i starting at 1 to jump over the table header!
                    for(var i=1;i<jobRows.length;i++){
                        var jobRow = jobRows[i];
                        var stateCell = jobRow.cells.namedItem("jobState");
                        var isRunning = runningJobs[jobRow.id];
                        if( isRunning){
                            stateCell.innerHTML=Admin.html(messages.running);
                        }else{
                            stateCell.innerHTML=Admin.html(messages.notRunning);
                        }
                    }

                }
            }
        });
    };

})();
