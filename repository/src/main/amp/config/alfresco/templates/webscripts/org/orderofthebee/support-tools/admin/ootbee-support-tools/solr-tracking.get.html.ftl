<#include "../admin-template.ftl" />

<@page title=msg("solr-tracking.title") readonly=true>

   <@section label=msg("solr-tracking.section.workspace.title") />

   <div class="column-left">
      <@field value="" label=msg("solr-tracking.section.workspace.indexing.title") 
      description=msg("solr-tracking.section.workspace.indexing.description") />  
      <@field value="" label=msg("solr-tracking.section.workspace.last.transaction.title") 
      description=msg("solr-tracking.section.workspace.last.transaction.description") />  
      <@field value="" label=msg("solr-tracking.section.workspace.aprox.time.title") 
      description=msg("solr-tracking.section.workspace.aprox.time.description") />  
      <@field value="${summary.alfresco['On disk (GB)']}" label=msg("solr-tracking.section.workspace.disk.usage.title") 
      description=msg("solr-tracking.section.workspace.disk.usage.description") />  
   </div>
   
   <div class="column-right">   
      <@field value="" label=msg("solr-tracking.section.workspace.index.lag.title") 
      description=msg("solr-tracking.section.workspace.index.lag.description")/>  
      <@field value="" label=msg("solr-tracking.section.workspace.aprox.transaction.title") 
      description=msg("solr-tracking.section.workspace.aprox.transaction.description")/>  
      <@field value="" label=msg("solr-tracking.section.workspace.memory.usage.title") description=msg("solr-tracking.section.workspace.memory.usage.description")/>      
   </div>
 
   <br/><br/>

   <@section label=msg("solr-tracking.section.archive.title") />

   <div class="column-left">
      <@field value="" label=msg("solr-tracking.section.archive.indexing.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
      <@field value="" label=msg("solr-tracking.section.archive.last.transaction.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
      <@field value="" label=msg("solr-tracking.section.archive.aprox.time.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
      <@field value="${summary.archive['On disk (GB)']}" label=msg("solr-tracking.section.archive.disk.usage.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
   </div>
   
   <div class="column-right">
      <@field value="" label=msg("solr-tracking.section.archive.index.lag.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
      <@field value="" label=msg("solr-tracking.section.archive.aprox.transaction.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>  
      <@field value="" label=msg("solr-tracking.section.archive.memory.usage.title") 
      description=msg("solr-tracking.section.workspace.memory.usage.description")/>      
   </div>

</@page>
   


