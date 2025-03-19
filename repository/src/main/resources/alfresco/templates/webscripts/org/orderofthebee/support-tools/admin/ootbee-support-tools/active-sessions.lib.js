/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

function buildConnectionPoolData()
{
    var ctxt, dataSource, connectionPoolData;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    dataSource = ctxt.getBean('defaultDataSource', Packages.javax.sql.DataSource);

    // technically javax.sql.DataSource does not provide the various properties
    // but DBCP BasicDataSource does
    connectionPoolData = {
        initialSize : dataSource.initialSize,
        numActive : dataSource.numActive,
        maxActive : dataSource.maxActive || dataSource.maxTotal, // dbcp2 renamed the property
        minIdle : dataSource.minIdle,
        numIdle : dataSource.numIdle,
        maxIdle : dataSource.maxIdle,
        maxWait : dataSource.maxWait || dataSource.maxWaitMillis, // dbcp2 renamed the property
        url : dataSource.url,
        driverClassName : dataSource.driverClassName
    };

    model.connectionPoolData = connectionPoolData;
}

function buildUserSessionsData(resolveUsers)
{
    var ctxt, authenticationService, usersWithTickets, ArrayList, userNames, userNamesArr, userSessionData, idx;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    // must be private bean because operations are not part of public API
    authenticationService = ctxt.getBean('authenticationService', Packages.org.alfresco.service.cmr.security.AuthenticationService);

    usersWithTickets = authenticationService.getUsersWithTickets(true);
    userSessionData = {
        userCountNonExpired : usersWithTickets.size(),
        ticketCountNonExpired : authenticationService.countTickets(true)
    };

    if (resolveUsers === true)
    {
        ArrayList = Packages.java.util.ArrayList;
        userNames = new ArrayList(usersWithTickets);
        userNamesArr = [];

        userSessionData.unexpiredUserNames = userNamesArr;
        userSessionData.unexpiredUsers = [];

        for (idx = 0; idx < userNames.size(); idx++)
        {
            userNamesArr.push(userNames.get(idx));
        }

        for (idx = 0; idx < userNamesArr.length; idx++)
        {
            userSessionData.unexpiredUsers.push(people.getPerson(userNamesArr[idx]));
        }
    }

    model.userSessionData = userSessionData;
}

/* exported buildActiveSessionsData */
function buildActiveSessionsData(resolveUsers)
{
    buildConnectionPoolData();
    buildUserSessionsData(resolveUsers);
}

/* exported logoutUser */
function logoutUser(userName)
{
    var ctxt, authenticationService;

    ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
    authenticationService = ctxt.getBean('AuthenticationService', Packages.org.alfresco.service.cmr.security.AuthenticationService);

    authenticationService.invalidateUserSession(userName);
}
