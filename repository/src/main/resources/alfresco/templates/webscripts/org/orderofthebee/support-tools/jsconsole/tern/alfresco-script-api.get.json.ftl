<#escape x as jsonUtils.encodeJSONString(x)>{
    "typeDefinitions" : [
        {
            "!name" : "alfresco-script-api",
            "!define" : {
                <@renderJavaTypes scriptAPIJavaTypeDefinitions/><#if scriptAPIJavaTypeDefinitions?? && scriptAPIJavaTypeDefinitions?size &gt; 0>,</#if>
                <@renderModelTypes />,
                <#-- we fake this as base type for native-like maps -->
                "NativeLikeMap": {
                    "length": {
                        "!type": "number",
                        "!doc": "(read-only)"
                    }
                },
                "NativeLikeMap2": {
                    "!proto": "NativeLikeMap",
                    "hasOwnProperty": {
                        "!type": "fn(prop: string) -> bool"
                    }
                },
                <#-- we fake this type for Search.queryResultSet (would just be plain object else) -->
                "SearchResultSetMeta" : {
                    "!proto": "Map",
                    "nodes" : "[ScriptNode]",
                    "meta" : {
                        "!proto": "Map",
                        <#-- meta structure as available from 5.0.d -->
                        "numberFound": "number",
                        "hasMore": "bool",
                        "facets": "object",
                        "spellcheck": "ScriptSpellCheckResult",
                        <#-- version dependent meta structure -->
                        "highlighting": {
                            "!proto": "Map",
                            "!doc": "Alfresco >= 5.1.2/5.2.1"
                        }
                    }
                }
            }<#if scriptAPIGlobalDefinitions?? && scriptAPIGlobalDefinitions?size &gt; 0>,</#if>
            <@renderGlobals scriptAPIGlobalDefinitions />
        },
        {
            "!name" : "alfresco-web-script-api",
            "!define" : {
                <@renderJavaTypes webScriptAPIJavaTypeDefinitions/>
            }<#if webScriptAPIGlobalDefinitions?? && webScriptAPIGlobalDefinitions?size &gt; 0>,</#if>
            <@renderGlobals webScriptAPIGlobalDefinitions />
        }
    ]
}</#escape>

<#macro renderGlobals globals><#compress><#escape x as jsonUtils.encodeJSONString(x)>
<#list globals as globalDefinition>
    "${globalDefinition.name}" : {
        <#if globalDefinition.doc??>
        "!doc" : "${globalDefinition.doc}"<#if globalDefinition.type != 'object'>,</#if>
        </#if>
        <#if globalDefinition.type != 'object'>
        "!type" : "${globalDefinition.type}"
        </#if>
    }<#if globalDefinition_has_next>,</#if>
</#list>
</#escape></#compress></#macro>

<#macro renderJavaTypes javaTypeDefinitions><#compress><#escape x as jsonUtils.encodeJSONString(x)>
<#list javaTypeDefinitions as javaTypeDefinition>
    "${javaTypeDefinition.name}" : {
        <#if javaTypeDefinition.doc??>
        "!doc" : "${javaTypeDefinition.doc}"<#if javaTypeDefinition.url?? || javaTypeDefinition.prototype?? || (javaTypeDefinition.members?? && javaTypeDefinition.members?size &gt; 0)>,</#if>
        </#if>
        <#if javaTypeDefinition.url??>
        "!url" : "${javaTypeDefinition.url}"<#if javaTypeDefinition.prototype?? || (javaTypeDefinition.members?? && javaTypeDefinition.members?size &gt; 0)>,</#if>
        </#if>
        <#if javaTypeDefinition.prototype??>
        "!proto" : "${javaTypeDefinition.prototype}"<#if javaTypeDefinition.members?? && javaTypeDefinition.members?size &gt; 0>,</#if>
        </#if>
        <#if javaTypeDefinition.members??>
            <#list javaTypeDefinition.members as memberDefinition>
            "${memberDefinition.name}" : {
                <#if memberDefinition.originalName??>
                "!original" : "${memberDefinition.originalName}",
                </#if>
                <#if memberDefinition.doc??>
                "!doc" : "<#if memberDefinition.readOnly??>${memberDefinition.readOnly?string('(read-only)', '(read-write)')} </#if>${memberDefinition.doc}",
                <#elseif memberDefinition.readOnly??>
                "!doc" : "${memberDefinition.readOnly?string('(read-only)', '(read-write)')}",
                </#if>
                <#if memberDefinition.url??>
                "!url" : "${memberDefinition.url}",
                </#if>
                "!type" : "${memberDefinition.type}"
            }<#if memberDefinition_has_next>,</#if>
            </#list>
        </#if>
    }<#if javaTypeDefinition_has_next>,</#if>
</#list>
</#escape></#compress></#macro>

<#macro renderModelTypes><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"TaskProperties" : {
    "!proto": "NativeLikeMap2",
    "!doc" : "The virtual type for task property maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list taskProperties as taskProperty>,
    <@renderProperty taskProperty />
    </#list>
},
"NodeProperties" : {
    "!proto": "NativeLikeMap2",
    "!doc" : "The virtual type for node property maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list nodeProperties as nodeProperty>,
    <@renderProperty nodeProperty />
    </#list>
},
"NodeParentChildAssocs" : {
    "!proto": "NativeLikeMap2",
    "!doc" : "The virtual type for node parent/child association maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list nodeChildAssociations as childAssociation>,
    <@renderAssociation childAssociation />
    </#list>
},
"NodePeerAssocs" : {
    "!proto": "NativeLikeMap2",
    "!doc" : "The virtual type for node peer association maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list nodePeerAssociations as nodePeerAssociation>,
    <@renderAssociation nodePeerAssociation />
    </#list>
}
</#escape></#compress></#macro>

<#macro renderProperty property><#compress><#escape x as jsonUtils.encodeJSONString(x)>
<#assign propertyName = shortQName(property.name) />
"${propertyName}" : {
    <@renderPropertyType property />,
    "!doc" : "${(property.description!property.title)!propertyName}"
}<#if propertyName?starts_with('cm:')>,
"${propertyName?substring(3)}" : {
    <@renderPropertyType property />,
    "!doc" : "${(property.description!property.title)!propertyName}"
}
</#if>
</#escape></#compress></#macro>

<#macro renderAssociation assoc><#compress><#escape x as jsonUtils.encodeJSONString(x)>
<#assign assocName = shortQName(assoc.name) />
"${assocName}" : {
    "!type": "[ScriptNode]",
    "!doc" : "${(assoc.description!assoc.title)!assocName}"
}<#if assocName?starts_with('cm:')>,
"${assocName?substring(3)}" : {
    "!type": "[ScriptNode]",
    "!doc" : "${(assoc.description!assoc.title)!assocName}"
}
</#if>
</#escape></#compress></#macro>

<#macro renderPropertyType property><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"!type": <#switch shortQName(property.dataType.name)>
    <#case "d:text">
    <#case "d:mltext">
        <#-- tests/experience shows even though Rhino WrapFactory wraps String as NativeJavaObject -->
        <#-- handling is still indistinguishable from native string -->
        "<#if property.multiValued>[</#if>string<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:boolean">
        <#-- tests/experience shows even though Rhino WrapFactory wraps Boolean as NativeJavaObject -->
        <#-- handling is still indistinguishable from native bool -->
        "<#if property.multiValued>[</#if>bool<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:noderef">
        "<#if property.multiValued>[</#if>ScriptNode<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:category">
        "<#if property.multiValued>[</#if>CategoryNode<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:int">
    <#case "d:long">
    <#case "d:float">
    <#case "d:double">
        <#-- tests/experience shows even though Rhino WrapFactory wraps Number as NativeJavaObject -->
        <#-- handling is still indistinguishable from native number -->
        "<#if property.multiValued>[</#if>number<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:content">
        "<#if property.multiValued>[</#if>ScriptContentData<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:date">
    <#case "d:datetime">
        <#-- Date is converted in ValueConverter -->
        "<#if property.multiValued>[</#if>+Date<#if property.multiValued>]</#if>"
        <#break>
    <#default>
        "<#if property.multiValued>[</#if>?<#if property.multiValued>]</#if>"
</#switch>
</#escape></#compress></#macro>