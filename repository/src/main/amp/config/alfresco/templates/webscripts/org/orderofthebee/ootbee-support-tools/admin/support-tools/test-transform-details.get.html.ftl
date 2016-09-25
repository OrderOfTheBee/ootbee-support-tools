<#include "../admin-template.ftl" />

<@page title=msg("test-transform.details.title") dialog=true >
    <@dialogbuttons />
    <div class="column-full">
        <#if headerKey??>
            <@section label=msg("test-transform.details." + headerKey)?html/>
        <#else>
            <@section label=header?html/>
        </#if>
        <div style="border: 1px solid #ccc; padding:0.5em; margin-top:1em;">
            <pre style="white-space: pre-wrap;">
                <#if messageKey??>
${msg("test-transform.details." + messageKey)?html}
                <#else>
${message?html}
                </#if>
            </pre>
        </div>
    <@dialogbuttons />
</@page>