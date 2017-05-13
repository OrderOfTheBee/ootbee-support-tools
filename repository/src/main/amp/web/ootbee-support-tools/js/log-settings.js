/**
 * Copyright (C) 2017 Axel Faust / Markus Joos / Michael Bui / Bindu Wavell
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

/* global Admin: false */

/**
 * Admin Log Settings
 */
var AdminLS = AdminLS || {};

(function()
{
    var KEYCODE_ENTER = 13;
    var KEYCODE_ESC = 27;

    var serviceContext, snapshotLogFile, snapshotLapNumber;
    
    AdminLS.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminLS.resetLogLevel = function resetLogLevel(loggerName)
    {
        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-loggers'
                    + (loggerName !== undefined ? ('/' + encodeURI(String(loggerName).replace(/\./, '%dot%'))) : ''),
            method : 'DELETE',
            fnSuccess : function resetLogLevel_success()
            {
                location.reload(true);
            }
        });
    };
    
    AdminLS.startLogSnapshot = function startLogSnapshot()
    {
        Admin.request({
          url : serviceContext + '/ootbee/admin/log4j-snapshot-create',
          method : 'GET',
          fnSuccess : function startLogSnapshot_success(res)
          {
              if (res.responseJSON)
              {
                  snapshotLogFile = res.responseJSON.snapshotLogFile;
                  document.getElementById("startLogSnapshot").style.display = 'none';
                  document.getElementById("stopLogSnapshot").style.display = 'inline';
                  document.getElementById("lapLogSnapshot").style.display = 'inline';
                  document.getElementById("lapMessageLogSnapshot").style.display = 'inline';
                  document.getElementById("lapMessageLogSnapshot").focus();
                  snapshotLapNumber = 1;
              }
          }
        });
    };
    
    AdminLS.stopLogSnapshot = function stopLogSnapshot()
    {
        window.open(serviceContext + '/ootbee/admin/log4j-snapshot-complete/'+snapshotLogFile+'?a=true','_blank');
        document.getElementById("startLogSnapshot").style.display = 'inline';
        document.getElementById("stopLogSnapshot").style.display = 'none';
        document.getElementById("lapLogSnapshot").style.display = 'none';
        document.getElementById("lapMessageLogSnapshot").style.display = 'none';
    };

    AdminLS.lapLogSnapshot = function lapLogSnapshot()
    {
        var inputEl = document.getElementById("lapMessageLogSnapshot");
        var message = inputEl.value;
        if (!message)
        {
            message = snapshotLapNumber++;
        }
        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-snapshot-lap?message=' + message,
            method : 'POST',
            fnSuccess : function lapLogSnapshot_success()
            {
                inputEl.value = '';
                inputEl.focus();
            }
        });
    };

    AdminLS.handleLogMessageLogSnapshotKeyUp = function handleLogMessageLogSnapshotKeyUp(event)
    {
        if (event.keyCode === KEYCODE_ENTER) {
            event.preventDefault();
            document.getElementById("lapLogSnapshot").click();
            return false;
        }
        if (event.keyCode === KEYCODE_ESC) {
            event.preventDefault();
            document.getElementById("stopLogSnapshot").click();
            return false;
        }
        return true;
    };
}());
