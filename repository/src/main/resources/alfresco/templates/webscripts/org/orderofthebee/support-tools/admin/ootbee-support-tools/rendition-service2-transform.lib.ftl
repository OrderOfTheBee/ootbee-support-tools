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

<#macro renderRenditionService2DefinitionTools>
    <div class="column-full">
        <@section label=msg("transform.renditionService2Definition.features.heading") />
        <p class="info">${msg("transform.renditionService2Definition.features.description")?html}</p>

        <@status label=msg("transform.renditionService2Definition.features.localTransform.label") description=msg("transform.renditionService2Definition.features.localTransform.description") value=hasLocalTransformClient />
        <@status label=msg("transform.renditionService2Definition.features.localTransformEnabled.label") description=msg("transform.renditionService2Definition.features.localTransformEnabled.description") value=localTransformEnabled />
        <@status label=msg("transform.renditionService2Definition.features.legacyTransform.label") description=msg("transform.renditionService2Definition.features.legacyTransform.description") value=hasLegacyTransformClient />
        <@status label=msg("transform.renditionService2Definition.features.legacyTransformEnabled.label") description=msg("transform.renditionService2Definition.features.legacyTransformEnabled.description") value=legacyTransformEnabled />
        <@status label=msg("transform.renditionService2Definition.features.synchronousTransform.label") description=msg("transform.renditionService2Definition.features.synchronousTransform.description") value=hasSynchronousTransformClient />
        <@status label=msg("transform.renditionService2Definition.features.remoteTransform.label") description=msg("transform.renditionService2Definition.features.remoteTransform.description") value=hasRemoteTransformClient />
        <@status label=msg("transform.renditionService2Definition.features.remoteTransformEnabled.label") description=msg("transform.renditionService2Definition.features.remoteTransformEnabled.description") value=remoteTransformEnabled />
    </div>

    <div class="column-full">
        <@section label=msg("transform.renditionService2Definition.check.heading") />
        <p class="info">${msg("transform.renditionService2Definition.check.description")?html}</p>

        <@options id="renditionService2DefinitionCheck-mimetype" name="renditionService2DefinitionCheckMimetype" label=msg("transform.renditionService2Definition.check.mimetype.label") description=msg("transform.renditionService2Definition.check.mimetype.description")>
            <#list mimetypes as mimetype>
                <@option label="${mimetype?html}" value="${mimetype?html}" />
            </#list>
        </@options>
        <@text id="renditionService2DefinitionCheck-size" name="renditionService2DefinitionCheckSize" label=msg("transform.renditionService2Definition.check.size.label") description=msg("transform.renditionService2Definition.check.size.description") value="-1" />

        <@button label=msg("transform.renditionService2Definition.check.button") onclick="AdminTT.findApplicableRenditions();"/>

        <@customField id="renditionService2DefinitionCheck-applicableRenditions" extraClasses="hidden dynamicResultContainer" label=msg("transform.renditionService2Definition.check.applicableRenditions.label") description=msg("transform.renditionService2Definition.check.applicableRenditions.description")>
            <ul id="renditionService2DefinitionCheck-applicableRenditions-list">
            </ul>
        </@>
    </div>

    <div class="column-full">
        <@section label=msg("transform.renditionService2Definition.list.heading") />
        <p class="info">${msg("transform.renditionService2Definition.list.description")?html}</p>

        <ul>
            <#list renditionDefinitions?keys as definitionName>
                <li class="renditionDefinition">
                    <h4>${definitionName?html}</h4>
                    <div class="renditionDefinitionDetails hidden" id="renditionService2DefinitionListDetails-${definitionName?html}">
                        <@field label=msg("transform.renditionService2Definition.list.targetMimetype.label") description=msg("transform.renditionService2Definition.list.targetMimetype.description") value=renditionDefinitions[definitionName].targetMimetype />
                        <#if renditionDefinitions[definitionName].transformOptions??>
                            <#assign transformOptions = renditionDefinitions[definitionName].transformOptions />
                            <@customField label=msg("transform.renditionService2Definition.list.options.label") description=msg("transform.renditionService2Definition.list.options.description")>
                                <ul>
                                    <#list transformOptions?keys as transformOptionKey>
                                        <li><pre>${transformOptionKey?html} = ${transformOptions[transformOptionKey]}</pre></li>
                                    </#list>
                                </ul>
                            </@>
                        </#if>
                    </div>                
                    <@button label=msg("transform.renditionService2Definition.list.toggleDetails") onclick="AdminTT.toggleRenditionDetails('${definitionName?js_string}');" />
                </li>
            </#list>
        </ul>
    </div>
</#macro>

<#macro renderRenditionService2TransformRegistryTools registryKey registryModel>
    <#-- caller can provide non-generic sections -->
    <#nested />

    <div class="column-full">
        <@section label=msg("transform.transformRegistry.check.heading") />
        <p class="info">${msg("transform.transformRegistry.check.description")?html}</p>

        <@options id=registryKey + "-transformRegistry-checkSourceType" name=registryKey + "-transformRegistry-checkSourceType" label=msg("transform.transformRegistry.check.sourceMimetype.label") description=msg("transform.transformRegistry.check.sourceMimetype.description")>
            <#list registryModel.transformSourceMimetypes as mimetype>
                <@option label="${mimetype?html}" value="${mimetype?html}" />
            </#list>
        </@options>
        <@text id=registryKey + "-transformRegistry-checkSourceSize" name=registryKey + "-transformRegistry-checkSourceSize" label=msg("transform.transformRegistry.check.size.label") description=msg("transform.transformRegistry.check.size.description") value="-1" />
        <@options id=registryKey + "-transformRegistry-checkTargetType" name=registryKey + "-transformRegistry-checkTargetType" label=msg("transform.transformRegistry.check.targetMimetype.label") description=msg("transform.transformRegistry.check.targetMimetype.description")>
            <#list registryModel.transformTargetMimetypes as mimetype>
                <@option label="${mimetype?html}" value="${mimetype?html}" />
            </#list>
        </@options>
        <@textarea id=registryKey + "-transformRegistry-checkOptions" name=registryKey + "-transformRegistry-checkOptions" label=msg("transform.transformRegistry.check.options.label") description=msg("transform.transformRegistry.check.options.description") />

        <div>
            <@button label=msg("transform.transformRegistry.check.button") onclick="AdminTT.findTransformDetails('${registryKey?js_string}');"/>
        </div>

        <div id="${registryKey?html}-transformRegistry-result-container" class="hidden dynamicResultContainer">
            <@customField label=msg("transform.transformRegistry.check.transformers.label") description=msg("transform.transformRegistry.check.transformers.description")>
                <ul id="${registryKey?html}-transformRegistry-result-transformers">
                </ul>
            </@>
            <@dynamicField id=registryKey + "-transformRegistry-result-maxSize" label=msg("transform.transformRegistry.check.maxSize.label") description=msg("transform.transformRegistry.check.maxSize.description") />
            <@dynamicField id=registryKey + "-transformRegistry-result-transformerName" label=msg("transform.transformRegistry.check.transformerName.label") description=msg("transform.transformRegistry.check.transformerName.description") />
        </div>
    </div>

    <div class="column-full">
        <@section label=msg("transform.transformRegistry.supportedTransforms.heading") />
        <p class="info">${msg("transform.transformRegistry.supportedTransforms.description")?html}</p>

        <div class="supported-transform-details-container">
            <#list registryModel.transformerNames as transformerName>
                <div id="${registryKey?html}-transformRegistry-transformDetails-${transformerName?html}" class="supported-transform-details">
                    <h4>${transformerName?html}</h4>
                    <@field label=msg("transform.transformRegistry.supportedTransforms.transformCount.label") description=msg("transform.transformRegistry.supportedTransforms.transformCount.description") value=(registryModel.transformCountsByTransformer[transformerName]!0)?c />
                    <@button label=msg("transform.transformRegistry.supportedTransforms.toggleTransformDetails") onclick="AdminTT.toggleTransformDetailsTransformations('${registryKey?js_string}', '${transformerName?js_string}');" />
                    <div id="${registryKey?html}-transformRegistry-transformDetails-${transformerName?html}-details" class="nested-details hidden">
                        <#if registryModel.optionsByTransformer[transformerName]??>
                            <@customField label=msg("transform.transformRegistry.supportedTransforms.options.label") description=msg("transform.transformRegistry.supportedTransforms.options.description")>
                                <@renderTransformerOptionModel registryModel.optionsByTransformer[transformerName] true />
                            </@>
                        </#if>
                        <@customField label=msg("transform.transformRegistry.supportedTransforms.transformations.label") description=msg("transform.transformRegistry.supportedTransforms.transformations.description")>
                            <ul>
                                <#list registryModel.transformsByTransformer[transformerName]?keys as sourceMimetype>
                                    <li>
                                        ${msg("transform.transformRegistry.supportedTransforms.transformationSourceMimetype")?html}: ${sourceMimetype?html}
                                        <ul>
                                            <#list registryModel.transformsByTransformer[transformerName][sourceMimetype] as transform>
                                                <li>
                                                    ${msg("transform.transformRegistry.supportedTransforms.transformationTargetMimetype")?html}: ${transform.targetMimetype?html},
                                                    ${msg("transform.transformRegistry.supportedTransforms.transformationPriority")?html}: ${transform.priority?c},
                                                    ${msg("transform.transformRegistry.supportedTransforms.transformationSourceSizeLimit")?html}: ${transform.maxSourceSizeBytes?c}</li>
                                                </li>
                                            </#list>
                                        </ul>
                                    </li>
                                </#list>
                            </ul>
                        </@>
                    </div>
                </div>
            </#list>
        </div>
    </div>

    <div class="column-full">
        <@section label=msg("transform.transformRegistry.extractionAndEmbedding.heading") />
        <p class="info">${msg("transform.transformRegistry.extractionAndEmbedding.description")?html}</p>

        <div class="extractionAndEmbedding-mimetype-container">
            <@customField label=msg("transform.transformRegistry.extractionAndEmbedding.extractableMimetypes.label") description=msg("transform.transformRegistry.extractionAndEmbedding.extractableMimetypes.description")>
                <ul>
                    <#list registryModel.extractableMimetypes as mimetype>
                        <li>${mimetype?html}</li>
                    </#list>
                    <#if registryModel.extractableMimetypes?size == 0>
                        <li>${msg("message.none")?html}</li>
                    </#if>
                </ul>
            </@>
            <@customField label=msg("transform.transformRegistry.extractionAndEmbedding.embeddableMimetypes.label") description=msg("transform.transformRegistry.extractionAndEmbedding.embeddableMimetypes.description")>
                <ul>
                    <#list registryModel.embeddableMimetypes as mimetype>
                        <li>${mimetype?html}</li>
                    </#list>
                    <#if registryModel.embeddableMimetypes?size == 0>
                        <li>${msg("message.none")?html}</li>
                    </#if>
                </ul>
            </@>
        </div>
    </div>
</#macro>

<#macro renderTransformerOptionModel optionModel isRoot=false>
    <#if optionModel.name?has_content>
        <li>${optionModel.name?html}<#if optionModel.required> (${msg("transform.transformRegistry.supportedTransforms.requiredOption")?html})</#if></li>
    <#elseif optionModel.groupElements??>
        <#if isRoot>
            <ul>
                <#list optionModel.groupElements as element>
                    <@renderTransformerOptionModel element />
                </#list>
            </ul>
        <#else>
            <li>${msg("transform.transformRegistry.supportedTransforms.optionGroup")?html}<#if optionModel.required> (${msg("transform.transformRegistry.supportedTransforms.requiredOption")?html})</#if>
                <ul>
                    <#list optionModel.groupElements as element>
                        <@renderTransformerOptionModel element />
                    </#list>
                </ul>
            </li>
        </#if>
    </#if>
</#macro>