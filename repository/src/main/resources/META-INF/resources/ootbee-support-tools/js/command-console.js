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

/* global Admin: false, el: false, alert: false */

var AdminCC = AdminCC || {};

(function()
{
    var serviceContext, messages = {}, activeCommandConsolePlugin = 'global', lastTriggeredRequestTimestamp;

    AdminCC.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminCC.addMessages = function addMessage(oMessages)
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

    AdminCC.submitConsoleCommand = function submitConsoleCommand()
    {
        var commandInput, commandFragments, plugin, command, commandArgs, specialCommandSelected;

        commandInput = el('command-console-command').value;

        // unless a specific command is provided, help is the default (i.e. if missing input)
        command = 'help';
        if (commandInput)
        {
            try
            {
                commandFragments = AdminCC.parseCommandInput(commandInput);
            }
            catch (e)
            {
                alert('Failed to parse command: ' + e);
                commandFragments = [];
            }

            if (commandFragments.length > 0 && commandFragments[0])
            {
                specialCommandSelected = false;

                // check for special commands
                switch (commandFragments[0].toLowerCase())
                {
                    case 'activateplugin':
                        if (commandFragments.length > 1 && commandFragments[1])
                        {
                            activeCommandConsolePlugin = commandFragments[1];
                            el('command-console-activePlugin').innerHTML = Admin.html(activeCommandConsolePlugin);
                        }
                        // show help - either for activated plugin or old (because argument was missing)
                        command = 'help';
                        specialCommandSelected = true;
                        break;
                    case 'listplugins':
                        plugin = 'global';
                        command = 'listPlugins';
                        specialCommandSelected = true;
                        break;
                }

                if (specialCommandSelected !== true)
                {
                    command = commandFragments[0];
                    commandArgs = commandFragments.slice(1);
                }
            }
        }

        AdminCC.runCommand(plugin, command, commandArgs);

        el('command-console-command').value = null;
        el('command-console-lastError').innerHTML = '';
        Admin.addClass(el('command-console-lastError'), 'hidden');
        if (commandFragments)
        {
            el('command-console-previousCommand').innerHTML = Admin.html(commandFragments.join(' '));
        }
        else
        {
            el('command-console-previousCommand').innerHTML = Admin.html(command + ' ' + commandArgs.join(' '));
        }

        return false;
    };

    AdminCC.runCommand = function runCommand(plugin, command, commandArgs)
    {
        var timestamp;

        // default to currently active plugin if not specified
        plugin = plugin || activeCommandConsolePlugin;
        commandArgs = commandArgs || [];
        
        timestamp = new Date().getTime();
        lastTriggeredRequestTimestamp = timestamp;

        // TODO Need progress indicator since commands may take a while

        Admin.request({
            url : serviceContext + '/ootbee/admin/command-console/' + encodeURIComponent(plugin) + '/' + encodeURIComponent(command),
            method : 'POST',
            data : {
                arguments : commandArgs
            },
            fnSuccess : function submitConsoleCommand__success(response)
            {
                var output, idx;

                if (timestamp === lastTriggeredRequestTimestamp)
                {
                    output = '';
                    // TODO How to support more flexibility (HTML provided by command plugin) without injection issues?
                    if (response.responseJSON.outputLines && Array.isArray(response.responseJSON.outputLines))
                    {
                        for (idx = 0; idx < response.responseJSON.outputLines.length; idx++)
                        {
                            output += Admin.html(response.responseJSON.outputLines[idx]);
                            output += '<br />';
                        }
                    }
                    else if (response.responseJSON.preformattedOutputLines && Array.isArray(response.responseJSON.preformattedOutputLines))
                    {
                        for (idx = 0; idx < response.responseJSON.preformattedOutputLines.length; idx++)
                        {
                            output += Admin.html(response.responseJSON.preformattedOutputLines[idx]);
                            output += '\n';
                        }
                        output = '<pre>' + output + '</pre>';
                    }
                    else if (response.responseJSON.multilineOutput)
                    {
                        output += Admin.html(response.responseJSON.multilineOutput);
                    }
                    else if (response.responseJSON.preformattedMultilineOutput)
                    {
                        output += '<pre>' + Admin.html(response.responseJSON.preformattedMultilineOutput) + '</pre>';
                    }
    
                    el('command-console-result').innerHTML = output;
                }
            },
            fnFailure : function submitConsoleCommand__failure(response)
            {
                var output, jsonResponse;
                
                if (timestamp === lastTriggeredRequestTimestamp)
                {
                    if (response.responseStatus === 404)
                    {
                        if (command !== 'help')
                        {
                            el('command-console-lastError').innerHTML = Admin.html(messages['command-console.error.unknownCommand'].replace(
                                    /\{0\}/, command).replace(/\{1\}/, plugin));
                            AdminCC.runCommand(plugin, 'help', []);
                        }
                        else if (plugin !== 'global')
                        {
                            el('command-console-lastError').innerHTML = Admin.html(messages['command-console.error.unknownPlugin'].replace(
                                    /\{0\}/, plugin));
                            activeCommandConsolePlugin = 'global';
                            el('command-console-activePlugin').innerHTML = Admin.html(activeCommandConsolePlugin);
                            AdminCC.runCommand('global', 'listPlugins', []);
                        }
                    }
                    else if (response.responseStatus === 401)
                    {
                        el('command-console-lastError').innerHTML = Admin.html(messages['command-console.error.authentication']);
                    }
                    else
                    {
                        el('command-console-lastError').innerHTML = Admin.html(messages['command-console.error.generic']);
                        output = response.responseText;
                        try
                        {
                            jsonResponse = JSON.parse(response.responseText);
                            if (jsonResponse.message)
                            {
                                output = jsonResponse.message;
                            }
                        }
                        catch(ignore)
                        {
                            // no-op
                        }
                    }

                    Admin.removeClass(el('command-console-lastError'), 'hidden');
                    if (output)
                    {
                        el('command-console-result').innerHTML = Admin.html(output);
                    }
                    else
                    {
                        el('command-console-result').innerHTML = '';
                    }
                }
            }
        });
    };

    AdminCC.parseCommandInput = function parseCommandInput(commandInput)
    {
        var pattern, matchResult, extractedFragments = [];

        if (commandInput)
        {
            pattern = /(?:"((?:[^"\\](?:\\.)?)+)"|([^\s]+))/g;
            while ((matchResult = pattern.exec(commandInput)) !== null)
            {
                extractedFragments.push(matchResult[1] ? matchResult[1] : matchResult[2]);
            }
        }

        return extractedFragments;
    };
}());
