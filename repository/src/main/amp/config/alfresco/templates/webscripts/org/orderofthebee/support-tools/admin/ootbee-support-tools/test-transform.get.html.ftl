<#-- 
Copyright (C) 2016, 2017 Axel Faust / Markus Joos
Copyright (C) 2016, 2017 Order of the Bee

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
Copyright (C) 2005-2017 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<@page title=msg("test-transform.title") readonly=true customJSFiles=["ootbee-support-tools/js/test-transform.js"]>

    <script type="text/javascript">//<![CDATA[
        AdminTT.setServiceUrl('${url.service}');
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("test-transform.intro-text")?html}</p>

        <@section label=msg("test-transform.getProperties.heading") />
        <p class="info">${msg("test-transform.getProperties.description")?html}</p>
    </div>
	<div class="column-left">
        <@options id="getProperties" name="getProperties" label=msg("test-transform.getProperties.options") value="false">
            <@option label=msg("test-transform.getProperties.options.all") value="false" />
            <@option label=msg("test-transform.getProperties.options.custom") value="true" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getProperties.button") onclick="AdminTT.showInDialog('getProperties', el('getProperties').value);"/>
    </div>

    <div class="column-full">
        <@section label=msg("test-transform.setProperties.heading") /> 
        <p class="info">${msg("test-transform.setProperties.description")?html}</p>
    </div>
	<div class="column-left">
        <@textarea name="setProperties" label=msg("test-transform.setProperties.property") description=msg("test-transform.setProperties.property.description") id="setProperties" />
    </div>
	<div class="column-right">
        <@button label=msg("test-transform.setProperties.button") onclick="AdminTT.showInDialog('setProperties', el('setProperties').value);"/>
	</div>

	<div class="column-full">
        <@section label=msg("test-transform.removeProperties.heading") />
        <p class="info">${msg("test-transform.removeProperties.description")?html}</p>
    </div>
    <div class="column-left">
        <@textarea name="removeProperties" label=msg("test-transform.removeProperties.property") id="removeProperties" />
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.removeProperties.button") onclick="AdminTT.showInDialog('removeProperties', el('removeProperties').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformationLog.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("test-transform.getTransformationLog.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformationLog.button") onclick="AdminTT.showInDialog('getTransformationLog');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformationDebugLog.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("test-transform.getTransformationDebugLog.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformationDebugLog.button") onclick="AdminTT.showInDialog('getTransformationDebugLog');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformerNames.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("test-transform.getTransformerNames.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformerNames.button") onclick="AdminTT.showInDialog('getTransformerNames');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformationStatistics.heading") />
        <p class="info">${msg("test-transform.getTransformationStatistics.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationStatistics-transformer" name="getTransformationStatistics-transformer" label=msg("test-transform.select.transfromer") description=msg("test-transform.select.transfromer.description") value="">
            <@option label=msg("test-transform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

      <@options id="getTransformationStatistics-from" name="getTransformationStatistics-from" label=msg("test-transform.select.from") description=msg("test-transform.select.from.description") value="">
         <@option label=msg("test-transform.select.from.option") value="" />
         <#list extensionsAndMimetypes as extensionAndMimetype>
            <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
         </#list>
      </@options>

      <@options id="getTransformationStatistics-to" name="getTransformationStatistics-to" label=msg("test-transform.select.to") description=msg("test-transform.select.to.description") value="">
         <@option label=msg("test-transform.select.to.option") value="" />
         <#list extensionsAndMimetypes as extensionAndMimetype>
            <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
         </#list>
      </@options>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformationStatistics.button") onclick="AdminTT.showInDialog('getTransformationStatistics', el('getTransformationStatistics-transformer').value, el('getTransformationStatistics-from').value, el('getTransformationStatistics-to').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.testTransform.heading") />
        <p class="info">${msg("test-transform.testTransform.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="testTransform-transformer" name="testTransform-transformer" label=msg("test-transform.select.transfromer") description=msg("test-transform.select.transfromer.description") value="">
            <@option label=msg("test-transform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

        <@options id="testTransform-from" name="testTransform-from" label=msg("test-transform.select.from") description=msg("test-transform.select.from.description") value="">
            <@option label=msg("test-transform.select.from.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="testTransform-to" name="testTransform-to" label=msg("test-transform.select.to") description=msg("test-transform.select.to.description") value="">
            <@option label=msg("test-transform.select.to.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="testTransform-context" name="testTransform-context" label=msg("test-transform.select.context") description=msg("test-transform.select.context.description") value="">
            <@option label=msg("test-transform.select.context.option") value="" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="webpreview" value="webpreview" />
            <@option label="syncRule" value="syncRule" />
            <@option label="asyncRule" value="asyncRule" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.testTransform.button") onclick="AdminTT.showInDialog('testTransform', el('testTransform-transformer').value, el('testTransform-from').value, el('testTransform-to').value, el('testTransform-context').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformationsByExtension.heading") />
        <p class="info">${msg("test-transform.getTransformationsByExtension.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationsByExtension-from" name="getTransformationsByExtension-from" label=msg("test-transform.select.from") description=msg("test-transform.select.from.description") value="">
            <@option label=msg("test-transform.select.from.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <#-- TODO Use proper display labels for extensions -->
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByExtension-to" name="getTransformationsByExtension-to" label=msg("test-transform.select.to") description=msg("test-transform.select.to.description") value="">
            <@option label=msg("test-transform.select.to.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <#-- TODO Use proper display labels for extensions -->
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByExtension-context" name="getTransformationsByExtension-context" label=msg("test-transform.select.context") description=msg("test-transform.select.context.description") value="">
            <@option label=msg("test-transform.select.context.option") value="" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="webpreview" value="webpreview" />
            <@option label="syncRule" value="syncRule" />
            <@option label="asyncRule" value="asyncRule" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformationsByExtension.button") onclick="AdminTT.showInDialog('getTransformationsByExtension', el('getTransformationsByExtension-from').value, el('getTransformationsByExtension-to').value, el('getTransformationsByExtension-context').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("test-transform.getTransformationsByTransformer.heading") />
        <p class="info">${msg("test-transform.getTransformationsByTransformer.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationsByTransformer-transformer" name="getTransformationsByTransformer-transformer" label=msg("test-transform.select.transfromer") description=msg("test-transform.select.transfromer.description") value="">
            <@option label=msg("test-transform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByTransformer-context" name="getTransformationsByTransformer-context" label=msg("test-transform.select.context") description=msg("test-transform.select.context.description") value="">
            <@option label=msg("test-transform.select.context.option") value="" />
            <@option label="asyncRule" value="asyncRule" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="syncRule" value="syncRule" />
            <@option label="webpreview" value="webpreview" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("test-transform.getTransformationsByTransformer.button") onclick="AdminTT.showInDialog('getTransformationsByTransformer', el('getTransformationsByTransformer-transformer').value, el('getTransformationsByTransformer-context').value);"/>
    </div>
</@page>