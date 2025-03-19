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

/* global Admin: false, el: false */

// The AdminTL root object has been extracted from the Alfresco Support Tools
// admin-log-settings-tail.html.ftl trim down page HTML sizes and promote clean
// separation of concerns

/**
 * Admin Tail Log
 */
var AdminTL = AdminTL || {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    window.scrollTo(0, document.body.scrollHeight);
    el("myinterval").value = 5;

    AdminTL.startTimer();
    AdminTL.getLogEvents();
});

(function()
{
    var serviceUrl, uuid;

    AdminTL.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminTL.setAppenderUUID = function setAppenderUUID(appenderUuid)
    {
        uuid = appenderUuid;
    };

    AdminTL.checkCounter = function checkCounter()
    {
        if (el("countdown").value <= 0)
        {
            AdminTL.getLogEvents();
            el("countdown").value = el("myinterval").value;
        }
        else
        {
            el("countdown").value = el("countdown").value - 1;
        }
    };

    AdminTL.closeDialog = function closeDialog()
    {
        AdminTL.stopTimer();
        top.window.Admin.removeDialog();
    };

    AdminTL.startTimer = function startTimer()
    {
        el("starttimer").hidden = true;
        el("stoptimer").hidden = false;
        el("countdown").value = el("myinterval").value;
        AdminTL.mytimer = setInterval(function()
        {
            AdminTL.checkCounter();
        }, 1000);
    };

    AdminTL.stopTimer = function stopTimer()
    {
        clearInterval(AdminTL.mytimer);
        el("stoptimer").hidden = true;
        el("starttimer").hidden = false;
    };

    AdminTL.switchMode = function switchMode()
    {
        if (document.getElementById('textonlybox').style.display === 'none')
        {
            document.getElementById('loggrid').style.display = 'none';
            document.getElementById('textonlybox').style.display = 'block';
        }
        else
        {
            document.getElementById('loggrid').style.display = 'block';
            document.getElementById('textonlybox').style.display = 'none';
            window.scrollTo(0, document.body.scrollHeight);
        }
    };

    AdminTL.getLogEvents = function getLogEvents()
    {
        Admin.request({
            url : serviceUrl + '-events?uuid=' + uuid,
            requestContentType : 'application/json',
            responseContentType : 'application/json',
            fnSuccess : function getLogEvents_success(res)
            {
                var json, logFragment, logMessage, i, logGridBody, logArea, logRow, logCell;
                if (res.responseText)
                {
                    json = JSON.parse(res.responseText);

                    if (json.events.length > 0)
                    {
                        logFragment = document.createDocumentFragment();
                        logMessage = '';

                        for (i = 0; i < json.events.length; i++)
                        {
                            logMessage += '\n';
                            logRow = document.createElement('tr');
                            Admin.addClass(logRow, 'logMessage-' + json.events[i].level);

                            logMessage += json.events[i].timestamp.nice;
                            logCell = document.createElement('td');
                            logCell.innerHTML = Admin.html(json.events[i].timestamp.nice);
                            logRow.appendChild(logCell);
                            
                            logMessage += ' ' + json.events[i].level;
                            logCell = document.createElement('td');
                            logCell.innerHTML = Admin.html(json.events[i].level);
                            logRow.appendChild(logCell);
                            
                            logCell = document.createElement('td');
                            logCell.innerHTML = Admin.html(json.events[i].loggerCompressedName);
                            logCell.setAttribute('title', json.events[i].loggerName);
                            logRow.appendChild(logCell);
                            logMessage += ' [' + json.events[i].loggerCompressedName + ']';

                            logMessage += ' ' + json.events[i].message;
                            logCell = document.createElement('td');
                            logCell.innerHTML = Admin.html(json.events[i].message);
                            logRow.appendChild(logCell);

                            logFragment.appendChild(logRow);
                        }

                        logGridBody = el('loggridBody');
                        logGridBody.appendChild(logFragment);

                        logArea = el('textareaLog');
                        logArea.value = logArea.value + logMessage;
                    }
                }
            }
        });
    };
}());
