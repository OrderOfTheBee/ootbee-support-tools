/**
 * Copyright (C) 2016 Axel Faust Copyright (C) 2016 Order of the Bee
 * 
 * This file is part of Community Support Tools
 * 
 * Community Support Tools is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Community Support Tools is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Community Support Tools. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco Copyright (C) 2005-2016 Alfresco Software Limited.
 */

/* global define: false */
define([ 'dojo/_base/declare', 'alfresco/services/BaseService', 'alfresco/core/CoreXhr', 'dojo/_base/lang', 'service/constants/Default',
        'dojo/dom-construct' ], function ootbeeSupportTools_service_LogFileService(declare, BaseService, CoreXhr, lang, Constants,
        domConstruct)
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

        onDowloadLogFilesZip : function ootbeeSupportTools_service_LogFileService__onDownloadLogFilesZip()
        {
            // TODO similar to downloadIFrame: create invisible form and use that for POST-based download
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
            }

            return path;
        }
    });
});
