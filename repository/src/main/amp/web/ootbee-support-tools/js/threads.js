/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
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
 * Linked to Alfresco Copyright
 * (C) 2005-2016 Alfresco Software Limited.
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

    AdminTD.saveTextAsFile = function saveTextAsFile(toSave)
    {
        var textToWrite, allDumps, i, tDump, dump, textFileAsBlob, fileNameToSaveAs, ie, ieVer, ie11, downloadLink;

        textToWrite = "";

        if (toSave === "all")
        {
            allDumps = document.getElementsByClassName("thread");

            for (i = 0; i < allDumps.length; i++)
            {
                tDump = allDumps[i].innerHTML;
                textToWrite += tDump + "\n";
            }
        }
        else
        {
            dump = el(toSave);
            if (dump)
            {
                tDump = dump.innerHTML;
                textToWrite += tDump + "\n";
            }
        }

        textToWrite = AdminTD.replaceAll("<span id=\"date\" class=\"highlight\">", "", textToWrite);
        textToWrite = AdminTD.replaceAll("<span class=\"highlight\">", "", textToWrite);
        textToWrite = AdminTD.replaceAll("</span>", "", textToWrite);
        textToWrite = AdminTD.replaceAll("&lt;", "<", textToWrite);
        textToWrite = AdminTD.replaceAll("&gt;", ">", textToWrite);

        textFileAsBlob = new Blob([ textToWrite ], {
            type : 'text/plain'
        });
        fileNameToSaveAs = toolName + ".txt";

        ie = navigator.userAgent.match(/MSIE\s([\d.]+)/);
        ie11 = navigator.userAgent.match(/Trident\/7.0/) && navigator.userAgent.match(/rv:11/);
        ieVer = (ie ? ie[1] : (ie11 ? 11 : -1));

        if (ie && ieVer < 10)
        {
            // TODO I18n
            alert("No blobs on IE ver < 10");
            return;
        }

        if (ie || ie11)
        {
            window.navigator.msSaveBlob(textFileAsBlob, fileNameToSaveAs);
        }
        else
        {
            downloadLink = document.createElement("a");

            downloadLink.download = fileNameToSaveAs;
            // TODO I18n
            downloadLink.innerHTML = "Download File";

            if (window.webkitURL !== null)
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
                downloadLink.style.display = "none";
                document.body.appendChild(downloadLink);
            }

            downloadLink.click();
        }
    };

    AdminTD.showTab = function showTab(tabName)
    {
        var allTabs, i, selectors;

        allTabs = document.getElementsByClassName("thread");
        for (i = 0; i < allTabs.length; i++)
        {
            if (allTabs[i].id === tabName)
            {
                AdminTD.removeClass(allTabs[i], "hidden");
            }
            else
            {
                AdminTD.addClass(allTabs[i], "hidden");
            }
        }

        selectors = document.getElementsByClassName("selector");

        for (i = 0; i < selectors.length; i++)
        {
            if (selectors[i].id === "s" + tabName)
            {
                AdminTD.addClass(selectors[i], "selected");
                el("savecurrent").setAttribute("onclick", "AdminTD.saveTextAsFile('" + tabName + "');");
            }
            else
            {
                AdminTD.removeClass(selectors[i], "selected");
            }
        }
    };

    AdminTD.getDump = function getDump()
    {
        Admin.request({
            url : serviceContext + "/ootbee/admin/" + toolName + "-getone.html",
            requestContentType : "text/html",
            responseContentType : "text/html",
            fnSuccess : function(res)
            {
                var counter, viewer, control, firstDel, secondDel, tabTitle;
                if (res.responseText)
                {
                    counter = document.getElementsByClassName("thread").length;
                    viewer = document.getElementById("viewer");
                    control = document.getElementById("control");
                    // Find the date in the response text:
                    firstDel = res.responseText.indexOf(">", res.responseText.indexOf("id=\"date\""));
                    secondDel = res.responseText.indexOf("<", firstDel);
                    tabTitle = res.responseText.substr(firstDel + 1, secondDel - (firstDel + 1));

                    viewer.innerHTML = viewer.innerHTML
                            + res.responseText.replace("__id__", "td" + counter).replace("__class__", "thread hidden");
                    control.innerHTML = control.innerHTML + "<input type=\"button\" class=\"selector\" value=\"" + tabTitle + "\" id=\"std"
                            + counter + "\" onclick=\"AdminTD.showTab('td" + counter + "');\" /> ";

                    AdminTD.showTab("td" + counter);
                }
            }
        });
    };

    AdminTD.hasClass = function hasClass(element, clas)
    {
        return element.className.match(new RegExp("(\\s|^)" + clas + "(\\s|$)"));
    };

    AdminTD.addClass = function addClass(element, clas)
    {
        if (!AdminTD.hasClass(element, clas))
        {
            element.className += " " + clas;
        }
    };

    AdminTD.removeClass = function removeClass(element, clas)
    {
        var reg;
        if (AdminTD.hasClass(element, clas))
        {
            reg = new RegExp("(\\s|^)" + clas + "(\\s|$)");
            element.className = element.className.replace(reg, " ");
        }
    };

    AdminTD.replaceAll = function replaceAll(find, replace, str)
    {
        return str.replace(new RegExp(find, 'g'), replace);
    };
}());
