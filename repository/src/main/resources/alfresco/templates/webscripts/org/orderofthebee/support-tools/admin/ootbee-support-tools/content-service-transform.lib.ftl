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
Copyright (C) 2005 - 2021 Alfresco Software Limited.
 
  -->
  
<#macro renderContentServiceTransformTools>
    <div class="column-full">
        <@section label=msg("transform.contentServiceTransform.getProperties.heading") />
        <p class="info">${msg("transform.contentServiceTransform.getProperties.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getProperties" name="getProperties" label=msg("transform.contentServiceTransform.getProperties.options") value="false">
            <@option label=msg("transform.contentServiceTransform.getProperties.options.all") value="false" />
            <@option label=msg("transform.contentServiceTransform.getProperties.options.custom") value="true" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getProperties.button") onclick="AdminTT.showInDialog('getProperties', el('getProperties').value);"/>
    </div>

    <div class="column-full">
        <@section label=msg("transform.contentServiceTransform.setProperties.heading") /> 
        <p class="info">${msg("transform.contentServiceTransform.setProperties.description")?html}</p>
    </div>
    <div class="column-left">
        <@textarea name="setProperties" label=msg("transform.contentServiceTransform.setProperties.property") description=msg("transform.contentServiceTransform.setProperties.property.description") id="setProperties" />
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.setProperties.button") onclick="AdminTT.showInDialog('setProperties', el('setProperties').value);"/>
    </div>

    <div class="column-full">
        <@section label=msg("transform.contentServiceTransform.removeProperties.heading") />
        <p class="info">${msg("transform.contentServiceTransform.removeProperties.description")?html}</p>
    </div>
    <div class="column-left">
        <@textarea name="removeProperties" label=msg("transform.contentServiceTransform.removeProperties.property") id="removeProperties" />
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.removeProperties.button") onclick="AdminTT.showInDialog('removeProperties', el('removeProperties').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformationLog.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("transform.contentServiceTransform.getTransformationLog.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformationLog.button") onclick="AdminTT.showInDialog('getTransformationLog');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformationDebugLog.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("transform.contentServiceTransform.getTransformationDebugLog.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformationDebugLog.button") onclick="AdminTT.showInDialog('getTransformationDebugLog');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformerNames.heading") />
    </div>
    <div class="column-left">
        <p class="info">${msg("transform.contentServiceTransform.getTransformerNames.description")?html}</p>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformerNames.button") onclick="AdminTT.showInDialog('getTransformerNames');"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformationStatistics.heading") />
        <p class="info">${msg("transform.contentServiceTransform.getTransformationStatistics.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationStatistics-transformer" name="getTransformationStatistics-transformer" label=msg("transform.contentServiceTransform.select.transfromer") description=msg("transform.contentServiceTransform.select.transfromer.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

      <@options id="getTransformationStatistics-from" name="getTransformationStatistics-from" label=msg("transform.contentServiceTransform.select.from") description=msg("transform.contentServiceTransform.select.from.description") value="">
         <@option label=msg("transform.contentServiceTransform.select.from.option") value="" />
         <#list extensionsAndMimetypes as extensionAndMimetype>
            <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
         </#list>
      </@options>

      <@options id="getTransformationStatistics-to" name="getTransformationStatistics-to" label=msg("transform.contentServiceTransform.select.to") description=msg("transform.contentServiceTransform.select.to.description") value="">
         <@option label=msg("transform.contentServiceTransform.select.to.option") value="" />
         <#list extensionsAndMimetypes as extensionAndMimetype>
            <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
         </#list>
      </@options>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformationStatistics.button") onclick="AdminTT.showInDialog('getTransformationStatistics', el('getTransformationStatistics-transformer').value, el('getTransformationStatistics-from').value, el('getTransformationStatistics-to').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.testTransform.heading") />
        <p class="info">${msg("transform.contentServiceTransform.testTransform.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="testTransform-transformer" name="testTransform-transformer" label=msg("transform.contentServiceTransform.select.transfromer") description=msg("transform.contentServiceTransform.select.transfromer.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

        <@options id="testTransform-from" name="testTransform-from" label=msg("transform.contentServiceTransform.select.from") description=msg("transform.contentServiceTransform.select.from.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.from.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="testTransform-to" name="testTransform-to" label=msg("transform.contentServiceTransform.select.to") description=msg("transform.contentServiceTransform.select.to.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.to.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="testTransform-context" name="testTransform-context" label=msg("transform.contentServiceTransform.select.context") description=msg("transform.contentServiceTransform.select.context.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.context.option") value="" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="webpreview" value="webpreview" />
            <@option label="syncRule" value="syncRule" />
            <@option label="asyncRule" value="asyncRule" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.testTransform.button") onclick="AdminTT.showInDialog('testTransform', el('testTransform-transformer').value, el('testTransform-from').value, el('testTransform-to').value, el('testTransform-context').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformationsByExtension.heading") />
        <p class="info">${msg("transform.contentServiceTransform.getTransformationsByExtension.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationsByExtension-from" name="getTransformationsByExtension-from" label=msg("transform.contentServiceTransform.select.from") description=msg("transform.contentServiceTransform.select.from.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.from.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <#-- TODO Use proper display labels for extensions -->
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByExtension-to" name="getTransformationsByExtension-to" label=msg("transform.contentServiceTransform.select.to") description=msg("transform.contentServiceTransform.select.to.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.to.option") value="" />
            <#list extensionsAndMimetypes as extensionAndMimetype>
                <#-- TODO Use proper display labels for extensions -->
                <@option label="${extensionAndMimetype.extension?html}" value="${extensionAndMimetype.extension?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByExtension-context" name="getTransformationsByExtension-context" label=msg("transform.contentServiceTransform.select.context") description=msg("transform.contentServiceTransform.select.context.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.context.option") value="" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="webpreview" value="webpreview" />
            <@option label="syncRule" value="syncRule" />
            <@option label="asyncRule" value="asyncRule" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformationsByExtension.button") onclick="AdminTT.showInDialog('getTransformationsByExtension', el('getTransformationsByExtension-from').value, el('getTransformationsByExtension-to').value, el('getTransformationsByExtension-context').value);"/>
    </div>

    <div class="column-full">  
        <@section label=msg("transform.contentServiceTransform.getTransformationsByTransformer.heading") />
        <p class="info">${msg("transform.contentServiceTransform.getTransformationsByTransformer.description")?html}</p>
    </div>
    <div class="column-left">
        <@options id="getTransformationsByTransformer-transformer" name="getTransformationsByTransformer-transformer" label=msg("transform.contentServiceTransform.select.transfromer") description=msg("transform.contentServiceTransform.select.transfromer.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.transfromer.option") value="" />
            <#list transformerNames as transformerName>
                <@option label="${transformerName?html}" value="${transformerName?html}" />
            </#list>
        </@options>

        <@options id="getTransformationsByTransformer-context" name="getTransformationsByTransformer-context" label=msg("transform.contentServiceTransform.select.context") description=msg("transform.contentServiceTransform.select.context.description") value="">
            <@option label=msg("transform.contentServiceTransform.select.context.option") value="" />
            <@option label="asyncRule" value="asyncRule" />
            <@option label="doclib" value="doclib" />
            <@option label="index" value="index" />
            <@option label="syncRule" value="syncRule" />
            <@option label="webpreview" value="webpreview" />
        </@options>
    </div>
    <div class="column-right">
        <@button label=msg("transform.contentServiceTransform.getTransformationsByTransformer.button") onclick="AdminTT.showInDialog('getTransformationsByTransformer', el('getTransformationsByTransformer-transformer').value, el('getTransformationsByTransformer-context').value);"/>
    </div>
</#macro>