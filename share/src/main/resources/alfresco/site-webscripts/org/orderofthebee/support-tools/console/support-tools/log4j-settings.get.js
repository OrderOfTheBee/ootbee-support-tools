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

function buildAddLoggerForm(repoTier)
{
    var form = {
        name : 'ootbee-support-tools/forms/AddLoggerForm',
        config : {
            additionalCssClasses : 'addLoggerForm',
            widgets : [ {
                name : 'alfresco/forms/controls/TextBox',
                config : {
                    name : 'logger',
                    label : '',
                    placeHolder : 'log-settings.loggerName.addLoggerPlaceHolder',
                    requirementConfig : {
                        initialValue : true
                    }
                }
            }, {
                name : 'alfresco/forms/controls/Select',
                config : {
                    name : 'level',
                    label : '',
                    value : 'INFO',
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
                    },
                    requirementConfig : {
                        initialValue : true
                    }
                }
            } ],
            okButtonLabel : 'log-settings.addLogger',
            okButtonPublishTopic : 'ALF_CRUD_CREATE',
            okButtonPublishGlobal : true,
            okButtonPublishPayload : {
                url : (repoTier ? 'ootbee/admin/log4j-loggers.json' : 'data/console/ootbee-support-tools/log4j-loggers'),
                urlType : repoTier ? 'PROXY' : 'SHARE',
                // just to avoid an unnecessary warning
                alfResponseTopic : String(Packages.java.util.UUID.randomUUID()),
                alfResponseScope : (repoTier ? 'REPO_' : 'SHARE_') + 'LOGGER_LIST/'
            },
            // TODO Report enhancement - simple/standardised reset to original state on successful publish
            showCancelButton : false
        }
    };

    return form;
}

function buildTailButton(repoTier)
{
    var button = {
        name : 'alfresco/buttons/AlfButton',
        config : {
            label : repoTier ? 'log-settings.tailRepo' : 'log-settings.tailShare',
            // TODO Report enhancement - dialog should support eager destroy
            publishTopic : 'ALF_CREATE_DIALOG_REQUEST',
            publishPayload : {
                dialogId : 'TAIL',
                hideTopic : 'HIDE-LOG-TAIL-DIALOG',
                dialogTitle : repoTier ? 'log-settings.tailRepo' : 'log-settings.tailShare',
                dialogWidth : '80vw',
                widgetsContent : [ {
                    name : 'ootbee-support-tools/list/LogList',
                    config : {
                        // TODO Report enhancement - table should not force "loading" to be larger than current table view
                        style : 'min-height: 40ex;',
                        loadDataPublishPayload : {
                            url : repoTier ? 'ootbee/admin/log4j-tail-events' : 'data/console/ootbee-support-tools/log4j-tail',
                            urlType : repoTier ? 'PROXY' : 'SHARE'
                        }
                    }
                } ],
                widgetsButtons : [ {
                    name : 'alfresco/buttons/AlfButton',
                    config : {
                        label : 'button.close',
                        publishTopic : 'HIDE-LOG-TAIL-DIALOG'
                    }
                } ]
            }
        }
    };

    return button;
}

function buildLogFilesButton(repoTier)
{
    var button = {
        name : 'alfresco/buttons/AlfButton',
        config : {
            label : repoTier ? 'log-settings.repoLogFiles' : 'log-settings.shareLogFiles',
            // TODO Report enhancement - dialog should support eager destroy
            publishTopic : 'ALF_CREATE_DIALOG_REQUEST',
            publishPayload : {
                dialogId : 'FILES',
                hideTopic : 'HIDE-LOG-FILES-DIALOG',
                dialogTitle : repoTier ? 'log-settings.repoLogFiles' : 'log-settings.shareLogFiles',
                dialogWidth : '80vw',
                widgetsContent : [
                        {
                            name : 'alfresco/menus/AlfMenuBar',
                            config : {
                                pubSubScope : 'OOTBEE_SUPPORT_TOOLS_FILES_DIALOG/',
                                widgets : [ {
                                    name : 'alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup',
                                    config : {
                                        // can't believe "selected-items.label" is not a global label
                                        // we prefix it to not mess with any global labels others may have added
                                        label : 'log-settings.files.selected-items.label',
                                        passive : false,
                                        itemKeyProperty : 'path',
                                        widgets : [ {
                                            id : 'DOWNLOAD_LOG_FILES_ZIP',
                                            name : 'alfresco/menus/AlfSelectedItemsMenuItem',
                                            config : {
                                                label : 'actions.download-as-zip',
                                                iconClass : "alf-doclib-action alf-download-as-zip-icon",
                                                publishTopic : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILES_ZIP',
                                                publishGlobal : true,
                                                publishPayload : {
                                                    urlType : repoTier ? 'PROXY' : 'CONTEXT',
                                                    baseUrl : repoTier ? 'ootbee/admin/log4j-log-files.zip'
                                                            : 'ootbee-support-tools/log4j-log-files.zip'
                                                }
                                            }
                                        }
                                        // TODO bulk delete action
                                        ]
                                    }
                                } ]
                            }
                        },
                        {
                            name : 'alfresco/lists/AlfList',
                            config : {
                                pubSubScope : 'OOTBEE_SUPPORT_TOOLS_FILES_DIALOG/',
                                reloadDataTopic : 'OOTBEE_SUPPORT_TOOLS_RELOAD_LOG_FILES',
                                // TODO Report enhancement - table should not force "loading" to be larger than current table view
                                style : 'min-height: 40ex;',
                                loadDataPublishTopic : 'ALF_CRUD_GET_ALL',
                                loadDataPublishPayload : {
                                    url : repoTier ? 'ootbee/admin/log4j-log-files.json'
                                            : 'data/console/ootbee-support-tools/log4j-log-files',
                                    urlType : repoTier ? 'PROXY' : 'SHARE'
                                },
                                itemsProperty : 'logFiles',
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
                                                label : 'log-settings.files.name'
                                            }
                                        }, {
                                            name : 'alfresco/lists/views/layouts/HeaderCell',
                                            config : {
                                                // TODO Report bug - missing padding style options
                                                label : 'log-settings.files.path'
                                            }
                                        }, {
                                            name : 'alfresco/lists/views/layouts/HeaderCell',
                                            config : {
                                                // TODO Report bug - missing padding style options
                                                label : 'log-settings.files.size'
                                            }
                                        }, {
                                            name : 'alfresco/lists/views/layouts/HeaderCell',
                                            config : {
                                                // TODO Report bug - missing padding style options
                                                label : 'log-settings.files.lastModified'
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
                                                widgets : [
                                                        {
                                                            name : 'alfresco/lists/views/layouts/Cell',
                                                            config : {
                                                                additionalCssClasses : 'smallpad',
                                                                widgets : [ {
                                                                    name : 'alfresco/renderers/Selector',
                                                                    itemKey : 'path'
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
                                                                        propertyToRender : 'name'
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
                                                                        propertyToRender : 'directoryPath'
                                                                    }
                                                                } ]
                                                            }
                                                        },
                                                        {
                                                            name : 'alfresco/lists/views/layouts/Cell',
                                                            config : {
                                                                additionalCssClasses : 'smallpad',
                                                                widgets : [ {
                                                                    name : 'alfresco/renderers/Size',
                                                                    config : {
                                                                        sizeProperty : 'size'
                                                                    }
                                                                } ]
                                                            }
                                                        },
                                                        {
                                                            name : 'alfresco/lists/views/layouts/Cell',
                                                            config : {
                                                                additionalCssClasses : 'smallpad',
                                                                widgets : [ {
                                                                    name : 'alfresco/renderers/Date',
                                                                    config : {
                                                                        simple : true,
                                                                        format : 'yyyy-mm-dd HH:MM:ss.lo',
                                                                        propertyToRender : 'lastModified.iso8601'
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
                                                                        // TODO Report enhancement - make size of Actions configurable (it
                                                                        // is frigging huge)
                                                                        // TODO Report enhancement - Actions should allow providing custom actions like the pre-defined action widgets
                                                                        // (currently customActions are mapped into AlfMenuItem dropping some options and adding some non-suppressable ones)
                                                                        customActions : [
                                                                                {
                                                                                    id : 'DOWNLOAD_LOG_FILE',
                                                                                    label : 'actions.download',
                                                                                    icon : 'document-download', // force to do this due Actions mapping
                                                                                    iconClass : 'alf-doclib-action alf-download-icon',
                                                                                    publishTopic : 'OOTBEE_SUPPORT_TOOLS_DOWNLOAD_LOG_FILE',
                                                                                    // actions always publish global but for consistency with toolbar action we includes this here
                                                                                    publishGlobal : true,
                                                                                    publishPayloadType : 'CONFIGURED',
                                                                                    publishPayloadItemMixin : true,
                                                                                    publishPayload : {
                                                                                        urlType : repoTier ? 'PROXY' : 'CONTEXT',
                                                                                        baseUrl : repoTier ? 'ootbee/admin/log4j-log-file'
                                                                                                : 'ootbee-support-tools/log4j-log-file'
                                                                                    }
                                                                                },
                                                                                {
                                                                                    id : 'DELETE_LOG_FILE',
                                                                                    label : 'actions.delete',
                                                                                    icon : 'document-delete', // force to do this due Actions mapping
                                                                                    iconClass : 'alf-doclib-action alf-delete-icon',
                                                                                    publishTopic : 'OOTBEE_SUPPORT_TOOLS_DELETE_LOG_FILE',
                                                                                    // actions always publish global but for consistency with toolbar action we includes this here
                                                                                    publishGlobal : true,
                                                                                    publishPayloadType : 'CONFIGURED',
                                                                                    publishPayloadItemMixin : true,
                                                                                    publishPayload : {
                                                                                        // TODO Report requirement: consistent topic paramaters
                                                                                        // alfSuccessTopic is primarily used in CoreXhr
                                                                                        // responseTopic and alfResponseTopic are inconsistently used in various places
                                                                                        // _SUCCESS is typically appended to the topic when signalling success to subscribers
                                                                                        // there is no common default pattern for an "as-is" topic parameter for success publications
                                                                                        alfSuccessTopic : 'OOTBEE_SUPPORT_TOOLS_RELOAD_LOG_FILES',
                                                                                        urlType : repoTier ? 'PROXY' : 'SHARE',
                                                                                        baseUrl : repoTier ? 'ootbee/admin/log4j-log-file'
                                                                                                : 'data/console/ootbee-support-tools/log4j-log-file'
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
                        } ],
                widgetsButtons : [ {
                    name : 'alfresco/buttons/AlfButton',
                    config : {
                        label : 'button.close',
                        publishTopic : 'HIDE-LOG-FILES-DIALOG'
                    }
                } ]
            }
        }
    };

    return button;
}

function buildResetLoggerButton(repoTier)
{
    var button = {
        name : 'alfresco/buttons/AlfButton',
        config : {
            label : 'log-settings.action.resetAllLoggerSettings',
            publishTopic : 'ALF_CRUD_DELETE',
            publishPayload : {
                url : repoTier ? 'ootbee/admin/log4j-loggers' : 'data/console/ootbee-support-tools/log4j-loggers',
                urlType : repoTier ? 'PROXY' : 'SHARE',
                // just to avoid an unnecessary warning
                alfResponseTopic : String(Packages.java.util.UUID.randomUUID()),
                alfResponseScope : (repoTier ? 'REPO_' : 'SHARE_') + 'LOGGER_LIST/'
            }
        }
    };

    return button;
}

function buildPanel(repoTier)
{
    var model = {
        name : 'alfresco/core/ProcessWidgets',
        id : repoTier ? 'REPO_PANEL' : 'SHARE_PANEL',
        title : repoTier ? 'log-settings.repositoryPanel' : 'log-settings.sharePanel',
        selected : !repoTier,
        delayProcessing : repoTier,
        config : {
            style : 'padding-top: 2ex;',
            widgets : [
                    buildAddLoggerForm(repoTier),
                    buildTailButton(repoTier),
                    buildLogFilesButton(repoTier),
                    buildResetLoggerButton(repoTier),
                    {
                        name : 'alfresco/lists/Paginator',
                        config : {
                            documentsPerPage : 20,
                            pageSizes : [ 20, 50, 100 ],
                            pubSubScope : (repoTier ? 'REPO_' : 'SHARE_') + 'LOGGER_LIST/',
                            style : 'text-align:center;'
                        }
                    }, 
                    {
                        name : 'alfresco/lists/AlfFilteredList',
                        config : {
                            pubSubScope : (repoTier ? 'REPO_' : 'SHARE_') + 'LOGGER_LIST/',
                            // TODO Report bug - CrudService should not hard-code topic
                            reloadDataTopic : 'ALF_DOCLIST_RELOAD_DATA',
                            loadDataPublishTopic : 'ALF_CRUD_GET_ALL',
                            loadDataPublishPayload : {
                                url : repoTier ? 'ootbee/admin/log4j-loggers.json' : 'data/console/ootbee-support-tools/log4j-loggers',
                                urlType : repoTier ? 'PROXY' : 'SHARE'
                            },
                            // TODO Report enhancement - filtering should not require these form topic cludges
                            filteringTopics : [ '_valueChangeOf_LOGGER_NAME' + (repoTier ? '_REPO' : '_SHARE'), '_valueChangeOf_UNCONFIGURED_LOGGERS' + (repoTier ? '_REPO' : '_SHARE') ],
                            widgetsForFilters : [ {
                                name : 'alfresco/forms/controls/TextBox',
                                config : {
                                    // TODO Report enhancement - filter widgets should align properly
                                    // TODO Report enhancement - simple width customisation
                                    style : 'vertical-align:top;',
                                    fieldId : 'LOGGER_NAME' + (repoTier ? '_REPO' : '_SHARE'),
                                    name : 'loggerName',
                                    label : 'log-settings.loggerName',
                                    placeHolder : 'log-settings.loggerName.filterPlaceHolder'
                                }
                            }, {
                                name : 'alfresco/forms/controls/CheckBox',
                                config : {
                                    // TODO Report enhancement - filter widgets should align properly
                                    style : 'vertical-align:top;',
                                    fieldId : 'UNCONFIGURED_LOGGERS' + (repoTier ? '_REPO' : '_SHARE'),
                                    name : 'showUnconfiguredLoggers',
                                    offValue : false,
                                    onValue : true,
                                    label : 'log-settings.showUnconfiguredLoggers'
                                }
                            } ],
                            usePagination : true,
                            currentPageSize : 20,
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
                                            // TODO Report bug - property zebraStriping without effect
                                            additionalCssClasses : 'zebra-striping',
                                            // TODO Report enhancement - simple CellProperty widget
                                            widgets : [
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'ootbee-support-tools/renderers/LoggerProperty',
                                                                config : {
                                                                    propertyToRender : 'shortDisplayName',
                                                                    propertyToRenderTitle : 'displayName'
                                                                }
                                                            } ]
                                                        }
                                                    },
                                                    {
                                                        name : 'alfresco/lists/views/layouts/Cell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            widgets : [ {
                                                                name : 'ootbee-support-tools/renderers/LoggerProperty',
                                                                config : {
                                                                    propertyToRender : 'parent.shortDisplayName',
                                                                    propertyToRenderTitle : 'parent.displayName'
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
                                                                    propertyToRender : 'additivity',
                                                                    // TODO Report enhancement - valueDisplayMap should inherently I18n the label (like label in HeaderCell)
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
                                                    },
                                                    {
                                                        name : 'ootbee-support-tools/list/LogLevelHighlightedCell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            logLevelPropertyKey : 'level',
                                                            widgets : [ {
                                                                // TODO Report enhancement - make InlineEditProperty usable (e.g. support all types of input elements with simple+smart config)
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
                                                    },
                                                    {
                                                        name : 'ootbee-support-tools/list/LogLevelHighlightedCell',
                                                        config : {
                                                            additionalCssClasses : 'smallpad',
                                                            logLevelPropertyKey : 'effectiveLevel',
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
                                                                    customActions : [
                                                                            {
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
                                                                                        // TODO Report enhancement - ALF_CRUD_UPDATE should differentiate request config from request data instead of submitting EVERYTHING
                                                                                        url : (repoTier ? 'ootbee/admin/log4j-loggers'
                                                                                                : 'data/console/ootbee-support-tools/log4j-loggers')
                                                                                                + '/{urlName}',
                                                                                        urlType : repoTier ? 'PROXY' : 'SHARE',
                                                                                        // just to avoid an unnecessary warning
                                                                                        alfResponseTopic : String(Packages.java.util.UUID
                                                                                                .randomUUID())
                                                                                    // TODO Report enhancement - any pubSub should support response topic without forcing _SUCCESS suffix
                                                                                    },
                                                                                    formValue : {
                                                                                        level : '{level}'
                                                                                    },
                                                                                    widgets : [
                                                                                            {
                                                                                                name : 'alfresco/forms/controls/HiddenValue',
                                                                                                config : {
                                                                                                    name : 'logger'
                                                                                                }
                                                                                            },
                                                                                            {
                                                                                                name : 'alfresco/forms/controls/Select',
                                                                                                config : {
                                                                                                    name : 'level',
                                                                                                    label : 'log-settings.setting',
                                                                                                    optionsConfig : {
                                                                                                        fixed : [
                                                                                                                {
                                                                                                                    value : 'UNSET',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.UNSET')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'OFF',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.OFF')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'TRACE',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.TRACE')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'DEBUG',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.DEBUG')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'INFO',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.INFO')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'WARN',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.WARN')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'ERROR',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.ERROR')
                                                                                                                },
                                                                                                                {
                                                                                                                    value : 'FATAL',
                                                                                                                    label : msg
                                                                                                                            .get('log-settings.level.FATAL')
                                                                                                                } ]
                                                                                                    }
                                                                                                }
                                                                                            } ]
                                                                                }
                                                                            },
                                                                            {
                                                                                id : 'RESET_SETTING',
                                                                                label : 'log-settings.action.resetLoggerSetting',
                                                                                // TODO Report enhancement - customActions should support widget-like renderFilter
                                                                                // TODO Filter based on "canBeReset" when possible
                                                                                publishTopic : 'ALF_CRUD_DELETE',
                                                                                publishPayloadType : 'PROCESS',
                                                                                publishPayloadModifiers : [ 'processCurrentItemTokens' ],
                                                                                publishPayload : {
                                                                                    url : (repoTier ? 'ootbee/admin/log4j-loggers'
                                                                                            : 'data/console/ootbee-support-tools/log4j-loggers')
                                                                                            + '/{urlName}',
                                                                                    urlType : repoTier ? 'PROXY' : 'SHARE',
                                                                                    // just to avoid an unnecessary warning
                                                                                    alfResponseTopic : String(Packages.java.util.UUID
                                                                                            .randomUUID())
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
    services : [ 'alfresco/services/CrudService', 'alfresco/services/DialogService', 'ootbee-support-tools/service/LogFileService' ],
    widgets : [ {
        id : 'SET_PAGE_TITLE',
        name : 'alfresco/header/SetTitle',
        config : {
            title : 'tool.log4j-settings.label'
        }
    }, {
        name : 'alfresco/html/Label',
        config : {
            label : 'log-settings.intro-text',
            style : 'display: block; margin-bottom: 2ex;'
        }
    }, {
        name : 'alfresco/layout/AlfTabContainer',
        config : {
            widgets : [ buildPanel(true), buildPanel(false) ]
        }
    } ]
};
