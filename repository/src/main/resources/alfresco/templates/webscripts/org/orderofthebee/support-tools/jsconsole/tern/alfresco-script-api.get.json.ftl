<#escape x as jsonUtils.encodeJSONString(x)>{
    "typeDefinitions" : [
        {
            "!name" : "alfresco-script-api",
            "!define" : {
                <@renderJavaTypes scriptAPIJavaTypeDefinitions/><#if scriptAPIJavaTypeDefinitions?? && scriptAPIJavaTypeDefinitions?size &gt; 0>,</#if>
                <@renderPropertyTypes />,
                <#-- we fake this type for Search.queryResultSet (would just be plain object else) -->
                "SearchResultSetMeta" : {
                    "nodes" : "[ScriptNode]",
                    "meta" : "JavaMap"
                    <#-- we could add all the metadata collected in latest Alfresco version as sub-structure but then it might not match Alfresco version in use -->
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

<#macro renderPropertyTypes><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"TaskProperties" : {
    "!doc" : "The virtual type for task property maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list taskProperties as taskProperty>,
    <@renderProperty taskProperty />
    </#list>
},
"NodeProperties" : {
    "!doc" : "The virtual type for node property maps. No global object by this type exists - it is only ever returned from Alfresco Script APIs"
    <#list nodeProperties as nodeProperty>,
    <@renderProperty nodeProperty />
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

<#macro renderPropertyType property><#compress><#escape x as jsonUtils.encodeJSONString(x)>
"!type": <#switch shortQName(property.dataType.name)>
    <#case "d:text">
    <#case "d:mltext">
        <#-- despite claims in ValueConverter comments, Rhino does not automatically wrap Java String to native string -->
        "<#if property.multiValued>[</#if>JavaString<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:boolean">
        <#-- despite claims in ValueConverter comments, Rhino does not automatically wrap Java Boolean to native boolean -->
        "<#if property.multiValued>[</#if>JavaBoolean<#if property.multiValued>]</#if>"
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
        <#-- despite claims in ValueConverter comments, Rhino does not automatically wrap Java Number to native number -->
        "<#if property.multiValued>[</#if>JavaNumber<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:content">
        "<#if property.multiValued>[</#if>ScriptContentData<#if property.multiValued>]</#if>"
        <#break>
    <#case "d:date">
    <#case "d:datetime">
        "<#if property.multiValued>[</#if>+Date<#if property.multiValued>]</#if>"
        <#break>
    <#default>
        "<#if property.multiValued>[</#if>?<#if property.multiValued>]</#if>"
</#switch>
</#escape></#compress></#macro>