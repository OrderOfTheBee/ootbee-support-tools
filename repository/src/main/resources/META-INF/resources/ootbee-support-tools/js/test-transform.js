/**
 * Copyright (C) 2016, 2017 Axel Faust / Markus Joos
 * Copyright (C) 2016, 2017 Order of the Bee
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
 * Linked to Alfresco Copyright
 * (C) 2005-2017 Alfresco Software Limited.
 */

/* global Admin: false */

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
            this.value = this.value + "\r\n";
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
    var serviceUrl;
    
    AdminTT.setServiceUrl = function setServiceURL(url)
    {
        serviceUrl = url;
    };

    AdminTT.showInDialog = function showInDialog(operation)
    {
        var url = serviceUrl + "-details?operation=" + operation;

        for (var i = 1; i < arguments.length; i++)
        {
            url += "&arg" + (i - 1) + "=" + encodeURIComponent(arguments[i]);
        }

        Admin.showDialog(url);
    };

})();
