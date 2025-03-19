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

function resetSiteDashboard(site)
{
    var siteProfile, response, componentResults, cidx, dashboardPage, oldProperties, tokens, key;

    response = remote.call('/api/sites/' + site);
    if (response.status.code === 200)
    {
        siteProfile = JSON.parse(response.response);
    }

    dashboardPage = sitedata.getPage('site/' + site + '/dashboard');
    if (siteProfile && dashboardPage)
    {
        componentResults = sitedata.findComponents('page', null, 'site/' + site + '/dashboard', null);
        for (cidx = 0; cidx < componentResults.length; cidx++)
        {
            sitedata.unbindComponent(componentResults[cidx].modelObject.scope, componentResults[cidx].modelObject.regionId,
                    componentResults[cidx].modelObject.sourceId);
        }
        
        oldProperties = {};
        for (key in dashboardPage.propertie)
        {
            if (dashboardPage.properties[key] !== null)
            {
                oldProperties[key] = dashboardPage.properties[key];
            }
        }
        dashboardPage.delete();
        
        tokens = {
            siteid : site
        };

        if (sitedata.newPreset(siteProfile.sitePreset, tokens))
        {
            dashboardPage = sitedata.getPage('site/' + site + '/dashboard');
            for (key in oldProperties)
            {
                if (oldProperties[key] !== null)
                {
                    dashboardPage.properties[key] = oldProperties[key];
                }
            }
            dashboardPage.save();
        }
    }
}

function resetUserDashboard(user)
{
    var componentResults, cidx, dashboardPage, tokens;

    dashboardPage = sitedata.getPage('user/' + user + '/dashboard');
    if (dashboardPage)
    {
        componentResults = sitedata.findComponents('page', null, 'user/' + user + '/dashboard', null);
        for (cidx = 0; cidx < componentResults.length; cidx++)
        {
            sitedata.unbindComponent(componentResults[cidx].modelObject.scope, componentResults[cidx].modelObject.regionId,
                    componentResults[cidx].modelObject.sourceId);
        }
        
        dashboardPage.delete();
        
        tokens = {
            userid : user
        };
        
        sitedata.newPreset('user-dashboard', tokens);
    }
}

function resetDashboards()
{
    // default json root object is crappy JSONObject implementation
    var obj, dashboardIds;
    
    obj = JSON.parse(String(json));
    
    dashboardIds = obj.dashboardIds || [];
    dashboardIds.forEach(function (dashboardId){
        var site, user;
        if (dashboardId.indexOf('site/') === 0)
        {
            site = dashboardId.substring(5, dashboardId.indexOf('/dashboard'));
            resetSiteDashboard(site);
        }
        else if (dashboardId.indexOf('user/') === 0)
        {
            user = dashboardId.substring(5, dashboardId.indexOf('/dashboard'));
            resetUserDashboard(user);
        }
    });
}

if (url.templateArgs.site)
{
    resetSiteDashboard(String(url.templateArgs.site));
}
else if (url.templateArgs.user)
{
    resetUserDashboard(String(url.templateArgs.user));
}
else if (this.json !== undefined)
{
    resetDashboards();
}