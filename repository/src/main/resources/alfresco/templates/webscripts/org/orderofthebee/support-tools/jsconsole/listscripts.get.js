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
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */

var prepareOutput = function prepareOutput(folder)
{
    var scriptList, children, idx, node;

    scriptList = [];

    children = folder.children;
    children.sort(function(a, b) {
        return String(a.name).localeCompare(b.name);
    });

    for (idx = 0; idx < children.length; idx++)
    {
        node = children[idx];

        if (node.isContainer)
        {
            scriptList.push({
                item: node,
                children: prepareOutput(node)
            });
        }
        else
        {
            scriptList.push({
                item: node
            });
        }
    }

    return scriptList;
};

function findAvailableScripts()
{
    var scriptFolder = search.selectNodes('/app:company_home/app:dictionary/app:scripts')[0];
    if (scriptFolder)
    {
        model.scripts = prepareOutput(scriptFolder);
    }
    else
    {
        model.scripts = [];
    }
}

findAvailableScripts();