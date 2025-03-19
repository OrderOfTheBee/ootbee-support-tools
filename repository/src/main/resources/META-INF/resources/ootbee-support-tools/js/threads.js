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

// The AdminTD root object has been extracted from the Alfresco Support Tools
// threads-common.inc.ftl trim down page HTML sizes and promote clean
// separation of concerns

/**
 * Hot ThreadsComponent
 */
var AdminTD = AdminTD || {};

/**
 * Admin Support Tools Component
 */
Admin.addEventListener(window, 'load', function()
{
    AdminTD.getDump();
});

(function()
{
    var serviceContext, toolName;

    AdminTD.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };

    AdminTD.setToolName = function setToolName(newToolName)
    {
        toolName = newToolName;
    };
    
    AdminTD.copyToClipboard = function copyToClipBoard(toSave)
    {
        var textToWrite, dump, tDump, area;

        textToWrite = '';
        dump = el(toSave);
        if (dump)
        {
            tDump = dump.innerHTML;
            textToWrite += tDump + '\n';
        }

        textToWrite = textToWrite.replace(/<\/?span[^>]*>/g, '');
        textToWrite = textToWrite.replace(/&lt;/g, '<');
        textToWrite = textToWrite.replace(/&gt;/g, '>');
        
        area = document.createElement('textarea');
        area.value = textToWrite;
        document.body.appendChild(area);
        area.select();
        document.execCommand('copy');
        document.body.removeChild(area);
    };

    AdminTD.saveTextAsFile = function saveTextAsFile(toSave)
    {
        var textToWrite, allDumps, i, tDump, dump, textFileAsBlob, fileNameToSaveAs, ie, ieVer, ie11, downloadLink;

        textToWrite = '';

        if (toSave === 'all')
        {
            allDumps = document.getElementsByClassName('thread');

            for (i = 0; i < allDumps.length; i++)
            {
                tDump = allDumps[i].innerHTML;
                textToWrite += tDump + '\n';
            }
        }
        else
        {
            dump = el(toSave);
            if (dump)
            {
                tDump = dump.innerHTML;
                textToWrite += tDump + '\n';
            }
        }

        textToWrite = textToWrite.replace(/<\/?span[^>]*>/g, '');
        textToWrite = textToWrite.replace(/&lt;/g, '<');
        textToWrite = textToWrite.replace(/&gt;/g, '>');

        textFileAsBlob = new Blob([ textToWrite ], {
            type : 'text/plain'
        });
        fileNameToSaveAs = toolName + '.txt';

        ie = navigator.userAgent.match(/MSIE\s([\d.]+)/);
        ie11 = navigator.userAgent.match(/Trident\/7.0/) && navigator.userAgent.match(/rv:11/);
        ieVer = (ie ? ie[1] : (ie11 ? 11 : -1));

        if (ie && ieVer < 10)
        {
            // TODO I18n
            alert('No blobs on IE ver < 10');
            return;
        }

        if (ie || ie11)
        {
            window.navigator.msSaveBlob(textFileAsBlob, fileNameToSaveAs);
        }
        else
        {
            downloadLink = document.createElement('a');

            downloadLink.download = fileNameToSaveAs;
            // TODO I18n
            downloadLink.innerHTML = 'Download File';

            if (window.webkitURL)
            {
                // Chrome allows the link to be clicked
                // without actually adding it to the DOM.
                downloadLink.href = window.webkitURL.createObjectURL(textFileAsBlob);
            }
            else
            {
                // Firefox requires the link to be added to the DOM
                // before it can be clicked.
                downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
                downloadLink.style.display = 'none';
                document.body.appendChild(downloadLink);
            }

            downloadLink.click();
        }
    };

    AdminTD.showTab = function showTab(tabName)
    {
        var allTabs, i, selectors, copyCurrentEl;

        allTabs = document.getElementsByClassName('thread');
        for (i = 0; i < allTabs.length; i++)
        {
            if (allTabs[i].id === tabName)
            {
                Admin.removeClass(allTabs[i], 'hidden');
            }
            else
            {
                Admin.addClass(allTabs[i], 'hidden');
            }
        }

        selectors = document.getElementsByClassName('selector');

        for (i = 0; i < selectors.length; i++)
        {
            if (selectors[i].id === 's' + tabName)
            {
                Admin.addClass(selectors[i], 'selected');
                copyCurrentEl = el('copycurrent');
                if (copyCurrentEl)
                {
                    copyCurrentEl.setAttribute('onclick', 'AdminTD.copyToClipboard("' + tabName + '");');
                }
                el('savecurrent').setAttribute('onclick', 'AdminTD.saveTextAsFile("' + tabName + '");');
            }
            else
            {
                Admin.removeClass(selectors[i], 'selected');
            }
        }
    };

    AdminTD.getDump = function getDump()
    {
        Admin.request({
            url : serviceContext + '/ootbee/admin/' + encodeURIComponent(toolName) + '-getone.html',
            requestContentType : 'text/html',
            responseContentType : 'text/html',
            fnSuccess : function(res)
            {
                var counter, viewer, control, firstDel, secondDel, tabTitle;
                if (res.responseText)
                {
                    counter = document.getElementsByClassName('thread').length;
                    viewer = document.getElementById('viewer');
                    control = document.getElementById('control');
                    // Find the date in the response text:
                    firstDel = res.responseText.indexOf('>', res.responseText.indexOf('id="date"'));
                    secondDel = res.responseText.indexOf('<', firstDel);
                    tabTitle = res.responseText.substr(firstDel + 1, secondDel - (firstDel + 1));

                    viewer.innerHTML = viewer.innerHTML
                            + res.responseText.replace('__id__', 'td' + counter).replace('__class__', 'thread hidden');
                    control.innerHTML = control.innerHTML + '<input type="button" class="selector" value="' + tabTitle + '" id="std'
                            + counter + '" onclick="AdminTD.showTab(\'td' + counter + '\');" />';

                    AdminTD.showTab('td' + counter);
                }
            }
        });
    };
}());
