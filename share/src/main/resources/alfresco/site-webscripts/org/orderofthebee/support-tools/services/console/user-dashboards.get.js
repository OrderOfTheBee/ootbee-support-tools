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
function queryUserDashboards(pages, maxItems)
{
    var response, peopleRetrieved, lastPeopleRetrieved, people, pidx, person, page, componentResults, ccount, cidx;

    peopleRetrieved = 0;
    // note: use (2 * maxItems) as limit so we can determine if more than maxItems exist for pagination (see queryDashboardComponents)
    while (lastPeopleRetrieved !== 0 && pages.length <= (2 * maxItems))
    {
        // for some reason people.get uses both skipCount + startIndex, maxItems + maxResults
        // use 99 as pageSize since default Repository-tier userToAuthorityTransactionalCache is set to 100
        response = remote.call('/api/people?filter=' + (args.filter !== null && String(args.filter).trim() !== '' ? args.filter : '*')
                + '&pageSize=99&maxResults=99&skipCount=' + peopleRetrieved + '&startIndex=' + peopleRetrieved + '&sortBy=userName&dir=asc');
        if (response.status.code === 200)
        {
            people = JSON.parse(response.response).people;
            for (pidx = 0; pidx < people.length && pages.length <= (2 * maxItems); pidx++)
            {
                person = people[pidx];
                page = sitedata.getPage('user/' + person.userName + '/dashboard');
                if (page)
                {
                    componentResults = sitedata.findComponents('page', null, 'user/' + person.userName + '/dashboard', null);
                    ccount = componentResults.length;
                    for (cidx = 0; cidx < componentResults.length; cidx++)
                    {
                        switch (String(componentResults[cidx].modelObject.regionId))
                        {
                            case 'title':
                            case 'navigation':
                            case 'full-width-dashlet':
                                ccount--;
                                break;
                            default: // NO-OP
                        }
                    }

                    pages.push({
                        userName : person.userName,
                        userDisplayName : (person.firstName === null ? '' : person.firstName)
                                + (person.lastName === null ? '' : ((person.firstName !== null ? ' ' : '') + person.lastName)) + ' ('
                                + person.userName + ')',
                        surfPage : page.modelObject,
                        numberComponents : ccount
                    });
                }
            }
            peopleRetrieved += people.length;
            lastPeopleRetrieved = people.length;
        }
    }
}

function main()
{
    var pages = [], maxItems, skipCount;

    skipCount = parseInt(args.startIndex || '0', 10);
    maxItems = parseInt(args.pageSize || '50', 10);

    queryUserDashboards(pages, skipCount + maxItems);

    if (skipCount > 0)
    {
        pages.splice(0, skipCount);
    }
    model.totalRecords = skipCount + pages.length;
    if (pages.length > maxItems)
    {
        pages.splice(maxItems, pages.length - maxItems);
    }
    model.pages = pages;
    model.startIndex = skipCount;
}

main();
