/**
 * Copyright (C) 2016 Axel Faust Copyright (C) 2016 Order of the Bee
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
 * Linked to Alfresco Copyright (C) 2005-2016 Alfresco Software Limited.
 */

model.jsonModel = {
    services : [ 'alfresco/services/CrudService' ],
    widgets : [ {
        id : 'SET_PAGE_TITLE',
        name : 'alfresco/header/SetTitle',
        config : {
            title : 'tool.log4j-settings.label'
        }
    }, {
        name : 'alfresco/lists/AlfList',
        config : {
            loadDataPublishTopic : 'ALF_CRUD_GET_ALL',
            loadDataPublishPayload : {
                url : 'data/console/ootbee-support-tools/log4j-settings-loggers',
                urlType : 'SHARE'
            },
            itemsProperty : 'loggers',
            widgets : [ {
                name : 'alfresco/lists/views/AlfListView',
                config : {
                    additionalCssClasses : 'bordered',
                    widgetsForHeader : [ {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            // TODO Report bug - missing padding style options
                            label : 'log-settings.column.loggerName'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.column.parentLoggerName'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.column.additivity'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.column.setting'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.column.effectiveValue'
                        }
                    } ],
                    widgets : [ {
                        name : 'alfresco/lists/views/layouts/Row',
                        config : {
                            // TODO Report bug - property zebraStriping without
                            // effect
                            additionalCssClasses : 'zebra-striping',
                            // TODO Report enhancement - simple CellProperty
                            // widget
                            widgets : [ {
                                name : 'alfresco/lists/views/layouts/Cell',
                                config : {
                                    additionalCssClasses : 'smallpad',
                                    widgets : [ {
                                        name : 'alfresco/renderers/Property',
                                        config : {
                                            propertyToRender : 'name'
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
                                            propertyToRender : 'parent.name'
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
                                            propertyToRender : 'additivity',
                                            // TODO Report enhancement -
                                            // valueDisplayMap
                                            // should inherently I18n the label
                                            // (like
                                            // label in HeaderCell)
                                            valueDisplayMap : [ {
                                                value : 'true',
                                                label : msg.get('log-settings.column.additivity.true')
                                            }, {
                                                value : 'false',
                                                label : msg.get('log-settings.column.additivity.false')
                                            } ]
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
                                            propertyToRender : 'level',
                                            valueDisplayMap : [ {
                                                value : 'UNSET',
                                                label : msg.get('log-settings.level.UNSET')
                                            }, {
                                                value : 'OFF',
                                                label : msg.get('log-settings.level.OFF')
                                            }, {
                                                value : 'TRACE',
                                                label : msg.get('log-settings.level.TRACE')
                                            }, {
                                                value : 'DEBUG',
                                                label : msg.get('log-settings.level.DEBUG')
                                            }, {
                                                value : 'INFO',
                                                label : msg.get('log-settings.level.INFO')
                                            }, {
                                                value : 'WARN',
                                                label : msg.get('log-settings.level.WARN')
                                            }, {
                                                value : 'ERROR',
                                                label : msg.get('log-settings.level.ERROR')
                                            }, {
                                                value : 'FATAL',
                                                label : msg.get('log-settings.level.FATAL')
                                            } ]
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
                                            propertyToRender : 'effectiveLevel',
                                            valueDisplayMap : [ {
                                                value : 'UNSET',
                                                label : msg.get('log-settings.level.UNSET')
                                            }, {
                                                value : 'OFF',
                                                label : msg.get('log-settings.level.OFF')
                                            }, {
                                                value : 'TRACE',
                                                label : msg.get('log-settings.level.TRACE')
                                            }, {
                                                value : 'DEBUG',
                                                label : msg.get('log-settings.level.DEBUG')
                                            }, {
                                                value : 'INFO',
                                                label : msg.get('log-settings.level.INFO')
                                            }, {
                                                value : 'WARN',
                                                label : msg.get('log-settings.level.WARN')
                                            }, {
                                                value : 'ERROR',
                                                label : msg.get('log-settings.level.ERROR')
                                            }, {
                                                value : 'FATAL',
                                                label : msg.get('log-settings.level.FATAL')
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
};
