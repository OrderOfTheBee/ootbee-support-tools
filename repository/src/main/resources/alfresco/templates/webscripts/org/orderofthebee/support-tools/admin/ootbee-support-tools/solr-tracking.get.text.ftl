<#compress>
<#--
Copyright (C) 2017 Cesar Capillas
Copyright (C) 2016 - 2020 Order of the Bee

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
Copyright (C) 2005 - 2020 Alfresco Software Limited.

-->

<#list coreNames as coreName>
# HELP alfresco_solr_tracking_${coreName} Solr tracking metrics for ${coreName}
# TYPE alfresco_solr_tracking_${coreName} gauge
alfresco_solr_tracking_${coreName}{type="numDocs"} ${trackingStatus[coreName]['index']['numDocs']?c}
alfresco_solr_tracking_${coreName}{type="maxDocs"} ${trackingStatus[coreName]['index']['maxDoc']?c}
alfresco_solr_tracking_${coreName}{type="deletedDocs"} ${trackingStatus[coreName]['index']['deletedDocs']?c}
alfresco_solr_tracking_${coreName}{type="diskUsage"} ${trackingStatus[coreName]['index']['sizeInBytes']?c}
alfresco_solr_tracking_${coreName}{type="memoryUsage"} ${trackingStatus[coreName]['index']['indexHeapUsageBytes']?c}
</#list>
</#compress>