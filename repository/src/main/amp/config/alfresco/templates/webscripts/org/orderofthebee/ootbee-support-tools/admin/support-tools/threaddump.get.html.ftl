<#-- 
Copyright (C) 2016 Axel Faust / Markus Joos
Copyright (C) 2016 Order of the Bee

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
Copyright (C) 2005-2016 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<#assign pageName>threaddump</#assign>
<#include "threads-common.inc.ftl">

<@page title=msg("threaddump.title") readonly=true>

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
 * Thread Dump Component
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