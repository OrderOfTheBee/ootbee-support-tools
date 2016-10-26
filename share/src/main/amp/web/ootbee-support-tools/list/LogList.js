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
define([ 'dojo/_base/declare', 'alfresco/lists/AlfList', 'dojo/_base/lang', 'alfresco/util/functionUtils' ],
        function ootbeeSupportTools_list_LogList(declare, List, lang, functionUtils)
        {
            return declare([ List ], {

                loadDataPublishTopic : 'ALF_CRUD_GET_ALL',

                loadDataPublishPayload : {
                    url : 'data/console/ootbee-support-tools/log4j-settings-tail',
                    urlType : 'SHARE'
                },

                widgets : [ {
                    name : 'alfresco/lists/views/AlfListView',
                    config : {
                        widgetsForHeader : [ {
                            name : 'alfresco/lists/views/layouts/HeaderCell',
                            config : {
                                // TODO Report bug - missing padding style options
                                label : 'log-settings.tail.timestamp'
                            }
                        }, {
                            name : 'alfresco/lists/views/layouts/HeaderCell',
                            config : {
                                // TODO Report bug - missing padding style options
                                label : 'log-settings.tail.level'
                            }
                        }, {
                            name : 'alfresco/lists/views/layouts/HeaderCell',
                            config : {
                                // TODO Report bug - missing padding style options
                                label : 'log-settings.tail.message'
                            }
                        } ],
                        widgets : [ {
                            name : 'alfresco/lists/views/layouts/Row',
                            config : {
                                // TODO Report bug - property zebraStriping without effect
                                additionalCssClasses : 'zebra-striping',
                                widgets : [ {
                                    name : 'alfresco/lists/views/layouts/Cell',
                                    config : {
                                        additionalCssClasses : 'smallpad',
                                        widgets : [ {
                                            name : 'alfresco/renderers/Date',
                                            config : {
                                                simple : true,
                                                format : 'yyyy-mm-dd HH:MM:ss.lo',
                                                propertyToRender : 'timestamp.iso8601'
                                            }
                                        } ]
                                    }
                                }, {
                                    name : 'alfresco/lists/views/layouts/Cell',
                                    config : {
                                        additionalCssClasses : 'smallpad',
                                        widgets : [ {
                                            name : 'alfresco/renderers/Property',
                                            config : {
                                                propertyToRender : 'level'
                                            }
                                        } ]
                                    }
                                }, {
                                    name : 'alfresco/lists/views/layouts/Cell',
                                    config : {
                                        additionalCssClasses : 'smallpad',
                                        widgets : [ {
                                            name : 'alfresco/renderers/Property',
                                            config : {
                                                propertyToRender : 'message'
                                            }
                                        } ]
                                    }
                                } ]
                            }
                        } ]
                    }
                } ],

                postMixInProperties : function ootbeeSupportTools_list_LogList__postMixInProperties()
                {
                    this.inherited(arguments);

                    if (this.loadDataPublishPayload && this.loadDataPublishPayload.url)
                    {
                        if (this.loadDataPublishPayload.url.indexOf('?') === -1)
                        {
                            this.loadDataPublishPayload.url += '?';
                        }
                        else
                        {
                            this.loadDataPublishPayload.url += '&';
                        }
                        this.loadDataPublishPayload.url += 'uuid=' + this.generateUuid();
                    }
                },

                postCreate : function ootbeeSupportTools_list_LogList__postCreate()
                {
                    this.inherited(arguments);

                    this._loadTimer = functionUtils.addRepeatingFunction(lang.hitch(this, this.loadData), 'LONG');
                },

                onDataLoadSuccess : function ootbeeSupportTools_list_LogList__onDataLoadSuccess(payload)
                {
                    if (this.pendingLoadRequest === true)
                    {
                        this.requestInProgress = false;
                        this.pendingLoadRequest = false;
                        this.loadData();
                    }
                    else
                    {
                        this.currentData = {};
                        this.currentData.items = payload.response.events;

                        if (this.currentData.items.length > 0)
                        {
                            this.processLoadedData(payload.response);

                            // temporarily flip so data is appended
                            // (we don't enable it generally since we don't need other aspects) 
                            this.useInfiniteScroll = true;
                            this.renderView();
                            this.useInfiniteScroll = false;

                            this.retainPreviousItemSelectionState(this.currentData.items);
                        }

                        this.alfPublish(this.requestFinishedTopic, {});
                    }
                },

                destroy : function ootbeeSupportTools_list_LogList__destroy()
                {
                    this._loadTimer.remove();

                    this.inherited(arguments);
                }

            });
        });
