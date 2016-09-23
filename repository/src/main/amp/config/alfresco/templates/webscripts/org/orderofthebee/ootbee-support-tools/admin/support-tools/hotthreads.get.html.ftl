<#include "../admin-template.ftl" />

<#assign pageName>hotthreads</#assign>
<#include "admin-alfresco-threads-common.inc.ftl">

<@page title=msg("alfresco-hotthreads.title") readonly=true>

${htmlStyle}
   
${htmlPortion}

   <script type="text/javascript">//<![CDATA[
      
/**
 * Admin Support Tools Component
 */
Admin.addEventListener(window, 'load', function() {
   AdminTD.getDump();
});

/**
 * Hot ThreadsComponent
 */
var AdminTD = AdminTD || {};

(function() {

${AdminTD_saveTextAsFile}

${AdminTD_showTab}
	
${AdminTD_getDump}
	
${AdminTD_hasClass}

${AdminTD_addClass}

${AdminTD_removeClass}

${AdminTD_replaceAll}
	
})();

//]]></script>

</@page>