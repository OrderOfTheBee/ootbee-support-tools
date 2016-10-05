<#include "../admin-template.ftl" />

<@page title=msg("scheduled-jobs.execute.title") dialog=true >

    <div class="column-full">
        <p class="intro">${msg("scheduled-jobs.execute.intro-text")?html}</p>
        <#if success>
            <p class="success">${msg("scheduled-jobs.execute.success", args.jobName)?html}</p>
        <#else>
            <p class="failure">${msg("scheduled-jobs.execute.error")?html}</p>
        </#if>
      
        <@dialogbuttons />
    </div>
   
</@page>