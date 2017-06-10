<#-- 
Copyright (C) 2017 Cesar Capillas
Copyright (C) 2017 Order of the Bee

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
Copyright (C) 2005-2017 Alfresco Software Limited.
 
  -->

<#include "../admin-template.ftl" />

<#assign status1_heap_usage = status1['indexHeapUsageBytes']*0.000000001>
<#assign status2_heap_usage = status2['indexHeapUsageBytes']*0.000000001>

<@page title=msg("solr-tracking.title") readonly=true>

   <@section label=msg("solr-tracking.section.workspace.title") />

   <div class="column-left">

      <@field value="${status1['numDocs']?c}"  label=msg("solr-tracking.section.workspace.numdocs.title") 
      description=msg("solr-tracking.section.workspace.numdocs.description") />  

      <@field value="${summary1['Active']?string('Yes', 'No')}" label=msg("solr-tracking.section.workspace.indexing.title") 
      description=msg("solr-tracking.section.workspace.indexing.description") />  
      
      <@field value="${summary1['Id for last TX in index']?c}" label=msg("solr-tracking.section.workspace.last.transaction.title") 
      description=msg("solr-tracking.section.workspace.last.transaction.description") />  
      
      <@field value="${summary1['Approx transaction indexing time remaining']}" label=msg("solr-tracking.section.workspace.aprox.time.title") 
      description=msg("solr-tracking.section.workspace.aprox.time.description") />  
      
      <@field value="${status1['size']}" label=msg("solr-tracking.section.workspace.disk.usage.title") 
      description=msg("solr-tracking.section.workspace.disk.usage.description") />  
   </div>
   
   <div class="column-right">   

      <@field value="${status1['maxDoc']?c}"  label=msg("solr-tracking.section.workspace.maxdocs.title") 
      description=msg("solr-tracking.section.workspace.maxdocs.description") />  

      <@field value="${status1['deletedDocs']?c}" label=msg("solr-tracking.section.workspace.deleted.title") 
      description=msg("solr-tracking.section.workspace.deleted.description") />  

      <@field value="${summary1['TX Lag']}" label=msg("solr-tracking.section.workspace.index.lag.title") 
      description=msg("solr-tracking.section.workspace.index.lag.description")/> 

      <@field value="${summary1['Approx transactions remaining']?c}"  label=msg("solr-tracking.section.workspace.aprox.transaction.title") 
      description=msg("solr-tracking.section.workspace.aprox.transaction.description")/>  

      <@field value="${status1_heap_usage?string('##0.000')}" label=msg("solr-tracking.section.workspace.memory.usage.title") description=msg("solr-tracking.section.workspace.memory.usage.description")/>      
   </div>

   <@section label=msg("solr-tracking.section.archive.title") />

   <div class="column-left">

      <@field value="${status2['numDocs']?c}"  label=msg("solr-tracking.section.archive.numdocs.title") 
      description=msg("solr-tracking.section.archive.numdocs.description") />  
      
      <@field value="${summary2['Active']?string('Yes', 'No')}" label=msg("solr-tracking.section.archive.indexing.title") 
      description=msg("solr-tracking.section.archive.indexing.description")/>  

      <@field value="${summary2['Id for last TX in index']?c}" label=msg("solr-tracking.section.archive.last.transaction.title") 
      description=msg("solr-tracking.section.archive.last.transaction.description")/>  
      
      <@field value="${summary2['Approx transaction indexing time remaining']}"  label=msg("solr-tracking.section.archive.aprox.time.title") 
      description=msg("solr-tracking.section.archive.aprox.time.description")/>  
      
      <@field value="${status2['size']}" label=msg("solr-tracking.section.archive.disk.usage.title") 
      description=msg("solr-tracking.section.archive.disk.usage.description")/>  
   </div>
   
   <div class="column-right">

      <@field value="${status2['maxDoc']?c}"  label=msg("solr-tracking.section.archive.maxdocs.title") 
      description=msg("solr-tracking.section.archive.maxdocs.description") />  

      <@field value="${status2['deletedDocs']?c}"  label=msg("solr-tracking.section.archive.deleted.title") 
      description=msg("solr-tracking.section.archive.deleted.description") />  

      <@field value="${summary2['TX Lag']}" label=msg("solr-tracking.section.archive.index.lag.title") 
      description=msg("solr-tracking.section.archive.index.lag.description")/>  
      
      <@field value="${summary2['Approx transactions remaining']?c}" label=msg("solr-tracking.section.archive.aprox.transaction.title") 
      description=msg("solr-tracking.section.archive.aprox.transaction.description")/>  
      
      <@field value="${status2_heap_usage?string('##0.000')}" label=msg("solr-tracking.section.archive.memory.usage.title") 
      description=msg("solr-tracking.section.archive.memory.usage.description")/>      
   </div>

</@page>