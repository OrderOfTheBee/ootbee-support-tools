/**
 * Copyright (C) 2016 - 2018 Axel Faust / Markus Joos / Jens Goldhammer
 * Copyright (C) 2016 - 2018 Order of the Bee
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
 * (C) 2005-2018 Alfresco Software Limited.
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
    var serviceUrl, messages = {}, lastStateResponseText;

    AdminSJ.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminSJ.addMessage = function addMessage(key, message)
    {
        messages[key] = message;
    };

    AdminSJ.adaptTimes = function (){
        var table, jobRows, i, jobRow, startTimeCell, previousFireCell, nextFireCell;
        
        table = el("jobs-table");
        jobRows = table.rows;

        // i starting at 1 to jump over the table header!
        for (i = 1; i < jobRows.length; i++){
            jobRow = jobRows[i];
            startTimeCell = jobRow.cells.namedItem("jobStartTime");
            previousFireCell = jobRow.cells.namedItem("jobPreviousFire");
            nextFireCell = jobRow.cells.namedItem("jobNextFire");

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
                var json, table, runningJobs, runningJobQuickLookup, jobIdx, jobName, jobGroup, jobRows, rowIdx, jobRow, nameCell, groupCell, stateCell, isRunning;
                // prevent overly excessive DOM updates by checking if there actually is a change to the previous state(s)
                if (res.responseJSON && lastStateResponseText !== res.responseText)
                {
                    json = res.responseJSON;
                    table = el("jobs-table");
                    runningJobs = json.runningJobs;
                    runningJobQuickLookup = {};
                    for (jobIdx = 0; jobIdx < runningJobs.length; jobIdx++)
                    {
                        jobName = runningJobs[jobIdx].jobName;
                        jobGroup = runningJobs[jobIdx].jobGroup;
                        // use html-encoded names as we only have innerHTML for lookup
                        runningJobQuickLookup[Admin.html(jobName) + '-' + Admin.html(jobGroup)] = true;
                    }

                    jobRows = table.rows;

                    // i starting at 1 to jump over the table header!
                    for (rowIdx = 1; rowIdx < jobRows.length; rowIdx++)
                    {
                        jobRow = jobRows[rowIdx];
                        nameCell = jobRow.cells.namedItem("jobName");
                        jobName = nameCell.innerHTML;
                        groupCell = jobRow.cells.namedItem("jobGroup");
                        jobGroup = groupCell.innerHTML;
                        stateCell = jobRow.cells.namedItem("jobState");
                        isRunning = runningJobQuickLookup[Admin.html(jobName) + '-' + Admin.html(jobGroup)] === true;
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
                lastStateResponseText = res.responseText;
            }
        });
    };

    AdminSJ.pauseTrigger = function pauseTrigger(triggerName, triggerGroup)
    {
        if (triggerName !== undefined && triggerName !== null && triggerGroup !== undefined && triggerGroup !== null)
        {
            Admin.request({
                url : serviceUrl + '/triggers/' + encodeURI(triggerGroup) + '/' + encodeURI(triggerName)
                        + '/pause',
                method : 'POST',
                fnSuccess : function pauseTrigger_success()
                {
                    window.location.reload();
                }
            });
        }
    };

    AdminSJ.resumeTrigger = function resumeTrigger(triggerName, triggerGroup)
    {
        if (triggerName !== undefined && triggerName !== null && triggerGroup !== undefined && triggerGroup !== null)
        {
            Admin.request({
                url : serviceUrl + '/triggers/' + encodeURI(triggerGroup) + '/' + encodeURI(triggerName)
                        + '/resume',
                method : 'POST',
                fnSuccess : function resumeTrigger_success()
                {
                    window.location.reload();
                }
            });
        }
    };
})();