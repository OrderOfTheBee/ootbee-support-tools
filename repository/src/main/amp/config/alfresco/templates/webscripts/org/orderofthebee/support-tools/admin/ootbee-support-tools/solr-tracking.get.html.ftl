<#include "../admin-template.ftl" />

<@page title=msg("solr-tracking.title") readonly=true>

   <@section label=msg("solr-tracking.section.workspace.title") />

   <div class="column-left">

      <@field value="${searcher1['numDocs']?c}"  label=msg("solr-tracking.section.workspace.numdocs.title") 
      description=msg("solr-tracking.section.workspace.numdocs.description") />  

      <@field value="${summary.alfresco['Active']?string('Yes', 'No')}"  label=msg("solr-tracking.section.workspace.indexing.title") 
      description=msg("solr-tracking.section.workspace.indexing.description") />  
      
      <@field value="${summary.alfresco['Id for last TX in index']?c}" label=msg("solr-tracking.section.workspace.last.transaction.title") 
      description=msg("solr-tracking.section.workspace.last.transaction.description") />  
      
      <@field value="${summary.alfresco['Approx transaction indexing time remaining']}" label=msg("solr-tracking.section.workspace.aprox.time.title") 
      description=msg("solr-tracking.section.workspace.aprox.time.description") />  
      
      <@field value="${searcher1['size']}" label=msg("solr-tracking.section.workspace.disk.usage.title") 
      description=msg("solr-tracking.section.workspace.disk.usage.description") />  
   </div>
   
   <div class="column-right">   

      <@field value="${searcher1['maxDoc']?c}"  label=msg("solr-tracking.section.workspace.maxdocs.title") 
      description=msg("solr-tracking.section.workspace.maxdocs.description") />  

      <@field value="${searcher1['deletedDocs']?c}" label=msg("solr-tracking.section.workspace.deleted.title") 
      description=msg("solr-tracking.section.workspace.deleted.description") />  

      <@field value="${summary.alfresco['TX Lag']}" label=msg("solr-tracking.section.workspace.index.lag.title") 
      description=msg("solr-tracking.section.workspace.index.lag.description")/> 

      <@field value="${summary.alfresco['Approx transactions remaining']?c}"  label=msg("solr-tracking.section.workspace.aprox.transaction.title") 
      description=msg("solr-tracking.section.workspace.aprox.transaction.description")/>  

      <@field value="${searcher1['indexHeapUsageBytes']*0.000000001}" label=msg("solr-tracking.section.workspace.memory.usage.title") description=msg("solr-tracking.section.workspace.memory.usage.description")/>      
   </div>
 
   <br/><br/>

   <@section label=msg("solr-tracking.section.archive.title") />

   <div class="column-left">

      <@field value="${searcher2['numDocs']?c}"  label=msg("solr-tracking.section.archive.numdocs.title") 
      description=msg("solr-tracking.section.archive.numdocs.description") />  

      <@field value="${summary.alfresco['Active']?string('Yes', 'No')}" label=msg("solr-tracking.section.archive.indexing.title") 
      description=msg("solr-tracking.section.archive.indexing.description")/>  

      <@field value="${summary.archive['Id for last TX in index']?c}" label=msg("solr-tracking.section.archive.last.transaction.title") 
      description=msg("solr-tracking.section.archive.last.transaction.description")/>  
      
      <@field value="${summary.archive['Approx transaction indexing time remaining']}"  label=msg("solr-tracking.section.archive.aprox.time.title") 
      description=msg("solr-tracking.section.archive.aprox.time.description")/>  
      
      <@field value="${searcher2['size']}" label=msg("solr-tracking.section.archive.disk.usage.title") 
      description=msg("solr-tracking.section.archive.disk.usage.description")/>  
   </div>
   
   <div class="column-right">

      <@field value="${searcher2['maxDoc']?c}"  label=msg("solr-tracking.section.archive.maxdocs.title") 
      description=msg("solr-tracking.section.archive.maxdocs.description") />  

      <@field value="${searcher2['deletedDocs']?c}"  label=msg("solr-tracking.section.archive.deleted.title") 
      description=msg("solr-tracking.section.archive.deleted.description") />  

      <@field value="${summary.archive['TX Lag']}" label=msg("solr-tracking.section.archive.index.lag.title") 
      description=msg("solr-tracking.section.archive.index.lag.description")/>  
      
      <@field value="${summary.archive['Approx transactions remaining']?c}" label=msg("solr-tracking.section.archive.aprox.transaction.title") 
      description=msg("solr-tracking.section.archive.aprox.transaction.description")/>  
      
      <@field value="${searcher2['indexHeapUsageBytes']*0.000000001}" label=msg("solr-tracking.section.archive.memory.usage.title") 
      description=msg("solr-tracking.section.archive.memory.usage.description")/>      
   </div>

</@page>
