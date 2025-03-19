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

<#include "../admin-template.ftl" />
<#include "./content-service-transform.lib.ftl" />
<#include "./rendition-service2-transform.lib.ftl" />

<@page title=msg("transform.title") readonly=true customJSFiles=["ootbee-support-tools/js/transform.js"] customCSSFiles=["ootbee-support-tools/css/transform.css"]>

    <script type="text/javascript">//<![CDATA[
        AdminTT.setServiceUrl('${url.service}');

        AdminTT.addMessages({
            'message.genericFailure': '${msg("message.genericFailure")?js_string}',
            'message.jsonFailure': '${msg("message.jsonFailure")?js_string}',
            'message.none': '${msg("message.none")?js_string}',
            'transform.transformRegistry.supportedTransforms.transformationPriority': '${msg("transform.transformRegistry.supportedTransforms.transformationPriority")?js_string}',
            'transform.transformRegistry.supportedTransforms.transformationSourceSizeLimit': '${msg("transform.transformRegistry.supportedTransforms.transformationSourceSizeLimit")?js_string}'
        });
    //]]></script>

    <div class="column-full">
        <p class="intro">${msg("transform.intro-text")?html}</p>
    </div>

    <#if supportsContentServiceTransformers>
        <@tsection label=msg("transform.contentServiceTransform.title") closed=supportsRenditionService2>
            <@renderContentServiceTransformTools />
        </@>
    </#if>
    <#if supportsRenditionService2>
        <@tsection label=msg("transform.renditionService2Definitions.title") closed=(supportsContentServiceTransformers || localTransformServiceRegistryModel?? || remoteTransformServiceRegistryModel??)>
            <@renderRenditionService2DefinitionTools />
        </@>

        <#if localTransformServiceRegistryModel??>
            <@tsection label=msg("transform.localTransformServiceRegistry.title") closed=true>
                <@renderRenditionService2TransformRegistryTools "localTransform" localTransformServiceRegistryModel>
                        <div class="column-full">
                            <@section label=msg("transform.transformRegistry.localTransformUrls.heading") />
                            <p class="info">${msg("transform.transformRegistry.localTransformUrls.description")?html}</p>

                            <#list localTransformServiceRegistryModel.remoteUrls?keys as configKey>
                                <@field label=configKey value=localTransformServiceRegistryModel.remoteUrls[configKey]>
                                    <@button label=msg("transform.transformRegistry.localTransformUrls.readyProbe") onclick="AdminTT.probeLocalTransform('${configKey?js_string}', false);" />
                                    <@button label=msg("transform.transformRegistry.localTransformUrls.liveProbe") onclick="AdminTT.probeLocalTransform('${configKey?js_string}', true);" />
                                    <@button label=msg("transform.transformRegistry.localTransformUrls.showLogs") onclick="AdminTT.showLocalTransformLogs('${configKey?js_string}');" />
                                </@>
                            </#list>
                        </div>
                </@>
            </@>
        </#if>

        <#if remoteTransformServiceRegistryModel??>
            <@tsection label=msg("transform.remoteTransformServiceRegistry.title") closed=true>
                <@renderRenditionService2TransformRegistryTools "remoteTransform" remoteTransformServiceRegistryModel>
                    <@field label=msg("transform.transformRegistry.remoteTransformUrl.label") description=msg("transform.transformRegistry.remoteTransformUrl.description") value=remoteTransformServiceRegistryModel.remoteUrl />
                </@>
            </@>
        </#if>
    </#if>
    </div>
</@page>