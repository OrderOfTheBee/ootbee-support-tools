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
/* global jsconsole: false */
jsconsole.setSpace(space);

/* exported recurse */
function recurse(scriptNode, processorOrOptions)
{
    var result, recurseInternal, options;

    result = [];

    recurseInternal = function(scriptNode, options, path, level)
    {
        var index, c, child, childPath, procResult;

        index = 0;

        if (level < options.maxlevel)
        {
            for (c = 0; c < scriptNode.children.length; c++)
            {
                child = scriptNode.children[c];
                childPath = path + '/' + child.name;

                if (typeof options.filter !== 'function' || options.filter(child, childPath, index, level))
                {
                    procResult = options.process(child, childPath, index, level);
                    if (procResult !== undefined)
                    {
                        result.push(procResult);
                    }
                }

                if (child.isContainer)
                {
                    if (typeof options.branch !== 'function' || options.branch(child, childPath, index, level))
                    {
                        recurseInternal(child, options, childPath, level + 1);
                    }
                }

                index++;
            }
        }
    };

    options = {};
    if (processorOrOptions === undefined)
    {
        options.process = function(node) { return node; };
    }
    else if (typeof processorOrOptions === 'function')
    {
        options.process = processorOrOptions;
    }
    else
    {
        options = processorOrOptions;
    }

    if (options.maxlevel === undefined)
    {
        options.maxlevel = 100;
    }

    recurseInternal(scriptNode, options, '', 0);

    return result;
}