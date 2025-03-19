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

/* global define: false */
define([ 'dojo/_base/declare', 'alfresco/lists/AlfList', 'dojo/_base/lang', 'alfresco/util/functionUtils' ],
        function ootbeeSupportTools_list_LogList(declare, List, lang, functionUtils)
        {
            return declare([ List ], {

                loadDataPublishTopic : 'ALF_CRUD_GET_ALL',

                loadDataPublishPayloadDefault : {
                    url : 'data/console/ootbee-support-tools/log4j-settings-tail',
                    urlType : 'SHARE'
                },

                widgetsDefault : [ {
                    name : 'alfresco/lists/views/AlfListView',
                    config : {
                        widgetsForHeader : [ {
                            name : 'alfresco/lists/views/layouts/HeaderCell',
                            config : {
                                // TODO Report bug - missing padding style options
                                label : 'log-settings.tail.timestamp',
                                // force column width for timestamp not to be wrapped
                                style : 'min-width: 28ex;'
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
                                label : 'log-settings.tail.logger'
                            }
                        }, {
                            name : 'alfresco/lists/views/layouts/HeaderCell',
                            config : {
                                // TODO Report bug - missing padding style options
                                label : 'log-settings.tail.message'
                            }
                        } ],
                        widgets : [ {
                            name : 'ootbee-support-tools/list/LogLevelHighlightedRow',
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
                                            name : 'ootbee-support-tools/renderers/LoggerProperty'
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

                constructor : function ootbeeSupportTools_list_LogList__constructor()
                {
                    // redefine the properties to avoid setting default values to the prototype
                    if (this.widgets === null)
                    {
                        Object.defineProperty(this, 'widgets', {
                            value : lang.clone(this.widgetsDefault),
                            enumerable : true,
                            writable : true,
                            configurable : true
                        });
                    }
                    
                    if (this.loadDataPublishPayload === null)
                    {
                        Object.defineProperty(this, 'loadDataPublishPayload', {
                            value : lang.clone(this.loadDataPublishPayloadDefault),
                            enumerable : true,
                            writable : true,
                            configurable : true
                        });
                    }
                },

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
                    else if (payload.response.events.length === 0)
                    {
                        this.hideLoadingMessage();
                        this.alfPublish(this.requestFinishedTopic, {});
                    }
                    else
                    {
                        this.currentData = {};
                        this.currentData.items = payload.response.events;

                        this.processLoadedData(payload.response);

                        // temporarily flip so data is appended
                        // (we don't enable it generally since we don't need other aspects)
                        this.useInfiniteScroll = true;
                        this.renderView();
                        this.useInfiniteScroll = false;

                        this.retainPreviousItemSelectionState(this.currentData.items);

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
