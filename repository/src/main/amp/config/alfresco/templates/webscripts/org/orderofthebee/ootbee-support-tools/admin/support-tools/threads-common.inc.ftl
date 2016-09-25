<#-- 
Copyright (C) 2016 Axel Faust / Markus Joos
Copyright (C) 2016 Order of the Bee

This file is part of Community Support Tools

Community Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Community Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005-2016 Alfresco Software Limited.
 
  -->

<#assign htmlStyle>
   <style>
      .${pageName} pre
      {
         white-space: pre-wrap;
      }
      .highlight
      {
         color: #c00;
      }
      .selector.selected
      {
         background-color: #FFFFFF !important;
		 border: 1px solid #444 !important;
		 border-bottom-color: #FFFFFF !important;
		 z-index: 2;
      }
      #viewer
      {
		 border: 1px solid #444;
         padding:0.5em;
		 z-index: -1;
		 position: relative;
      }
      button.save
      {
         background-color: #6E9E2D;
      }
	  .selector
	  {
		-webkit-border-bottom-right-radius:0px !important;
		-moz-border-radius-bottomright:0px !important;
		border-bottom-right-radius:0px !important;
		-webkit-border-bottom-left-radius:0px !important;
		-moz-border-radius-bottomleft:0px !important;
		border-bottom-left-radius:0px !important;
		-webkit-border-top-right-radius:8px !important;
		-moz-border-radius-topright:8px !important;
		border-top-right-radius:8px !important;
		-webkit-border-top-left-radius:8px !important;
		-moz-border-radius-topleft:8px !important;
		border-top-left-radius:8px !important;
		background-color: #EEEEEE !important;
		color: #222 !important;
		z-index: -1;
		font-size: 9px;
		margin-bottom:-1px;
	  }
   </style>
</#assign>

<#assign htmlPortion>
   <div class="column-full">
      <p class="intro">${msg("${pageName}.intro-text")?html}</p>

   	<@button label=msg("${pageName}.get-another") onclick="AdminTD.getDump();"/>
    <@button id="savecurrent" class="save" label=msg("${pageName}.savecurrent") onclick="AdminTD.saveTextAsFile('current');"/>
   	<@button class="save" label=msg("${pageName}.saveall") onclick="AdminTD.saveTextAsFile('all');"/>

   	<@section label="" />
   	<div id="control" class="buttons"></div>
    <div id="viewer" class="${pageName}"></div>
   </div>
</#assign>

<#assign AdminTD_saveTextAsFile>
   AdminTD.saveTextAsFile = function saveTextAsFile(tosave)
   {
       var textToWrite = "";

       if (tosave === "all")
       {
           var allDumps = document.getElementsByClassName("thread");

           for (var i = 0; i < allDumps.length; i++)
           {
               var tDump = allDumps[i].innerHTML;
               textToWrite += tDump + "\n";
           }
       }
       else
       {
           var dump = el(tosave);
           if (dump)
           {
               var tDump = dump.innerHTML;
               textToWrite += tDump + "\n";
           }
       }

       textToWrite = AdminTD.replaceAll("<span id=\"date\" class=\"highlight\">", "", textToWrite);
       textToWrite = AdminTD.replaceAll("<span class=\"highlight\">", "", textToWrite);
       textToWrite = AdminTD.replaceAll("</span>", "", textToWrite);
       textToWrite = AdminTD.replaceAll("&lt;", "<", textToWrite);
       textToWrite = AdminTD.replaceAll("&gt;", ">", textToWrite);

       var textFileAsBlob = new Blob([textToWrite], {
           type: 'text/plain'
       });
       var fileNameToSaveAs = "${pageName}.txt";

       var ie = navigator.userAgent.match(/MSIE\s([\d.]+)/),
           ie11 = navigator.userAgent.match(/Trident\/7.0/) && navigator.userAgent.match(/rv:11/),
           ieVer = (ie ? ie[1] : (ie11 ? 11 : -1));

       if (ie && ieVer < 10) {
           console.log("No blobs on IE ver<10");
           return;
       }

       if (ie || ie11) {
           window.navigator.msSaveBlob(textFileAsBlob, fileNameToSaveAs);
       }
       else
       {
           var downloadLink = document.createElement("a");

           downloadLink.download = fileNameToSaveAs;
           downloadLink.innerHTML = "Download File";

           if (window.webkitURL != null)
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
   }
</#assign>

<#assign AdminTD_showTab>
	AdminTD.showTab = function showTab(tabName)
	{
	    var allTabs = document.getElementsByClassName("thread");
	    for (var i = 0; i < allTabs.length; i++)
	    {
	        if (allTabs[i].id == tabName)
	        {
	            AdminTD.removeClass(allTabs[i], "hidden");
	        }
	        else
	        {
	            AdminTD.addClass(allTabs[i], "hidden");
	        }
	    }

	    var selectors = document.getElementsByClassName("selector");
        
	    for (var i = 0; i < selectors.length; i++)
	    {
	        if (selectors[i].id == "s" + tabName)
	        {
	            AdminTD.addClass(selectors[i], "selected");
	            el("savecurrent").setAttribute("onclick", "AdminTD.saveTextAsFile('" + tabName + "');");
	        }
	        else
	        {
	            AdminTD.removeClass(selectors[i], "selected");
	        }
	    }
	}
</#assign>

<#assign AdminTD_getDump>
	AdminTD.getDump = function getDump()
	{
	    Admin.request({
	        url: "${url.serviceContext}/ootbee/admin/${pageName}-getone.html",
	        requestContentType: "text/html",
	        responseContentType: "text/html",
	        fnSuccess: function(res)
	        {
	            if (res.responseText)
	            {
	                var counter = document.getElementsByClassName("thread").length;
	                var viewer = document.getElementById("viewer");
	                var control = document.getElementById("control");
	                // Find the date in the response text:
	                var firstDel = res.responseText.indexOf(">", res.responseText.indexOf("id=\"date\""));
	                var secondDel = res.responseText.indexOf("<", firstDel);
	                var tabTitle = res.responseText.substr(firstDel + 1, secondDel - (firstDel + 1));

	                viewer.innerHTML = viewer.innerHTML + res.responseText.replace("__id__", "td" + counter).replace("__class__", "thread hidden");
	                control.innerHTML = control.innerHTML + "<input type=\"button\" class=\"selector\" value=\"" + tabTitle + "\" id=\"std" + counter + "\" onclick=\"AdminTD.showTab('td" + counter + "');\" /> ";

	                AdminTD.showTab("td" + counter);
	            }
	        }
	    });
	}
</#assign>

<#assign AdminTD_hasClass>
   AdminTD.hasClass = function hasClass(element, clas)
   {
       return element.className.match(new RegExp("(\\s|^)" + clas + "(\\s|$)"));
   }
</#assign>

<#assign AdminTD_addClass>   
   AdminTD.addClass = function addClass(element, clas)
   {
       if (!AdminTD.hasClass(element, clas))
       {
           element.className += " " + clas;
       }
   }
</#assign>

<#assign AdminTD_removeClass>
   AdminTD.removeClass = function removeClass(element, clas)
   {
       if (AdminTD.hasClass(element, clas))
       {
           var reg = new RegExp("(\\s|^)" + clas + "(\\s|$)");
           element.className = element.className.replace(reg, " ");
       }
   }
</#assign>

<#assign AdminTD_replaceAll>
   AdminTD.replaceAll = function replaceAll(find, replace, str)
   {
       return str.replace(new RegExp(find, 'g'), replace);
   }
</#assign>