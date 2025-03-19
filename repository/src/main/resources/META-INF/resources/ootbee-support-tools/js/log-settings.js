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

/**
 * Admin Log Settings
 */
var AdminLS = AdminLS || {};

Admin.addEventListener(window, 'load', function()
{
    AdminLS.refreshLoggers();
});

(function()
{
    var KEYCODE_ENTER = 13;
    var KEYCODE_ESC = 27;

    var serviceContext, snapshotUUID, snapshotLapNumber, showUnconfiguredLoggers = false, messages = {};

    AdminLS.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminLS.addMessages = function addMessage(oMessages)
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

    AdminLS.toggleShowUnconfiguredLoggers = function toggleShowUnconfiguredLoggers()
    {
        showUnconfiguredLoggers = !showUnconfiguredLoggers;
        Admin.toggleHiddenElement(el('showUnconfiguredLoggers'));
        Admin.toggleHiddenElement(el('hideUnconfiguredLoggers'));
        AdminLS.refreshLoggers();
    };

    AdminLS.submitNewLogger = function submitNewLogger()
    {
        var loggerName, level;

        loggerName = el('newLoggerName').value;
        level = el('newLoggerLevel').value;

        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-loggers',
            method : 'POST',
            data : {
                logger : loggerName,
                level : level
            },
            fnSuccess : function submitNewLogger_success()
            {
                el('newLoggerName').value = '';
                AdminLS.refreshLoggers();
            }
        });

        return false;
    };

    function createAppenderDetailsOnClick(logger)
    {
        var loggerUrlName = logger.urlName, fn = function appenderDetailsOnClick()
        {
            Admin.showDialog(serviceContext + '/ootbee/admin/log4j-appenders?logger=' + loggerUrlName);
        };
        return fn;
    }

    function createLogLevelResetOnClick(logger)
    {
        var loggerUrlName = logger.urlName, fn = function logLevelResetOnClick()
        {
            AdminLS.resetLogLevel(loggerUrlName);
        };
        return fn;
    }

    function createLogLevelOnChange(logger)
    {
        var loggerName = logger.name, loggerUrlName = logger.urlName, fn = function logLevelOnChange(event)
        {
            var newValue = event.target.value;

            Admin.request({
                url : serviceContext + '/ootbee/admin/log4j-loggers/' + loggerUrlName,
                method : 'PUT',
                data : {
                    logger : loggerName,
                    level : newValue
                },
                fnSuccess : function logLevelOnChange_success()
                {
                    AdminLS.refreshLoggers();
                }
            });
        };
        return fn;
    }

    AdminLS.refreshLoggers = function refreshLoggers()
    {
        var loadingMessage, table;

        loadingMessage = el('loadingMessage');
        table = el('loggerTable');

        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-loggers?showUnconfiguredLoggers=' + String(showUnconfiguredLoggers),
            method : 'GET',
            fnSuccess : function refreshLoggers_success(res)
            {
                var json, i, j, logger, loggersBody, loggerRow, loggerCell, select, options, option, anchor, oldLoggerBody;

                json = res.responseJSON ? res.responseJSON : JSON.parse(res.responseText);

                loggersBody = document.createElement('tbody');
                loggersBody.id = 'loggerTableBody';

                for (i = 0; i < json.loggers.length; i++)
                {
                    logger = json.loggers[i];
                    loggerRow = document.createElement('tr');
                    loggersBody.appendChild(loggerRow);

                    loggerCell = document.createElement('td');
                    loggerCell.setAttribute('title', logger.displayName);
                    loggerCell.innerHTML = Admin.html(logger.shortDisplayName);
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    if (logger.parent !== undefined)
                    {
                        loggerCell.setAttribute('title', logger.parent.displayName);
                        loggerCell.innerHTML = Admin.html(logger.parent.shortDisplayName);
                    }
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    loggerCell.innerHTML = Admin.html(messages['log-settings.column.additivity.' + String(logger.additivity === 'true')]);
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    select = document.createElement('select');
                    options = [ 'UNSET', 'OFF', 'TRACE', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL' ];
                    for (j = 0; j < options.length; j++)
                    {
                        option = document.createElement('option');
                        option.innerHTML = Admin.html(messages['log-settings.level.' + options[j]]);
                        option.value = options[j];
                        Admin.addClass(option, 'setting-' + options[j]);
                        option.selected = logger.level === options[j];
                        select.add(option);
                    }
                    Admin.addEventListener(select, 'change', createLogLevelOnChange(logger));
                    loggerCell.appendChild(select);
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    Admin.addClass(loggerCell, 'effectiveLevel');
                    Admin.addClass(loggerCell, 'setting-' + logger.effectiveLevel);
                    loggerCell.innerHTML = Admin.html(messages['log-settings.level.' + logger.effectiveLevel]);
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    anchor = document.createElement('a');
                    Admin.addEventListener(anchor, 'click', createAppenderDetailsOnClick(logger));
                    anchor.innerHTML = Admin.html(messages['log-settings.appenderDetails']);
                    loggerCell.appendChild(anchor);
                    loggerRow.appendChild(loggerCell);

                    loggerCell = document.createElement('td');
                    if (logger.canBeReset === 'true')
                    {
                        anchor = document.createElement('a');
                        Admin.addEventListener(anchor, 'click', createLogLevelResetOnClick(logger));
                        anchor.innerHTML = Admin.html(messages['log-settings.reset']);
                        loggerCell.appendChild(anchor);
                    }
                    loggerRow.appendChild(loggerCell);
                }

                oldLoggerBody = el('loggerTableBody');
                oldLoggerBody.parentNode.replaceChild(loggersBody, oldLoggerBody);

                Admin.addClass(loadingMessage, 'hidden');
                Admin.removeClass(table, 'hidden');
            }
        });

        Admin.addClass(table, 'hidden');
        Admin.removeClass(loadingMessage, 'hidden');
    };

    AdminLS.resetLogLevel = function resetLogLevel(loggerUrlName)
    {
        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-loggers' + (loggerUrlName !== undefined ? ('/' + loggerUrlName) : ''),
            method : 'DELETE',
            fnSuccess : function resetLogLevel_success()
            {
                AdminLS.refreshLoggers();
            }
        });
    };

    AdminLS.startLogSnapshot = function startLogSnapshot()
    {
        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-snapshots',
            method : 'POST',
            fnSuccess : function startLogSnapshot_success(res)
            {
                if (res.responseJSON)
                {
                    snapshotUUID = res.responseJSON.snapshotUUID;
                    Admin.addClass(el('startLogSnapshot'), 'hidden');
                    Admin.removeClass(el('stopLogSnapshot'), 'hidden');
                    Admin.removeClass(el('lapLogSnapshot'), 'hidden');
                    Admin.removeClass(el('lapMessageLogSnapshot'), 'hidden');
                    el('lapMessageLogSnapshot').focus();
                    snapshotLapNumber = 1;
                }
            }
        });
    };

    AdminLS.stopLogSnapshot = function stopLogSnapshot()
    {
        window.open(serviceContext + '/ootbee/admin/log4j-snapshots/' + encodeURIComponent(snapshotUUID) + '?a=true', '_blank');
        Admin.removeClass(el('startLogSnapshot'), 'hidden');
        Admin.addClass(el('stopLogSnapshot'), 'hidden');
        Admin.addClass(el('lapLogSnapshot'), 'hidden');
        Admin.addClass(el('lapMessageLogSnapshot'), 'hidden');
        snapshotUUID = null;
    };

    AdminLS.lapLogSnapshot = function lapLogSnapshot()
    {
        var inputEl = el('lapMessageLogSnapshot');
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
        if (event.keyCode === KEYCODE_ENTER)
        {
            event.preventDefault();
            el('lapLogSnapshot').click();
            return false;
        }
        if (event.keyCode === KEYCODE_ESC)
        {
            event.preventDefault();
            el('stopLogSnapshot').click();
            return false;
        }
        return true;
    };
}());
