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

/* global Admin: false, el: false, moment:false*/

/**
 * Scheduled Jobs Component
 */
var AdminSJ = AdminSJ || {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    AdminSJ.adaptTimes();
    setInterval(AdminSJ.updateStates, 2000);
});

(function()
{
    var serviceUrl, messages = {};

    AdminSJ.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminSJ.addMessage = function addMessage(key, message)
    {
        messages[key] = message;
    };

    AdminSJ.adaptTimes = function (){
        var table, jobRows, i;
        
        table = el("jobs-table");
        jobRows = table.rows;

        // i starting at 1 to jump over the table header!
        for (i = 1; i < jobRows.length; i++){
            var jobRow = jobRows[i];
            var startTimeCell = jobRow.cells.namedItem("jobStartTime");
            var previousFireCell = jobRow.cells.namedItem("jobPreviousFire");
            var nextFireCell = jobRow.cells.namedItem("jobNextFire");

            startTimeCell.title = moment().to(startTimeCell.innerHTML);

            if (previousFireCell.innerHTML){
                previousFireCell.title = moment().to(previousFireCell.innerHTML);
            }

            if (nextFireCell.innerHTML){
                nextFireCell.title = moment().to(nextFireCell.innerHTML);
            }

        }

    };

    AdminSJ.updateStates = function updateStates()
    {
        Admin.request({
            method : "GET",
            url : serviceUrl + "-states",
            fnSuccess : function(res)
            {
                var json, table, runningJobs, jobRows, i, jobRow, stateCell, isRunning;
                if (res.responseJSON)
                {
                    json = res.responseJSON;
                    table = el("jobs-table");
                    runningJobs = json.runningJobs;

                    jobRows = table.rows;

                    // i starting at 1 to jump over the table header!
                    for (i = 1; i < jobRows.length; i++)
                    {
                        jobRow = jobRows[i];
                        stateCell = jobRow.cells.namedItem("jobState");
                        isRunning = runningJobs[jobRow.id];
                        if (isRunning)
                        {
                            stateCell.innerHTML = Admin.html(messages.running);
                        }
                        else
                        {
                            stateCell.innerHTML = Admin.html(messages.notRunning);
                        }
                    }

                }
            }
        });
    };

})();