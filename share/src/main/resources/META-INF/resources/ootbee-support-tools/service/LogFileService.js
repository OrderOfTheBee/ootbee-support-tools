/**
 * Copyright (C) 2016 - 2025 Order of the Bee
 * 
 * This file is part of OOTBee Support Tools
 * 
 * OOTBee Support Tools is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * OOTBee Support Tools is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with OOTBee Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 */

/* global define: false */
define([ 'dojo/_base/declare', 'alfresco/services/BaseService', 'alfresco/core/CoreXhr', 'dojo/_base/lang', 'dojo/_base/array',
        'service/constants/Default', 'dojo/dom-construct' ], function ootbeeSupportTools_service_LogFileService(declare, BaseService,
        CoreXhr, lang, array, Constants, domConstruct)
{
    return declare([ BaseService, CoreXhr ], {

        DOWNLOAD_LOG_FILES_ZIP_TOPIC : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILES_ZIP',

        DOWNLOAD_LOG_FILE_TOPIC : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILE',

        DELETE_LOG_FILE_TOPIC : 'OOTBEE_SUPPORT_TOOLS_DELETE_LOG_FILE',

        registerSubscriptions : function ootbeeSupportTools_service_LogFileService__registerSubscriptions()
        {
            this.alfSubscribe(this.DOWNLOAD_LOG_FILES_ZIP_TOPIC, lang.hitch(this, this.onDownloadLogFilesZip));
            this.alfSubscribe(this.DOWNLOAD_LOG_FILE_TOPIC, lang.hitch(this, this.onDownloadLogFile));
            this.alfSubscribe(this.DELETE_LOG_FILE_TOPIC, lang.hitch(this, this.onDeleteLogFile));
        },

        onDownloadLogFilesZip : function ootbeeSupportTools_service_LogFileService__onDownloadLogFilesZip(payload)
        {
            var baseUrl, url;

            if (payload.urlType === 'PROXY')
            {
                baseUrl = Constants.PROXY_URI;
            }
            else if (payload.urlType === 'SHARE')
            {
                baseUrl = Constants.URL_SERVICECONTEXT;
            }
            else if (payload.urlType === 'CONTEXT')
            {
                baseUrl = Constants.URL_CONTEXT;
            }

            if (baseUrl !== undefined && lang.isString(payload.baseUrl))
            {
                url = baseUrl + payload.baseUrl + '?' + this.getCsrfParameter() + "=" + encodeURIComponent(this.getCsrfToken());
            }

            if (url !== undefined && lang.isArray(payload.selectedItems))
            {
                if (this.downloadZipForm)
                {
                    document.body.removeChild(this.downloadZipForm);
                }

                this.downloadZipForm = domConstruct.create('form', {
                    id : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILEs_ZIP_FORM',
                    action : url,
                    method : 'POST',
                    enctype : 'multipart/form-data',
                    'accept-charset' : 'utf-8',
                    style : 'display:none'
                }, document.body);

                array.forEach(payload.selectedItems, lang.hitch(this,
                        function ootbeeSupportTools_service_LogFileService__onDownloadLogFilesZip_forEachSelectedItem(logFile)
                        {
                            domConstruct.create('input', {
                                name : 'paths',
                                type : 'checkbox',
                                checked : true,
                                value : logFile.path
                            }, this.downloadZipForm);
                        }));

                this.downloadZipForm.submit();
            }
        },

        onDownloadLogFile : function ootbeeSupportTools_service_LogFileService__onDownloadLogFile(payload)
        {
            var baseUrl, path, url;

            if (payload.urlType === 'PROXY')
            {
                baseUrl = Constants.PROXY_URI;
            }
            else if (payload.urlType === 'SHARE')
            {
                baseUrl = Constants.URL_SERVICECONTEXT;
            }
            else if (payload.urlType === 'CONTEXT')
            {
                baseUrl = Constants.URL_CONTEXT;
            }

            path = this._toPath(payload);

            if (lang.isString(path) && baseUrl !== undefined && lang.isString(payload.baseUrl))
            {
                url = baseUrl + payload.baseUrl + '/' + path;
            }

            if (url !== undefined)
            {
                if (this.downloadIFrame)
                {
                    this.downloadIFrame.src = url + '?a=true';
                }
                else
                {
                    this.downloadIFrame = domConstruct.create('iframe', {
                        id : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILE_IFRAME',
                        src : url + '?a=true',
                        style : 'display:none'
                    }, document.body);
                }
            }
        },

        onDeleteLogFile : function ootbeeSupportTools_service_LogFileService__onDeleteLogFile(payload)
        {
            var baseUrl, path, url;

            if (payload.urlType === 'PROXY')
            {
                baseUrl = Constants.PROXY_URI;
            }
            else if (payload.urlType === 'SHARE')
            {
                baseUrl = Constants.URL_SERVICECONTEXT;
            }
            else if (payload.urlType === 'CONTEXT')
            {
                baseUrl = Constants.URL_CONTEXT;
            }

            path = this._toPath(payload);

            if (lang.isString(path) && baseUrl !== undefined && lang.isString(payload.baseUrl))
            {
                url = baseUrl + payload.baseUrl + '/' + path;
            }

            if (url !== undefined)
            {
                this
                        .serviceXhr({
                            url : url,
                            method : 'DELETE',
                            // default callback in CoreXhr annoyingly exposes too much detail data
                            // it also requires too much mapping
                            // someone ought to provide a better CoreXhr mixin one day...
                            successCallback : function ootbeeSupportTools_service_LogFileService__onDeleteLogFile_successCallback()
                            {
                                if (payload.alfSuccessTopic)
                                {
                                    this.alfPublish(payload.alfSuccessTopic, {}, false, false, payload.alfSuccessScope
                                            || payload.alfResponseScope);
                                }

                                if (payload.alfResponseTopic)
                                {
                                    this.alfPublish(payload.alfResponseTopic + '_SUCCESS', {}, false, false, payload.alfSuccessScope
                                            || payload.alfResponseScope);
                                }
                            }
                        });
            }
        },

        _toPath : function ootbeeSupportTools_service_LogFileService__toPath(logFileItem)
        {
            var pathFragments, path, idx;

            if (lang.isString(logFileItem.path))
            {
                pathFragments = logFileItem.path.split(/\//);
                for (idx = 0; idx < pathFragments.length; idx++)
                {
                    pathFragments[idx] = encodeURIComponent(pathFragments[idx]).replace(/:/g, '%3A');
                }
                path = pathFragments.join('/');
                // cleanup any duplicate slashes
                path = path.replace(/\/+/g, '/');
                // path should not start with a slash to avoid double-slash in URL
                // (leading slash will be re-added as part of file path resolution in backend)
                if (path.substr(0, 1) === '/')
                {
                    path = path.substr(1);
                }
            }

            return path;
        }
    });
});
