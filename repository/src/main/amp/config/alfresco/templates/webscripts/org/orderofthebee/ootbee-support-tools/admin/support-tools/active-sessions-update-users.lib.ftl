{<#escape x as jsonUtils.encodeJSONString(x)><#compress>
    "users": [
    <#list userSessionData.unexpiredUsers as user>
        <#if user??> 
            {
                "username" : "${user.properties.userName!''}",
                "firstName" : "${user.properties.firstName!''}",
                "lastName" : "${user.properties.lastName!''}",
                "email" : "${user.properties.email!''}"
            }<#if user_has_next>,</#if>
        </#if> 
    </#list>
    ]
</#compress></#escape>}