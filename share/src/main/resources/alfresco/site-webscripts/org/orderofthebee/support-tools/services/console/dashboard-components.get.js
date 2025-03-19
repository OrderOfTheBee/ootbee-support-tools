/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */
function queryUserDashboardComponents(components, componentFilter, maxItems)
{
    var response, peopleRetrieved, lastPeopleRetrieved, people, pidx, person, regionFilter, componentResults, cidx;

    if (args.sourceType === null || String(args.sourceType) === 'all' || String(args.sourceType) === 'user')
    {
        peopleRetrieved = 0;
        // note: use (2 * maxItems) as limit so we can determine if more than maxItems exist for pagination (see queryDashboardComponents)
        while (lastPeopleRetrieved !== 0 && components.length <= (2 * maxItems))
        {
            // for some reason people.get uses both skipCount + startIndex, maxItems + maxResults
            // use 99 as pageSize since default Repository-tier userToAuthorityTransactionalCache is set to 100
            response = remote.call('/api/people?filter=' + (args.filter !== null && String(args.filter).trim() !== '' ? args.filter : '*')
                    + '&pageSize=99&maxResults=99&skipCount=' + peopleRetrieved + '&startIndex=' + peopleRetrieved + '&sortBy=userName&dir=asc');
            if (response.status.code === 200)
            {
                people = JSON.parse(response.response).people;
                for (pidx = 0; pidx < people.length && components.length <= (2 * maxItems); pidx++)
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
                    for (cidx = 0; cidx < componentResults.length && components.length <= (2 * maxItems); cidx++)
                    {
                        if (componentFilter(componentResults[cidx].modelObject))
                        {
                            components.push({
                                simpleSource : person.userName,
                                simpleSourceDisplayName : (person.firstName === null ? '' : person.firstName)
                                        + (person.lastName === null ? '' : ((person.firstName !== null ? ' ' : '') + person.lastName))
                                        + ' (' + person.userName + ')',
                                simpleSourceType : 'user',
                                surfComponent : componentResults[cidx].modelObject
                            });
                        }
                    }
                }
                peopleRetrieved += people.length;
                lastPeopleRetrieved = people.length;
            }
        }
    }
}

function querySiteDashboardComponents(components, componentFilter, maxItems)
{
    var response, sitesRetrieved, lastSitesRetrieved, sites, sidx, regionFilter, componentResults, cidx;

    if (args.sourceType === null || String(args.sourceType) === 'all' || String(args.sourceType) === 'site')
    {
        sitesRetrieved = 0;
        // note: use (2 * maxItems) as limit so we can determine if more than maxItems exist for pagination (see queryDashboardComponents)
        while (lastSitesRetrieved !== 0 && components.length <= (2 * maxItems))
        {
            // Repository-tier ADMRemoteStore does not support wildcard queries for site/*/dashboard
            // so we must query site before looking up dashboard components
            // we don't want to load too much too early so incrementally increase result set size (no way to specify startIndex) 
            response = remote.call('/api/sites?size=' + (sitesRetrieved + 25)
                    + (args.filter !== null && String(args.filter).trim() !== '' ? ('&nf=' + args.filter) : ''));
            if (response.status.code === 200)
            {
                sites = JSON.parse(response.response);
                for (sidx = sitesRetrieved; sidx < sites.length && components.length <= (2 * maxItems); sidx++)
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
                    for (cidx = 0; cidx < componentResults.length && components.length <= (2 * maxItems); cidx++)
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
                lastSitesRetrieved = sites.length - sitesRetrieved;
                sitesRetrieved += lastSitesRetrieved;
            }
        }
    }
}

function buildComponentFilter(startIndex)
{
    var suppressedRegionIds, urlPattern, skipRemaining;

    suppressedRegionIds = [ 'title', 'navigation', 'full-width-dashlet' ];

    if (args.componentUrl !== undefined && args.componentUrl !== null && String(args.componentUrl).trim() !== '')
    {
        urlPattern = new RegExp(String(args.componentUrl), 'g');
    }

    skipRemaining = startIndex;

    return function componentFilter_apply(component)
    {
        var include = true;

        include = include && suppressedRegionIds.indexOf(String(component.regionId)) === -1;
        include = include && (urlPattern === undefined || urlPattern.test(String(component.getURL())));

        if (include === true && skipRemaining > 0)
        {
            skipRemaining--;
            include = false;
        }

        return include;
    };
}

function queryDashboardComponents()
{
    var components = [], maxItems, skipCount, componentFilter;

    skipCount = parseInt(args.startIndex || '0', 10);
    maxItems = parseInt(args.pageSize || '50', 10);

    componentFilter = buildComponentFilter(skipCount);

    queryUserDashboardComponents(components, componentFilter, maxItems);
    querySiteDashboardComponents(components, componentFilter, maxItems);

    model.totalRecords = skipCount + components.length;
    if (components.length > maxItems)
    {
        components.splice(maxItems, components.length - maxItems);
    }
    model.components = components;
    model.startIndex = skipCount;
}

queryDashboardComponents();
