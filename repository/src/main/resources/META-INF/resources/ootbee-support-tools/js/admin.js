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

/* global alert: false */

// The Admin root object has been extracted from the Alfresco admin-template.ftl
// to trim down page HTML sizes and promote clean separation of concerns
/* JavaScript global helper methods and event handlers */
var el = function el(id)
{
    return document.getElementById(id);
};

/* Admin namespace helper methods */
var Admin = Admin || {};
(function()
{

    /* private scoped values */
    var _dialog = null;
    var _dialogScrollPosition = null;
    var _messages = {};
    var _ids = {};

    /* publicly accessable helper functions */

    Admin.addMessage = function addMessage(key, message)
    {
        _messages[key] = message;
    };

    Admin.addMessages = function addMessages(obj)
    {
        var key;
        for (key in obj)
        {
            if (obj.hasOwnProperty(key))
            {
                _messages[key] = obj[key];
            }
        }
    };

    Admin.registerId = function registerId(key, id)
    {
        _ids[key] = id;
    };

    // disabled default CSRF config
    Admin.CSRF = {
        enabled: false,
        cookie: "",
        header: "",
        parameter: "",
        properties: {}
    };

    /**
     * Returns the CSRF token.
     *
     * Note! Make sure to use this method just before a request is made against the server since it might have been
     * updated in another browser tab or window.
     * 
     * @method CSRFToken
     * @return {String} The CSRF token or null if not enable or not defined.
     */
    Admin.CSRFToken = function CSRFToken()
    {
        var token = null, cookieName = Admin.CSRF.getCookie();
        if (cookieName)
        {
            var matches = document.cookie.match(new RegExp("(?:^|; )" + cookieName + "=([^;]*)"));
            if (matches)
            {
                // remove quotes to support Jetty app-server - bug where it quotes a valid cookie value see ALF-18823
                token = decodeURIComponent(matches[1]).replace(/"/g, '');
            }
        }
        return token;
    };

    Admin.CSRF.getCookie = function getCookie()
    {
        return Admin.substitute(Admin.CSRF.cookie, Admin.CSRF.properties || {});
    };

    Admin.CSRF.getParameter= function getParameter()
    {
        return Admin.substitute(Admin.CSRF.parameter, Admin.CSRF.properties || {});
    };

    Admin.CSRF.getHeader = function getHeader()
    {
        return Admin.substitute(Admin.CSRF.header, Admin.CSRF.properties || {});
    };

    /**
     * Simple string substitution helper. Replaces simple instances of templated strings {name} within a string from
     * a property object. Each key in the property object is replaced in the string with it's value if match is found.
     * 
     * @param str  String to replace into
     * @param properties Object of key/value pairs to replace templates values with
     */
    Admin.substitute = function substitute(str, properties)
    {
        for (var prop in properties)
        {
            str = str.replace("{" + prop + "}", properties[prop]);
        }
        return str;
    };

    /**
     * String trim helper
     * 
     * @param s
     *            {string} String to trim pre and post whitespace from
     * @return trimmed string value - returns empty string for null or undefined
     *         input
     */
    Admin.trim = function trim(s)
    {
        return s ? s.replace(/^\s+|\s+$/g, "") : "";
    };

    /**
     * String HTML encoding helper
     * 
     * @param s
     *            {string} String to HTML encode
     * @return encoded string value - returns empty string for null or undefined
     *         input
     */
    Admin.html = function html(s)
    {
        if (!s)
        {
            return "";
        }
        s = "" + s;
        return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#39;");
    };

    /**
     * Helper to add a named DOM event listener function to an object
     * 
     * @param obj
     *            {Element} DOM object to add the named event listener too
     * @param event
     *            {string} Event name e.g. "click"
     * @param fn
     *            {function} Event handler function to invoke
     */
    Admin.addEventListener = function addEventListener(obj, event, fn)
    {
        if (obj.addEventListener)
        {
            obj.addEventListener(event, fn, false);
        }
        else
        {
            obj.attachEvent("on" + event, fn);
        }
    };

    /**
     * DIV Section toggle event handler
     * 
     * @param a
     *            {Element} Anchor element that fired the toggle event
     */
    Admin.sectionToggle = function sectionToggle(a)
    {
        // walk the DOM nodes to get to the toggle div element
        var div = a.parentElement.parentElement.getElementsByTagName("div")[0].getElementsByTagName("div")[0];
        if (Admin.toggleHiddenElement(div))
        {
            // will now be open
            a.innerHTML = "\u25BC";
        }
        else
        {
            // will now be closed
            a.innerHTML = "\u25BA";
        }
    };

    /**
     * Toggle the "hidden" class for a given DOM element
     * 
     * @param el
     *            {Element} Element to add/remove the "hidden" class to
     */
    Admin.toggleHiddenElement = function toggleHiddenElement(el)
    {
        var clazzes = el.className.split(" "), foundHidden = false;
        for (var i = 0; i < clazzes.length; i++)
        {
            // found the toggle el - switch display class
            foundHidden = clazzes[i] === "hidden";
            if (foundHidden)
            {
                clazzes.splice(i, 1);
                break;
            }
        }
        if (!foundHidden)
        {
            clazzes.push("hidden");
        }

        // apply new classes to the el
        el.className = clazzes.join(" ");

        return foundHidden;
    };

    Admin.addClass = function addClass(el, c)
    {
        var clazzes = el.className.split(" "), found = false;
        for (var i = 0; i < clazzes.length; i++)
        {
            found = clazzes[i] === c;
            if (found)
            {
                break;
            }
        }
        if (!found)
        {
            el.className += " " + c;
        }
    };

    Admin.removeClass = function removeClass(el, c)
    {
        var clazzes = el.className.split(" ");
        for (var i = 0; i < clazzes.length; i++)
        {
            if (clazzes[i] === c)
            {
                clazzes.splice(i, 1);
                break;
            }
        }
        el.className = clazzes.join(" ");
    };

    /**
     * Add a row to an existing table
     * 
     * @param table
     *            {Element} The table DOM element
     * @param row
     *            {Array} Array of cell data to add
     * @param index
     *            {Integer} Index to insert at - default is add to end
     */
    Admin.addTableRow = function addTableRow(table, row, index)
    {
        var tr = table.insertRow(index !== undefined ? index : -1);
        for (var i = 0, td; i < row.length; i++)
        {
            td = tr.insertCell(-1);
            td.innerHTML = row[i];
        }
    };

    /**
     * Show the contents of a URL in a dialog styled IFrame
     * 
     * @param url
     *            {String} URL in the same domain to display in the dialog frame
     */
    Admin.showDialog = function showDialog(url, compact)
    {
        if (!_dialog)
        {
            // generate IFrame for the dialog contents
            var iframe = document.createElement('iframe');
            iframe.name = iframe.id = 'admin-dialog';
            iframe.src = url;
            iframe.frameBorder = 0;
            iframe.className = (compact ? 'dialog compact' : 'dialog');
            _dialog = iframe;

            // generate a shield over the background elements
            var shield = document.createElement('div');
            shield.id = "shield";
            shield.className = 'shield';

            // display the elements and hide background scrollbars
            document.body.appendChild(shield);
            document.body.appendChild(iframe);
            document.body.style.overflow = 'hidden';

            // scroll to top and record last Y scroll position
            _dialogScrollPosition = window.pageYOffset;
            window.scrollTo(0, 0);
        }
    };

    /**
     * Remove any existing dialog frame and restore background elements
     */
    Admin.removeDialog = function removeDialog(state, args)
    {
        if (_dialog)
        {
            // remove the dialog IFrame and shield from the DOM
            _dialog.parentNode.removeChild(_dialog);
            var shield = el("shield");
            shield.parentNode.removeChild(shield);

            // restore background scrollbars
            document.body.style.overflow = "";

            // scroll back
            window.scrollTo(0, _dialogScrollPosition);

            _dialog = null;

            if (state)
            {
                switch (state)
                {
                    case "saved":
                        Admin.onDialogFinished(args);
                        break;
                }
            }
        }
    };

    /**
     * Default onDialogFinished event handler with empty impl. Dialog templates
     * can override this to add additional processing.
     */
    /* jshint unused: false */
    Admin.onDialogFinished = function onDialogFinished(args)
    {
    };
    /* jshint unused: true */

    /**
     * Ajax request helper
     * 
     * @param config
     *            {Object} Of the form: { method: "GET|POST|PUT|DELETE", url:
     *            "endpointurl", data: {object to be posted},
     *            requestContentType: "application/json", responseContentType:
     *            "application/json", fnSuccess: successHandler(response),
     *            fnFailure: failureHandler(response) }
     */
    Admin.request = function request(config)
    {
        var req = new XMLHttpRequest();
        var data = config.data || {};
        if (req.overrideMimeType)
        {
            req.overrideMimeType((config.responseContentType ? config.responseContentType : "application/json") + "; charset=utf-8");
        }
        req.open(config.method ? config.method : "GET", config.url);
        if ((config.method === "POST" || config.method === "PUT") && Admin.CSRF.enabled)
        {
            req.setRequestHeader(Admin.CSRF.getHeader(), Admin.CSRFToken());
        }
        req.setRequestHeader("Content-Type", (config.requestContentType ? config.requestContentType : "application/json") + ";charset=UTF-8");
        req.setRequestHeader("Accept", config.responseContentType ? config.responseContentType : "application/json");
        req.onreadystatechange = function()
        {
            if (req.readyState === 4)
            {
                if (req.status >= 200 && req.status < 300)
                {
                    // success - call handler
                    if (config.fnSuccess)
                    {
                        var json;
                        try
                        {
                            json = !config.responseContentType || config.responseContentType === "application/json" ? JSON
                                    .parse(req.responseText) : null;
                        }
                        catch (e)
                        {
                            // Developer JSON response error (e.g. malformed
                            // response text)
                            alert(e + "\n" + req.status + "\n" + req.responseText);
                        }
                        config.fnSuccess.call(this, {
                            responseText : req.responseText,
                            responseStatus : req.status,
                            responseJSON : json
                        });
                    }
                }
                else
                {
                    // failure - call handler
                    if (config.fnFailure)
                    {
                        config.fnFailure.call(this, {
                            responseText : req.responseText,
                            responseStatus : req.status
                        });
                    }
                    else
                    {
                        // default error handler
                        alert(_messages.requestError + "\n\n" + req.responseText + "\n\n" + req.responseStatus);
                    }
                }
            }
        };
        if (config.method === "POST" || config.method === "PUT")
        {
            // TODO: support form url encoded
            req.send(JSON.stringify(data));
        }
        else
        {
            req.send(null);
        }
    };

    /**
     * Perform binary file upload to a given service URL. Uses hidden iframe
     * technique to give an Ajax like upload with support for earlier browser
     * APIs.
     * 
     * @param fileId
     *            ID of the File element to POST
     * @param url
     *            URL of the service endpoint
     * @param successHandler
     *            Success handler function - passed JSON object response as
     *            argument
     * @param failureHandler
     *            Failure handler function - no arguments
     */
    Admin.uploadFile = function uploadFile(fileId, url, successHandler, failureHandler)
    {
        var file = el(fileId), ownerDocument = file.ownerDocument, pwindow = ownerDocument.defaultView || ownerDocument.parentWindow, iframe = ownerDocument
                .createElement("iframe");
        iframe.style.display = "none";
        iframe.name = "AdminUploadFrame";
        iframe.id = iframe.name;
        ownerDocument.body.appendChild(iframe);

        // target the frame properly in IE
        pwindow.frames[iframe.name].name = iframe.name;

        Admin.addEventListener(iframe, 'load', function()
        {
            var frame, content, json;
            
            frame = document.getElementById(iframe.name);
            if (frame.contentDocument)
            {
                content = frame.contentDocument.body.textContent;
            }
            else if (frame.contentWindow)
            {
                content = frame.contentWindow.document.body.textContent;
            }
            try
            {
                if (successHandler)
                {
                    json = JSON.parse(content);
                    successHandler.call(this, json);
                }
            }
            catch (e)
            {
                if (failureHandler)
                {
                    failureHandler.call(this);
                }
            }
        });

        var form = ownerDocument.createElement("form");
        ownerDocument.body.appendChild(form);
        form.style.display = "none";
        form.method = "post";
        form.encoding = "multipart/form-data";
        form.enctype = "multipart/form-data";
        form.target = iframe.name;
        form.action = url;
        if (Admin.CSRF.enabled)
        {
            form.action += "?" + Admin.CSRF.getParameter() + "=" + encodeURIComponent(Admin.CSRFToken());
        }
        form.appendChild(file);
        form.submit();
    };

    /**
     * Switch an input field between test and password to show and hide the
     * text.
     * 
     * @param id
     *            {String} ID of the password field
     * @param button
     *            {Element} The button that was clicked
     */
    Admin.togglePassword = function togglePassword(id, button)
    {
        var field = el(id);

        if (field.type === "password")
        {
            button.value = Admin.html(_messages.passwordHide);
            field.type = "text";
        }
        else
        {
            button.value = Admin.html(_messages.passwordShow);
            field.type = "password";
        }
    };

    /* Page load handler */
    Admin.addEventListener(window, 'load', function()
    {
        // get the root form element
        var form = el(_ids.formId);

        // add CSRF token if enabled
        if (Admin.CSRF.enabled)
        {
            var url = form.attributes.action.value;
            url += (url.lastIndexOf('?') === -1 ? "?" : "&") + Admin.CSRF.getParameter() + "=" + encodeURIComponent(Admin.CSRFToken());
            form.attributes.action.value = url;
        }

        // ensure ENTER press in a Form field doesn't submit the Form
        Admin.addEventListener(form, 'keypress', function(e)
        {
            if (e.keyCode === 13)
            {
                if(e.preventDefault)
                {
                    e.preventDefault();
                }
                else
                {
                    e.returnValue = false;
                }
            }
            return true;
        });

        // highlight first form input field
        var fields = form.getElementsByTagName("input");
        for (var i = 0; i < fields.length; i++)
        {
            if (fields[i].type === "text" || fields[i].type === "textarea")
            {
                if (!fields[i].readOnly)
                {
                    fields[i].focus();
                    break;
                }
            }
        }

        // escape key handler to close dialog page
        Admin.addEventListener(document, 'keypress', function(e)
        {
            if (e.keyCode === 27)
            {
                top.window.Admin.removeDialog();
            }
        });
    });
})();
