/**
 * Copyright (C) 2016 Axel Faust
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
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
        for (key in oldProperties)
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
                dashboardPage.properties[key] = oldProperties[key];
            }
            dashboardPage.save();
        }
    }
}

resetSiteDashboard(String(url.templateArgs.site));
