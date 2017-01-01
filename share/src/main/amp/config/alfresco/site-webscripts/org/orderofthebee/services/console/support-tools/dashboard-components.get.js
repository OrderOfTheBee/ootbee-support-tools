/**
 * Copyright (C) 2016 Axel Faust
 * Copyright (C) 2016 Order of the Bee
 * 
 * This file is part of Community Support Tools
 * 
 * Community Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco Copyright (C) 2005-2016 Alfresco Software Limited.
 */
function queryUserDashboardComponents(components, componentFilter)
{
    var response, people, pidx, person, regionFilter, componentResults, cidx;

    if (args.sourceType === null || String(args.sourceType) === 'all' || String(args.sourceType) === 'user')
    {
        response = remote.call('/api/people?filter=' + (args.filter !== null && String(args.filter).trim() !== '' ? args.filter : '*'));
        if (response.status.code === 200)
        {
            people = JSON.parse(response.response).people;
            for (pidx = 0; pidx < people.length; pidx++)
            {
                person = people[pidx];
                
                if (regionFilter === undefined)
                {
                    regionFilter = args.region !== null && String(args.region).trim() !== '' ? args.region : null;
                    if (typeof regionFilter === 'string' && regionFilter.indexOf('*') !== 0)
                    {
                        regionFilter = '*' + regionFilter;
                    }
                    
                    if (typeof regionFilter === 'string' && regionFilter.lastIndexOf('*') !== regionFilter.length() - 1)
                    {
                        regionFilter = regionFilter + '*';
                    }
                }
                
                componentResults = sitedata.findComponents('page', regionFilter, 'user/' + person.userName + '/dashboard', null);
                // componentResults is not a proper native JS array so concat won't work
                for (cidx = 0; cidx < componentResults.length; cidx++)
                {
                    if (componentFilter(componentResults[cidx].modelObject))
                    {
                        components.push({
                            simpleSource : person.userName,
                            simpleSourceDisplayName : (person.firstName === null ? '' : person.firstName)
                                    + (person.lastName === null ? '' : ((person.firstName !== null ? ' ' : '') + person.lastName)) + ' (' + person.userName + ')',
                            simpleSourceType : 'user',
                            surfComponent : componentResults[cidx].modelObject
                        });
                    }
                }
            }
        }
    }
}

function querySiteDashboardComponents(components, componentFilter)
{
    var response, sites, sidx, regionFilter, componentResults, cidx;

    if (args.sourceType === null || String(args.sourceType) === 'all' || String(args.sourceType) === 'site')
    {
        // Repository-tier ADMRemoteStore does not support wildcard queries for site/*/dashboard
        // so we must query site before looking up dashboard components
        response = remote.call('/api/sites' + (args.filter !== null && String(args.filter).trim() !== '' ? ('?nf=' + args.filter) : ''));
        if (response.status.code === 200)
        {
            sites = JSON.parse(response.response);
            for (sidx = 0; sidx < sites.length; sidx++)
            {
                if (regionFilter === undefined)
                {
                    regionFilter = args.region !== null && String(args.region).trim() !== '' ? args.region : null;
                    if (typeof regionFilter === 'string' && regionFilter.indexOf('*') !== 0)
                    {
                        regionFilter = '*' + regionFilter;
                    }
                    
                    if (typeof regionFilter === 'string' && regionFilter.lastIndexOf('*') !== regionFilter.length() - 1)
                    {
                        regionFilter = regionFilter + '*';
                    }
                }
                
                componentResults = sitedata.findComponents('page', regionFilter, 'site/' + sites[sidx].shortName + '/dashboard', null);
                for (cidx = 0; cidx < componentResults.length; cidx++)
                {
                    if (componentFilter(componentResults[cidx].modelObject))
                    {
                        components.push({
                            simpleSource : sites[sidx].shortName,
                            simpleSourceDisplayName : sites[sidx].title,
                            simpleSourceDescription : sites[sidx].description,
                            simpleSourceType : 'site',
                            surfComponent : componentResults[cidx].modelObject
                        });
                    }
                }
            }
        }
    }
}

function buildComponentFilter()
{
    var suppressedRegionIds, urlPattern;

    suppressedRegionIds = [ 'title', 'navigation' ];

    if (args.componentUrl !== undefined && args.componentUrl !== null && String(args.componentUrl).trim() !== '')
    {
        urlPattern = new RegExp(String(args.componentUrl), 'g');
    }

    return function componentFilter_apply(component)
    {
        var include = true;

        include = include && suppressedRegionIds.indexOf(String(component.regionId)) === -1;
        include = include && (urlPattern === undefined || urlPattern.test(String(component.getURL())));

        return include;
    };
}

function queryDashboardComponents()
{
    var components = [], componentFilter = buildComponentFilter();

    queryUserDashboardComponents(components, componentFilter);
    querySiteDashboardComponents(components, componentFilter);

    model.components = components;
}

queryDashboardComponents();
