/**
 * Copyright (C) 2016 Axel Faust
 * Copyright (C) 2016 Order of the Bee
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
        name : 'alfresco/lists/AlfFilteredList',
        config : {
            pubSubScope : 'LOGGER_LIST/',
            // TODO Report bug - CrudService should not hard-code topic
            reloadDataTopic : 'ALF_DOCLIST_RELOAD_DATA',
            loadDataPublishTopic : 'ALF_CRUD_GET_ALL',
            loadDataPublishPayload : {
                url : 'data/console/ootbee-support-tools/log4j-settings-loggers',
                urlType : 'SHARE'
            },
            // TODO Report enhancement - filtering should not require these form topic cludges
            filteringTopics : [ '_valueChangeOf_LOGGER_NAME', '_valueChangeOf_UNCONFIGURED_LOGGERS' ],
            widgetsForFilters : [ {
                name : 'alfresco/forms/controls/TextBox',
                config : {
                    fieldId : 'LOGGER_NAME',
                    name : 'loggerName',
                    label : 'log-settings.LoggerName',
                    placeHolder : 'log-settings.loggerName.filterPlaceHolder'
                }
            }, {
                name : 'alfresco/forms/controls/CheckBox',
                config : {
                    fieldId : 'UNCONFIGURED_LOGGERS',
                    name : 'showUnconfiguredLoggers',
                    offValue : false,
                    onValue : true,
                    label : 'log-settings.showUnconfiguredLoggers'
                }
            } ],
            // TODO Support pagination and sorting
            usePagination : false,
            itemsProperty : 'loggers',
            widgets : [ {
                name : 'alfresco/lists/views/AlfListView',
                config : {
                    additionalCssClasses : 'bordered',
                    widgetsForHeader : [ {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            // TODO Report bug - missing padding style options
                            label : 'log-settings.loggerName'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.parentLoggerName'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.additivity'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.setting'
                        }
                    }, {
                        name : 'alfresco/lists/views/layouts/HeaderCell',
                        config : {
                            label : 'log-settings.effectiveValue'
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
                                            propertyToRender : 'displayName'
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
                                            propertyToRender : 'parent.displayName'
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
                                            // valueDisplayMap should inherently
                                            // I18n the label (like label in
                                            // HeaderCell)
                                            valueDisplayMap : [ {
                                                value : 'true',
                                                label : msg.get('log-settings.additivity.true')
                                            }, {
                                                value : 'false',
                                                label : msg.get('log-settings.additivity.false')
                                            } ]
                                        }
                                    } ]
                                }
                            }, {
                                name : 'alfresco/lists/views/layouts/Cell',
                                config : {
                                    additionalCssClasses : 'smallpad',
                                    widgets : [ {
                                        // TODO Report enhancement - make InlineEditProperty usable
                                        // (e.g. support all types of input elements with simple+smart config)
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
                            }, {
                                name : 'alfresco/lists/views/layouts/Cell',
                                config : {
                                    // TODO Report enhancement - there should be a nopad option
                                    additionalCssClasses : 'nopad',
                                    style : 'padding: 0;',
                                    widgets : [ {
                                        name : 'alfresco/renderers/Actions',
                                        config : {
                                            // TODO Report enhancement - make size of Actions configurable (it is frigging huge)
                                            customActions : [ {
                                                id : 'EDIT_SETTING',
                                                label : 'log-settings.action.editLoggerSetting',
                                                publishTopic : 'ALF_CREATE_FORM_DIALOG_REQUEST',
                                                publishPayloadType : 'PROCESS',
                                                publishPayloadModifiers : [ 'processCurrentItemTokens' ],
                                                publishPayload : {
                                                    dialogId : 'EDIT_LOGGER_SETTING',
                                                    dialogTitle : 'log-settings.action.editLoggerSetting',
                                                    formSubmissionTopic : 'ALF_CRUD_UPDATE',
                                                    formSubmissionPayloadMixin : {
                                                        // TODO Report enhancement - ALF_CRUD_UPDATE should differentiate request config
                                                        // from request data instead of submitting EVERYTHING
                                                        url : 'data/console/ootbee-support-tools/log4j-settings-loggers',
                                                        urlType : 'SHARE',
                                                        alfResponseTopic : 'LOGGER_LIST/'
                                                    // TODO Report enhancement - any pubSub should support response topic without forcing
                                                    // _SUCCESS suffix
                                                    },
                                                    formValue : {
                                                        logger : '{name}',
                                                        level : '{level}'
                                                    },
                                                    widgets : [ {
                                                        name : 'alfresco/forms/controls/HiddenValue',
                                                        config : {
                                                            name : 'logger'
                                                        }
                                                    }, {
                                                        name : 'alfresco/forms/controls/Select',
                                                        config : {
                                                            name : 'level',
                                                            label : 'log-settings.setting',
                                                            optionsConfig : {
                                                                fixed : [ {
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
    } ]
};
