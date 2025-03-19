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

/* global Admin: false, el: false, alert: false */

// The AdminTT root object has been extracted from the Alfresco Support Tools
// admin-testtransform.get.html.ftl trim down page HTML sizes and promote clean
// separation of concerns
/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    var textareas, listenerFn, i, ta;
    
    listenerFn = function(e)
    {
        if (e.keyCode === 13)
        {
            this.value = this.value + '\r\n';
        }
        return true;
    };
    
    textareas = document.getElementsByTagName('textarea');
    for (i = 0; i < textareas.length; i++)
    {
        ta = textareas[i];
        Admin.addEventListener(ta, 'keypress', listenerFn);
    }
});

/**
 * Test Transform Component
 */
var AdminTT = AdminTT || {};

(function()
{
    var serviceUrl, messages = {};
    
    AdminTT.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminTT.addMessages = function addMessage(oMessages)
    {
        var key;
        if (oMessages !== undefined && oMessages !== null)
        {
            for (key in oMessages)
            {
                if (oMessages.hasOwnProperty(key))
                {
                    messages[key] = oMessages[key];
                }
            }
        }
    };

    function handleRequestError(response) {
        var message, jsonResponse;

        message = messages['message.genericFailure'].replace(/\{0\}/, response.responseStatus);
        try
        {
            jsonResponse = JSON.parse(response.responseText);
            if (jsonResponse.message)
            {
                message = messages['message.jsonFailure'].replace(/\{0\}/, jsonResponse.message);
            }
        }
        catch(ignore)
        {
            // no-op
        }
        alert(message);
    }

    AdminTT.showInDialog = function showInDialog(operation)
    {
        var url = serviceUrl + '-content-service-details?operation=' + operation;

        for (var i = 1; i < arguments.length; i++)
        {
            url += '&arg' + (i - 1) + '=' + encodeURIComponent(arguments[i]);
        }

        Admin.showDialog(url);
    };

    AdminTT.findApplicableRenditions = function findApplicableRenditions()
    {
        var sourceMimetype, sourceSize, url, rqData;

        sourceMimetype = el('renditionService2DefinitionCheck-mimetype').value;
        sourceSize = el('renditionService2DefinitionCheck-size').value;

        url = serviceUrl + '/renditionService2/findApplicableRenditions';
        rqData = {
            sourceMimetype: sourceMimetype,
            sourceSize: sourceSize
        };

        Admin.request({
            method: 'POST',
            url: url,
            data: rqData,
            fnSuccess: function (response) {
                var resultsTopContainer, resultContainer, renditions, idx, li;

                resultsTopContainer = el('renditionService2DefinitionCheck-applicableRenditions');
                Admin.removeClass(resultsTopContainer, 'hidden');

                resultContainer = el('renditionService2DefinitionCheck-applicableRenditions-list');
                while (resultContainer.childNodes.length > 0)
                {
                    resultContainer.removeChild(resultContainer.childNodes[0]);
                }

                renditions = response.responseJSON.applicableRenditions;
                for (idx = 0; idx < renditions.length; idx++)
                {
                    li = document.createElement('li');
                    li.innerHTML = Admin.html(renditions[idx]);
                    resultContainer.appendChild(li);
                }

                if (renditions.length === 0)
                {
                    li = document.createElement('li');
                    li.innerHTML = Admin.html(messages['messages.none']);
                    resultContainer.appendChild(li);
                }
            },
            fnFailure: handleRequestError
        });
    };
    
    AdminTT.toggleRenditionDetails = function toggleRenditionDetails(definition)
    {
        var renditionDiv;

        renditionDiv = el('renditionService2DefinitionListDetails-' + definition);
        Admin.toggleHiddenElement(renditionDiv);
    };

    function loadRegistryCheckOptions(registryKey)
    {
        var options, optionLines, lineIdx, line, sepIdx, key, value;

        options = el(registryKey + '-transformRegistry-checkOptions').value;

        optionLines = options.split(/\n/g);
        options = {};
        for (lineIdx = 0; lineIdx < optionLines.length; lineIdx++)
        {
            if (/^\s*[^\s=]+\s*=.*/.test(optionLines[lineIdx]))
            {
                line = optionLines[lineIdx];
                sepIdx = line.indexOf('=');
                key = line.substring(0, sepIdx);
                key = key.replace(/(^\s+)|(\s+$)/g, '');
                value = line.substring(sepIdx + 1);
                value = value.replace(/(^\s+)|(\s+$)/g, '');
                options[key] = value;
            }
        }
        
        return options;
    }

    AdminTT.findTransformDetails = function findTransformDetails(registryKey)
    {
        var sourceMimetype, sourceSize, targetMimetype, options, url, rqData;

        sourceMimetype = el(registryKey + '-transformRegistry-checkSourceType').value;
        sourceSize = el(registryKey + '-transformRegistry-checkSourceSize').value;
        targetMimetype = el(registryKey + '-transformRegistry-checkTargetType').value;
        options = loadRegistryCheckOptions(registryKey);

        url = serviceUrl + '/renditionService2/findTransformDetails';
        rqData = {
            sourceMimetype: sourceMimetype,
            targetMimetype: targetMimetype,
            sourceSize: sourceSize,
            options: options,
            registryKey: registryKey
        };

        Admin.request({
            method: 'POST',
            url: url,
            data: rqData,
            fnSuccess: function (response) {
                var resultContainer, transformersList, maxSizeValueEl, transformerNameValueEl, idx, transformer, li, liText;

                resultContainer = el(registryKey + '-transformRegistry-result-container');
                Admin.removeClass(resultContainer, 'hidden');

                transformersList = el(registryKey + '-transformRegistry-result-transformers');
                maxSizeValueEl = el(registryKey + '-transformRegistry-result-maxSize-value');
                transformerNameValueEl = el(registryKey + '-transformRegistry-result-transformerName-value');

                while (transformersList.childNodes.length > 0)
                {
                    transformersList.removeChild(transformersList.childNodes[0]);
                }

                for (idx = 0; idx < response.responseJSON.transformers.length; idx++)
                {
                    transformer = response.responseJSON.transformers[idx];
                    li = document.createElement('li');

                    liText = transformer.name;
                    liText += ', ' + messages['transform.transformRegistry.supportedTransforms.transformationPriority'] + ': ' + transformer.priority;
                    liText += ', ' + messages['transform.transformRegistry.supportedTransforms.transformationSourceSizeLimit'] + ': ' + transformer.maxSourceSizeBytes;
                    li.innerHTML = Admin.html(liText);

                    transformersList.appendChild(li);
                }

                if (response.responseJSON.transformers.length === 0)
                {
                    li = document.createElement('li');
                    li.innerHTML = Admin.html(messages['message.none']);
                    transformersList.appendChild(li);
                }

                maxSizeValueEl.innerHTML = Admin.html(response.responseJSON.maxSourceSizeBytes || '');
                transformerNameValueEl.innerHTML = Admin.html(response.responseJSON.transformerName || '');
            },
            fnFailure: handleRequestError
        });
    };

    AdminTT.probeLocalTransform = function probeLocalTransform(localTransformName, live)
    {
        var url, rqData;

        url = serviceUrl + '/renditionService2/probeLocalTransform';
        rqData = {
            localTransformName: localTransformName,
            live: live
        };

        Admin.request({
            method: 'POST',
            url: url,
            data: rqData,
            fnSuccess: function (response) {
                alert(response.responseJSON.probeResponse);
            },
            fnFailure: handleRequestError
        });
    };

    AdminTT.showLocalTransformLogs = function showLocalTransformLogs(localTransformName)
    {
        var url = serviceUrl + '/renditionService2/' + encodeURIComponent(localTransformName) + '/localTransformLogs';
        Admin.showDialog(url);
    };

    AdminTT.toggleTransformDetailsTransformations = function toggleTransformDetailsTransformations(registryKey, transformerName)
    {
        var transformationsDetailDiv;
        
        transformationsDetailDiv = el(registryKey + '-transformRegistry-transformDetails-' + transformerName + '-details');

        Admin.toggleHiddenElement(transformationsDetailDiv);
    };
})();