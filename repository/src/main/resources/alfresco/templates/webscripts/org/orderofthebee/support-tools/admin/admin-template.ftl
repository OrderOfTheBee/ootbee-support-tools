<#-- 
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.
 
  -->

<#--
   ADMIN TEMPLATE MACROS
   
   This file is mostly a 1:1 copy of the Alfresco original admin-template.ftl
   The main changes are
   - externalization of inline JavaScript
   - the ability to load custom JSS and CSS files
   - optionality of maxlength on form fields instead of hard default of 255 characters
   - option to specify rows/cols for textarea
-->
<#--
   Template outer "page" macro.
   
   @param title - Title msg for the page
   @param readonly (default:false) - boolean read only flag, if true will not display the Submit buttons.
   @param controller (default:"/admin") - optionally override the Form controller
   @param params (default:"") - url encoded params to be added to the HTML form URL
-->
<#macro page title readonly=false controller=DEFAULT_CONTROLLER!"/admin" params="" dialog=false customJSFiles=[] customCSSFiles=[]>
<#assign FORM_ID="admin-jmx-form" />
<#if server.edition == "Community">
    <#assign docsEdition = "community" />
<#elseif server.edition == "Enterprise" >
    <#assign docsEdition = server.getVersionMajor() + "." + server.getVersionMinor() />
</#if>
<#if metadata??>
<#assign HOSTNAME>${msg("admin-console.host")}: ${metadata.hostname}</#assign>
<#assign HOSTADDR>${msg("admin-console.ipaddress")}: ${metadata.hostaddress}</#assign>
</#if>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Alfresco &raquo; ${title?html}<#if metadata??> [${HOSTNAME} ${HOSTADDR}]</#if></title>
   <meta http-equiv="X-UA-Compatible" content="IE=Edge" />
   <link rel="shortcut icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" />
   <link rel="icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" />
   <link rel="stylesheet" type="text/css" href="${url.context}/css/reset.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/css/alfresco.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/admin/css/admin.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/ootbee-support-tools/css/admin.css" />

   <!--[if IE 8 ]><style type="text/css">.dialog{width:100%}</style><![endif]-->
   <script type="text/javascript" src="${url.context}/ootbee-support-tools/js/admin.js"></script>
   <script type="text/javascript">//<![CDATA[

   <#assign CSRF=(config.scoped["CSRFPolicy"]["filter"].getChildren("rule")?size != 0)!false>
   <#if CSRF>
      Admin.CSRF.enabled = true;
      Admin.CSRF.cookie = "${config.scoped["CSRFPolicy"]["client"].getChildValue("cookie")!""}";
      Admin.CSRF.header = "${config.scoped["CSRFPolicy"]["client"].getChildValue("header")!""}";
      Admin.CSRF.parameter = "${config.scoped["CSRFPolicy"]["client"].getChildValue("parameter")!""}";

      <#if config.scoped["CSRFPolicy"]["properties"]??>
         <#assign csrfProperties = (config.scoped["CSRFPolicy"]["properties"].children)![]>
         <#list csrfProperties as p>
            Admin.CSRF.properties["${p.name?js_string}"] = "${(p.value!"")?js_string}";
         </#list>
      </#if>
   </#if>

   Admin.addMessages({
        requestError: "${msg("admin-console.requesterror")?js_string}",
        passwordHide : "${msg("admin-console.password.hide")?js_string}",
        passwordShow : "${msg("admin-console.password.show")?js_string}"
   });

   Admin.registerId("formId", "${FORM_ID}");

   //]]></script>

   <#list customCSSFiles as cssFile>
        <link rel="stylesheet" type="text/css" href="${url.context}/${cssFile}" />
   </#list>
   <#list customJSFiles as jsFile>
        <script type="text/javascript" src="${url.context}/${jsFile}"></script>
   </#list>

</head>
<#if !dialog>
<body>
   <#--
       Template for a full page view
   -->
   <div class="sticky-wrapper">
      
      <div class="header">
         <span><a href="${url.serviceContext}${DEFAULT_CONTROLLER!"/admin"}">${msg("admin-console.header")}</a></span><#if metadata??><span class="meta">${HOSTNAME}</span><span class="meta">${HOSTADDR}</span></#if>
         <div style="float:right"><a href="${msg("admin-console.help-link", docsEdition)}" target="_blank">${msg("admin-console.help")}</a></div>
      </div>
      
      <div class="navigation-wrapper">
         
         <div class="navigation">
            <#-- A console tool is defined as a member of the 'AdminConsole' WebScript family -->
<#local tool=""/>
<#if tools??>
            <ul>
   <#list tools as group>
      <#list group as tool>
         <#if tool_index = 0 && tool.group != ""></ul><h3>${tool.groupLabel}</h3><ul></#if>
               <li class="<#if tool.selected><#local tool=tool.uri/>selected</#if>"><a href="${url.serviceContext}${tool.uri}" class="tool-link" title="${tool.description?html}">${tool.label?html}</a></li>
      </#list>
   </#list>
</#if>
            </ul>
         </div>
         
         <div class="main-wrapper">
         
            <div class="title">
               <span class="logo"><img src="${url.context}/images/logo/logo.png" width="145" height="48" alt="" /></span>
               <span class="logo-separator">&nbsp;</span>
               <h1>${title?html}</h1>
            </div>
<#-- User information messages -->
<#if args.m??>
            <div class="message">
               ${.now?string("HH:mm:ss")} - ${msg(args.m)?html}
               <a href="#" onclick="this.parentElement.style.display='none';" title="${msg("admin-console.close")}">[X]</a>
            </div>
</#if>
<#if args.e??>
            <div class="message error">
               ${.now?string("HH:mm:ss")} - ${msg(args.e)?html}
               <a href="#" onclick="this.parentElement.style.display='none';" title="${msg("admin-console.close")}">[X]</a>
            </div>
</#if>
            <div class="main">
               <form id="${FORM_ID}" action="${url.serviceContext}${controller}?t=${tool?url}<#if params!="">&${params}</#if>" enctype="multipart/form-data" accept-charset="utf-8" method="post">
<#-- Template-specific markup -->
<#nested>

<#if !readonly>
                  <div class="submission buttons">
                     <input type="submit" value="${msg("admin-console.save")}" />
                     <input class="cancel" type="button" value="${msg("admin-console.cancel")}" onclick="location.href='${url.service}'" />
                  </div>
</#if>
               </form>
            </div>
            
         </div>
      
      </div>
      
      <div class="push"></div>
      
   </div>
   
   <div class="footer">
      Alfresco Software, Inc. &copy; 2005-2025 All rights reserved.
   </div>
   
<#else>
<body class="dialog-body">
   <#--
       Template for a dialog page view
   -->
   <div>
      
      <div class="navigation-wrapper">
         
         <div>
         
            <div class="title">
               <span class="logo"><img src="${url.context}/images/logo/logo.png" width="145" height="48" alt="" /></span>
               <span class="logo-separator">&nbsp;</span>
               <h1>${title?html}</h1>
            </div>
            <div class="main">
               <form id="${FORM_ID}" action="${url.serviceContext}/enterprise/admin/admin-dialog<#if params!="">&${params}</#if>" enctype="multipart/form-data" accept-charset="utf-8" method="post">
<#-- Template-specific markup -->
<#nested>
               </form>
            </div>
            
         </div>
      
      </div>
      
   </div>
</#if>
</body>
</html>
</#macro>

<#macro dialogbuttons save=false close=true>
   <div class="buttons">
<#-- Template-specific markup -->
<#nested>
      <#if save><input type="submit" value="${msg("admin-console.save")}" /></#if>
      <#if close><input class="cancel" type="button" value="${msg("admin-console.close")}" onclick="top.window.Admin.removeDialog();" /></#if>
   </div>
</#macro>

<#--
   Template section macros.
-->
<#macro section label>
   <h2>${label?html}</h2>
   <div class="section">
<#nested>
   </div>
</#macro>
<#macro tsection label closed=true>
   <div>
      <h2>${label?html} <a class="action toggler" href="#" onclick="Admin.sectionToggle(this);return false;"><#if closed>&#x25BA;<#else>&#x25BC;</#if></a></h2>
      <div class="section">
         <div class="toggle <#if closed>hidden</#if>">
<#nested>
         </div>
      </div>
   </div>
</#macro>

<#--
   Template field macros and value conversion.
-->
<#function cvalue type value="">
   <#switch type>
      <#case "java.util.Date">
         <#if value?has_content>
            <#return value?datetime>
         <#else>
            <#return value>
         </#if>
         <#break>
      <#case "boolean">
         <#return value?string>
         <#break>
      <#case "java.lang.Long">
         <#return value>
         <#break>
      <#default>
         <#return value>
   </#switch>
</#function>

<#macro control attribute>
   <#if attribute.readonly>
      <@attrfield attribute=attribute />
   <#else>
      <#switch attribute.type>
         <#case "java.util.Date">
            <@attrtext attribute=attribute />
            <#break>
         <#case "boolean">
            <@attrcheckbox attribute=attribute />
            <#break>
         <#case "java.lang.Long">
            <@attrtext attribute=attribute />
            <#break>
         <#default>
            <@attrtext attribute=attribute />
      </#switch>
   </#if>
</#macro>

<#-- Hidden field -->
<#macro hidden name value="" id="">
   <input type="hidden" <#if id?has_content>id="${id?html}"</#if> name="${name?html}" value="${value?html}" />
</#macro>
<#macro attrhidden attribute name=attribute.qname id="">
   <@hidden name=name value=cvalue(attribute.type, attribute.value) id=id />
</#macro>

<#-- Label and simple read-only field -->
<#macro field label="" description="" value="" id="" style="" extraClasses="">
   <div <#if id?has_content>id="${id?html}" </#if>class="control field<#if extraClasses?has_content> ${extraClasses}</#if>"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <#if value?has_content><span class="value">${value?html}</span></#if>
      <#if description?has_content><span class="description">${description?html}</span></#if>
      <#nested>
   </div>
</#macro>
<#macro attrfield attribute label=attribute.name description="" id="" style="" extraClasses="">
   <@field label=label description=description value=cvalue(attribute.type, attribute.value) id=id style=style extraClasses=extraClasses>
      <#nested>
   </@field>
</#macro>

<#macro dynamicField id label="" description="" style="" extraClasses="">
   <div id="${id?html}" class="control field<#if extraClasses?has_content> ${extraClasses}</#if>"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span id="${id?html}-value" class="value"></span>
      <#if description?has_content><span class="description">${description?html}</span></#if>
      <#nested>
   </div>
</#macro>

<#-- Label and field with custom value rendering-->
<#macro customField label="" description="" id="" style="" extraClasses="">
   <div <#if id?has_content>id="${id?html}" </#if>class="control field<#if extraClasses?has_content> ${extraClasses}</#if>"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value"><#nested></span>
      <#if description?has_content><span class="description">${description?html}</span></#if>
   </div>
</#macro>

<#-- Label and text input field -->
<#macro text name label="" description="" value="" maxlength="" id="" style="" controlStyle="" valueStyle="" placeholder="" escape=true>
   <div class="control text"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value"<#if valueStyle?has_content> style="${valueStyle?html}"</#if>><input <#if id?has_content>id="${id?html}"</#if> name="${name?html}" value="${value?html}" <#if maxlength?is_number>maxlength="${maxlength?c}"</#if> tabindex="0" <#if placeholder?has_content>placeholder="${placeholder?html}"</#if> <#if controlStyle?has_content>style="${controlStyle?html}"</#if>/></span>
      <#if description?has_content><span class="description"><#if escape>${description?html}<#else>${description}</#if></span></#if>
   </div>
</#macro>
<#macro attrtext attribute label=attribute.name description="" maxlength="" id="" style="" controlStyle="" valueStyle="" placeholder="" escape=true>
   <@text name=attribute.qname label=label description=description value=cvalue(attribute.type, attribute.value) maxlength=maxlength id=id style=style controlStyle=controlStyle valueStyle=valueStyle placeholder=placeholder escape=escape />
</#macro>

<#-- Label and password input field -->
<#macro password id name label="" description="" value="" maxlength="" style="" controlStyle="" visibilitytoggle=false>
   <div class="control text password"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value"><input id="${id?html}" name="${name?html}" value="${value?html}" <#if maxlength?is_number>maxlength="${maxlength?c}"</#if> type="password" tabindex="0" <#if controlStyle?has_content>style="${controlStyle?html}"</#if>/></span>
      <#if visibilitytoggle><@button label=msg("admin-console.password.show")?html onclick="Admin.togglePassword('${id?html}', this);" /></#if>
      <#if description?has_content><span class="description">${description?html}</span></#if>
   </div>
</#macro>
<#macro attrpassword attribute label=attribute.name id=attribute.qname description="" maxlength="" style="" controlStyle="" visibilitytoggle=false populatevalue=false>
   <#if populatevalue>
   <@password name=attribute.qname label=label id=id description=description value=cvalue(attribute.type, attribute.value) maxlength=maxlength style=style controlStyle=controlStyle visibilitytoggle=visibilitytoggle />
   <#else>
   <@password name=attribute.qname label=label id=id description=description maxlength=maxlength style=style controlStyle=controlStyle visibilitytoggle=visibilitytoggle />
   </#if>
</#macro>

<#-- Label and text area field -->
<#macro textarea name label="" description="" value="" maxlength="" id="" style="" controlStyle="" rows="" cols="">
   <div class="control textarea"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value"><textarea <#if id?has_content>id="${id?html}"</#if> name="${name?html}" <#if rows?is_number>rows="${rows?c}"</#if> <#if rows?is_number>cols="${cols?c}"</#if> <#if maxlength?is_number>maxlength="${maxlength?c}"</#if> tabindex="0" <#if controlStyle?has_content>style="${controlStyle?html}"</#if>>${value?html}</textarea></span>
      <#if description?has_content><span class="description">${description?html}</span></#if>
   </div>
</#macro>
<#macro attrtextarea attribute label=attribute.name description="" maxlength="" id="" style="" controlStyle="" rows="" cols="">
   <@textarea name=attribute.qname label=label description=description value=cvalue(attribute.type, attribute.value) maxlength=maxlength id=id style=style controlStyle=controlStyle rows=rows cols=cols />
</#macro>

<#-- Label and checkbox boolean field -->
<#macro checkbox name label description="" value="false" id="" style="" controlStyle="">
   <div class="control checkbox"<#if style?has_content> style="${style?html}"</#if>>
      <span class="label">${label?html}:</span>
      <span class="value">
         <input <#if id?has_content>id="${id?html}"</#if> name="" onchange="el('${name?html}').value = (this.checked ? 'true' : 'false');" type="checkbox" <#if value="true">checked="checked"</#if> tabindex="0" <#if controlStyle?has_content>style="${controlStyle?html}"</#if>/>
         <input id="${name?html}" name="${name?html}" type="hidden" value="<#if value="true">true<#else>false</#if>" />
      </span>
      <#if description?has_content><span class="description">${description?html}</span></#if>
      <#nested>
   </div>
</#macro>
<#macro attrcheckbox attribute label=attribute.name description="" id="" style="" controlStyle="">
   <@checkbox name=attribute.qname label=label description=description value=cvalue(attribute.type, attribute.value) id=id style=style controlStyle=controlStyle>
   <#nested>
   </@checkbox>
</#macro>

<#-- Status read-only boolean field -->
<#macro status label description="" value="false" style="">
   <#if value?is_boolean || (value?is_string && value?has_content)>
      <#if (value?is_boolean && value == true) || (value?is_string && value == "true")><#local tooltip=msg("admin-console.enabled")?html><#else><#local tooltip=msg("admin-console.disabled")?html></#if>
      <div class="control status"<#if style?has_content> style="${style?html}"</#if>>
         <span class="label">${label?html}:</span>
         <span class="value">
            <img src="${url.context}/admin/images/<#if (value?is_boolean && value == true) || (value?is_string && value == "true")>enabled<#else>disabled</#if>.gif" width="16" height="16" alt="${tooltip}" title="${tooltip}" />
            <span>${tooltip}</span>
         </span>
         <#if description?has_content><span class="description">${description?html}</span></#if>
      </div>
   <#else>
      <div class="control status"<#if style?has_content> style="${style?html}"</#if>>
         <span class="label">${label?html}:</span>
         <span class="value">
            <span>${msg("admin-console.unavailable")}</span>
         </span>
         <#if description?has_content><span class="description">${description?html}</span></#if>
      </div>
   </#if>
</#macro>
<#macro attrstatus attribute="" label=attribute.name description="" style="">
   <#-- Special handling for missing attribute - as some JMX objects can be temporarily unavailable -->
   <#if attribute?has_content>
      <@status label=label description=description value=cvalue(attribute.type, attribute.value) style=style />
   <#else>
      <@status label=label description=description value="" style=style />
   </#if>
</#macro>

<#-- Label and Options Drop-Down list -->
<#macro options name label="" description="" value="" id="" style="" valueStyle="" onchange="" onclick="" escape=true>
   <div class="control options"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value"<#if valueStyle?has_content> style="${valueStyle?html}"</#if>>
         <select <#if id?has_content>id="${id?html}"</#if> name="${name?html}" tabindex="0"<#if onchange?has_content> onchange="${onchange?html}"</#if><#if onclick?has_content> onclick="${onclick?html}"</#if>>
<#assign _value=value>
<#nested>
         </select>
      </span>
      <#if description?has_content><span class="description"><#if escape>${description?html}<#else>${description}</#if></span></#if>
   </div>
</#macro>
<#macro option label value>
            <option value="${value?html}" <#if value=_value>selected="selected"</#if>>${label?html}</option>
</#macro>
<#macro attroptions attribute label=attribute.name description="" id="" style="" valueStyle="" onchange="" escape=true>
   <@options name=attribute.qname label=label description=description value=cvalue(attribute.type, attribute.value) id=id style=style valueStyle="" onchange="" escape=escape>
      <#nested>
   </@options>
</#macro>

<#-- Label and Radio Button list -->
<#macro radios name label="" description="" value="" style="">
   <div class="control radio"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <span class="value">
<#assign _name=name>
<#assign _value=value>
<#nested>
      </span>
      <#if description?has_content><span class="description">${description?html}</span></#if>
   </div>
</#macro>
<#macro radio label value id="">
         <div class="radiovalue">
            <input <#if id?has_content>id="${id?html}"</#if> type="radio" name="${_name?html}" value="${value?html}" <#if value=_value>checked="checked"</#if> tabindex="0" />
            <span class="radiolabel">${label?html}</span>
         </div>
</#macro>

<#-- Ordered and Unordered list of values -->
<#macro list listtype label="" description="" value="" style="">
   <div class="control list"<#if style?has_content> style="${style?html}"</#if>>
      <#if label?has_content><span class="label">${label?html}:</span></#if>
      <${listtype?html}>
   <#list value?split(",") as x>
         <li>${x?html}</li>
   </#list>
      </${listtype?html}>
      <#if description?has_content><span class="description">${description?html}</span></#if>
   </div>
</#macro>
<#macro ulist label="" description="" value="" style="">
   <@list listtype="ul" label=label description=description value=value style=style />
</#macro>
<#macro olist label="" description="" value="" style="">
   <@list listtype="ol" label=label description=description value=value style=style />
</#macro>
<#macro attrulist attribute label=attribute.name description="" style="">
   <@ulist label=label description=description value=cvalue(attribute.type, attribute.value) style=style />
</#macro>
<#macro attrolist attribute label=attribute.name description="" style="">
   <@olist label=label description=description value=cvalue(attribute.type, attribute.value) style=style />
</#macro>

<#-- Simple button with JavaScript event handler -->
<#macro button label description="" onclick="" style="" id="" class="" disabled="false">
   <input class="<#if class?has_content>${class?html}<#else>inline</#if>" <#if id?has_content>id="${id?html}"</#if> <#if style?has_content>style="${style?html}"</#if> type="button" value="${label?html}" onclick="${onclick?html}" <#if disabled="true">disabled="true"</#if> />
   <#if description?has_content><span class="description">${description?html}</span></#if>
</#macro>