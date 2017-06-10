<#compress>
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
{
    "AlfrescoNumDocs" : ${status1['numDocs']?c},
    "AlfrescoMaxDocs" : ${status1['maxDoc']?c},
    "AlfrescoDeletedDocs" : ${status1['deletedDocs']?c},
    "AlfrescoOnDiskBytes" : ${status1['sizeInBytes']?c},
    "AlfrescoHeapBytes" : ${status1['indexHeapUsageBytes']?c},
    "ArchiveNumDocs" : ${status2['numDocs']?c},
    "ArchiveMaxDocs" : ${status2['maxDoc']?c},
    "ArchiveDeletedDocs" : ${status2['deletedDocs']?c},
    "ArchiveOnDiskBytes" : ${status2['sizeInBytes']?c},
    "ArchiveHeapBytes" : ${status2['indexHeapUsageBytes']?c}  
}
</#compress>