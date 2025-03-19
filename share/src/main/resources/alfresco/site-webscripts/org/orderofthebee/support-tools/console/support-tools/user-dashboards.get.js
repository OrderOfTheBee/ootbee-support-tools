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
function buildPanel()
{
    var model = {
        name : 'alfresco/core/ProcessWidgets',
        id : 'DASHBOARDS_PANEL',
        config : {
            widgets : [
                    {
                        name : 'alfresco/lists/Paginator',
                        config : {
                            documentsPerPage : 20,
                            pageSizes : [ 20, 50, 100 ],
                            compactMode : true,
                            pubSubScope : 'DASHBOARD_LIST/',
                            style : 'text-align:center;',
                            widgetsBefore : [ {
                                name : 'ootbee-support-tools/menu/SelectedItemsMenuBarPopup',
                                config : {
                                    // can't believe "selected-items.label" is not a global label
                                    // we prefix it to not mess with any global labels others may have added
                                    label : 'dashboard-components.selected-items.label',
                                    passive : false,
                                    itemKeyProperty : 'id',
                                    widgets : [ {
                                        id : 'RESET_DASHBOARDS',
                                        name : 'ootbee-support-tools/menu/SelectedItemsMenuItem',
                                        config : {
                                            label : 'user-dashboards.action.resetDashboards',
                                            iconClass : "alf-doclib-action alf-delete-icon",
                                            itemKeyProperty : 'id',
                                            // not really a create but ALF_CRUD_CREATE uses POST
                                            publishTopic : 'ALF_CRUD_CREATE',
                                            publishGlobal : true,
                                            publishPayloadType : 'PROCESS',
                                            publishPayloadModifiers : [ 'processCurrentItemTokens' ],
                                            publishPayload : {
                                                urlType : 'SHARE',
                                                url : 'data/console/ootbee-support-tools/dashboards/reset',
                                                dashboardIds : '{selectedItemKeys}',
                                                // just to avoid an unnecessary warning
                                                alfResponseTopic : String(Packages.java.util.UUID.randomUUID())
                                            }
                                        }
                                    } ]
                                }
                            } ]
                        }
                    },
                    {
                        name : 'alfresco/lists/AlfFilteredList',
                        config : {
                            pubSubScope : 'DASHBOARD_LIST/',
                            // TODO Report bug - CrudService should not hard-code topic
                            reloadDataTopic : 'ALF_DOCLIST_RELOAD_DATA',
                            loadDataPublishTopic : 'ALF_CRUD_GET_ALL',
                            loadDataPublishPayload : {
                                url : 'data/console/ootbee-support-tools/user-dashboards',
                                urlType : 'SHARE'
                            },
                            // TODO Report enhancement - filtering should not require these form topic cludges
                            filteringTopics : [ '_valueChangeOf_USER' ],
                            widgetsForFilters : [ {
                                name : 'alfresco/forms/controls/TextBox',
                                config : {
                                    // TODO Report enhancement - filter widgets should align properly
                                    // TODO Report enhancement - simple width customisation
                                    style : 'vertical-align:top;',
                                    fieldId : 'USER',
                                    name : 'filter',
                                    label : 'user-dashboards.user',
                                    placeHolder : 'user-dashboards.user.filterPlaceHolder'
                                }
                            } ],
                            usePagination : true,
                            currentPageSize : 20,
                            itemsProperty : 'dashboards',
                            widgets : [ {
                                name : 'alfresco/lists/views/AlfListView',
                                config : {
                                    additionalCssClasses : 'bordered',
                                    widgetsForHeader : [ {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            label : ''
                                        }
                                    }, {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            // TODO Report bug - missing padding style options
                                            label : 'user-dashboards.user'
                                        }
                                    }, {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            label : 'user-dashboards.id'
                                        }
                                    }, {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            label : 'user-dashboards.template'
                                        }
                                    }, {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            label : 'user-dashboards.componentCount'
                                        }
                                    }, {
                                        name : 'alfresco/lists/views/layouts/HeaderCell',
                                        config : {
                                            label : ''
                                        }
                                    } ],
                                    widgets : [ {
                                        name : 'alfresco/lists/views/layouts/Row',
                                        config : {
                                            // TODO Report bug - property zebraStriping without effect
                                            additionalCssClasses : 'zebra-striping',
                                            // TODO Report enhancement - simple CellProperty widget
                                            widgets : [
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Selector',
                                                                itemKey : 'id'
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Property',
                                                                config : {
                                                                    propertyToRender : 'userDisplayName'
                                                                }
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Property',
                                                                config : {
                                                                    propertyToRender : 'id'
                                                                }
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Property',
                                                                config : {
                                                                    propertyToRender : 'templateId'
                                                                }
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Property',
                                                                config : {
                                                                    propertyToRender : 'componentCount'
                                                                }
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            // TODO Report enhancement - there should be a nopad option
                                                            additionalCssClasses : 'nopad',
                                                            style : 'padding: 0;',
                                                            widgets : [ {
                                                                name : 'alfresco/renderers/Actions',
                                                                config : {
                                                                    onlyShowOnHover : true,
                                                                    // TODO Report enhancement - make size of Actions configurable (it is frigging huge)
                                                                    customActions : [ {
                                                                        id : 'RESET_DASHBOARD',
                                                                        label : 'user-dashboards.action.resetDashboard',
                                                                        iconClass : "alf-doclib-action alf-delete-icon",
                                                                        // TODO Report enhancement - customActions should support widget-like renderFilter
                                                                        // TODO Filter based on "canBeReset" when possible
                                                                        publishTopic : 'ALF_CRUD_CREATE',
                                                                        publishPayloadType : 'PROCESS',
                                                                        publishPayloadModifiers : [ 'processCurrentItemTokens' ],
                                                                        publishPayload : {
                                                                            url : 'data/console/ootbee-support-tools/user/{user}/dashboard/reset',
                                                                            urlType : 'SHARE',
                                                                            // just to avoid an unnecessary warning
                                                                            alfResponseTopic : String(Packages.java.util.UUID.randomUUID())
                                                                        }
                                                                    } ]
                                                                }
                                                            } ]
                                                        }
                                                    } ]
                                        }
                                    } ]
                                }
                            } ]
                        }
                    } ]
        }
    };

    return model;
}

model.jsonModel = {
    services : [ 'alfresco/services/CrudService' ],
    widgets : [ {
        id : 'SET_PAGE_TITLE',
        name : 'alfresco/header/SetTitle',
        config : {
            title : 'tool.user-dashboards.label'
        }
    }, {
        name : 'alfresco/html/Label',
        config : {
            label : 'user-dashboards.intro-text',
            style : 'display: block; margin-bottom: 2ex;'
        }
    }, buildPanel() ]
};
