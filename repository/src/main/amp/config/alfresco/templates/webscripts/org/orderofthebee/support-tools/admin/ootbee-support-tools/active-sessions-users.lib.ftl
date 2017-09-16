<#compress>
<#-- 
Copyright (C) 2016, 2017 Axel Faust / Markus Joos
Copyright (C) 2016, 2017 Order of the Bee

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

<#escape x as jsonUtils.encodeJSONString(x)>
{
    "users": [
    <#assign first = true />
    <#list userSessionData.unexpiredUsers as user>
        <#if user??><#if !first>,</#if>
            <#assign first = false />
            {
                "userName" : "${user.properties.userName!''}",
                "firstName" : "${user.properties.firstName!''}",
                "lastName" : "${user.properties.lastName!''}",
                "email" : "${user.properties.email!''}"
            }
        </#if> 
    </#list>
    ]
}
</#escape>
</#compress>