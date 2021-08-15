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