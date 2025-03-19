<#compress>
<#escape x as jsonUtils.encodeJSONString(x)>
<#-- 
Copyright (C) 2016 - 2025 Order of the Bee

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
Copyright (C) 2005 - 2025 Alfresco Software Limited.
 
  -->

{
    "logFiles" : [
        <#if logFiles??><#list logFiles as logFile>{
            "name" : "${logFile.name}",
            "path" : "${logFile.path}",
            "directoryPath" : "${logFile.directoryPath}",
            "size" : "${logFile.size?c}",
            "lastModified" : {
                "raw" : "${logFile.lastModified?c}",
                "iso8601" : "${xmldate(logFile.lastModified?number_to_datetime)}",
                "nice" : "${logFile.lastModified?number_to_datetime?string("yyyy-MM-dd HH:mm:ss:SSS")}"
            }
        }<#if logFile_has_next>,</#if>
        </#list></#if>
    ]
}
</#escape>
</#compress>