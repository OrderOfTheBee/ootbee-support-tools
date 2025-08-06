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
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */

/**
 * OOTBee root namespace.
 *
 * @namespace OOTBee
 */
// Ensure OOTBee root object exists
if (typeof OOTBee === 'undefined' || !OOTBee)
{
    var OOTBee = {};
}

/**
 * Admin Console Javascript Console
 *
 * @namespace OOTBee
 * @class OOTBee.JavaScriptConsole
 */
(function()
{
    var Dom, Event, Element, $html, $hasEventInterest;

    Dom = YAHOO.util.Dom;
    Event = YAHOO.util.Event;
    Element = YAHOO.util.Element;
    $html = Alfresco.util.encodeHTML;
    $hasEventInterest = Alfresco.util.hasEventInterest;

    function getQueryVariable(variable)
    {
        var query, vars, i, pair;

        query = window.location.search.substring(1);
        vars = query.split('&');
        for (i = 0; i < vars.length; i++)
        {
            pair = vars[i].split('=');
            if (pair[0] === variable) {
                return unescape(pair[1]);
            }
        }
        return '';
    }

    function browserSupportsHtml5Storage()
    {
        try
        {
            var testString = 'LSTEST12345';
            localStorage.setItem(testString, testString );
            localStorage.removeItem(testString);
            return true;
        }
        catch(e)
        {
            return false;
        }
    }

    function showStatusInfo(cm, statusLineClass)
    {
        var currentLine, column, info, text;

        currentLine = cm.getCursor().line+1;
        column = cm.getCursor().ch;
        info = 'Line ' + currentLine + ' \t - Column ' + column;
        text = YAHOO.util.Selector.query(statusLineClass, null, true);
        text.innerHTML = info;
    }

    /**
      * JavaScriptConsole constructor.
      *
      * @param {String}
      *            htmlId The HTML id of the parent element
      * @return {OOTBee.JavaScriptConsole} The new JavaScriptConsole instance
      * @constructor
      */
    OOTBee.JavaScriptConsole = function JavaScriptConsole_constructor(htmlId)
    {
        this.name = 'OOTBee.JavaScriptConsole';
        OOTBee.JavaScriptConsole.superclass.constructor.call(this, htmlId);

        // ensure high-enough zIndex to overlay anything from editors
        Alfresco.util.PopupManager.zIndex = 30;

        Alfresco.util.ComponentManager.register(this);
        Alfresco.util.YUILoaderHelper.require(['button', 'container', 'datasource', 'datatable',  'paginator', 'json', 'history', 'tabview'], this.onComponentsLoaded, this);

        OOTBee.JavaScriptConsolePanelHandler = function JavaScriptConsolePanelHandler_constructor(parent)
        {
            OOTBee.JavaScriptConsolePanelHandler.superclass.constructor.call(this, 'main');
            this.parent = parent;
        };

        YAHOO.extend(OOTBee.JavaScriptConsolePanelHandler, Alfresco.ConsolePanelHandler,
        {
            parent: null,

            /**
              * Called by the ConsolePanelHandler when this panel shall be loaded
              *
              * @method onLoad
              */
            onLoad: function JavaScriptConsolePanelHandler_onLoad()
            {
                this.parent.widgets.pathField = Dom.get(this.parent.id + '-pathField');
                this.parent.widgets.documentField = Dom.get(this.parent.id + '-documentField');
                this.parent.widgets.nodeField = Dom.get(this.parent.id + '-nodeRef');
                this.parent.widgets.scriptInput = Dom.get(this.parent.id + '-jsinput');
                this.parent.widgets.scriptOutput = Dom.get(this.parent.id + '-jsoutput');
                this.parent.widgets.repoInfoOutput = Dom.get(this.parent.id + '-repoInfo');
                this.parent.widgets.dumpInfoOutput = Dom.get(this.parent.id + '-dump');
                this.parent.widgets.jsonOutput = Dom.get(this.parent.id + '-jsonOutput');
                this.parent.widgets.templateInput = Dom.get(this.parent.id + '-templateinput');
                this.parent.widgets.templateOutputHtml = Dom.get(this.parent.id + '-templateoutputhtml');
                this.parent.widgets.templateOutputText = Dom.get(this.parent.id + '-templateoutputtext');
                this.parent.widgets.config = {
                    runas: Dom.get(this.parent.id + '-runas'),
                    transaction: Dom.get(this.parent.id + '-transactions'),
                    urlargs: Dom.get(this.parent.id + '-urlarguments'),
                    runlikecrazy: Dom.get(this.parent.id + '-runlikecrazy')
                };

                this.parent.widgets.selectDestinationButton = Alfresco.util.createYUIButton(this.parent, 'selectDestination-button', this.parent.onSelectDestinationClick);
                this.parent.widgets.executeButton = Alfresco.util.createYUIButton(this.parent, 'execute-button', this.parent.onExecuteClick);
                this.parent.widgets.refreshButton = Alfresco.util.createYUIButton(this.parent, 'refresh-button', this.parent.onRefreshServerInfoClick);
                Dom.addClass(this.parent.widgets.refreshButton._button.parentNode.parentNode, 'refresh-button-env');
            }
        });

        this.panelHandler = new OOTBee.JavaScriptConsolePanelHandler(this);

        this.options.documentNodeRef = getQueryVariable('nodeRef');
        this.options.documentName = getQueryVariable('name');
        this.options.documentDump = getQueryVariable('dump');

        this.javascriptCommands = {};

        return this;
    };

    YAHOO.extend(OOTBee.JavaScriptConsole, Alfresco.ConsoleTool,
    {
        panelHandler: null,

        clearOutput: function JavaScriptConsole_clearOutput()
        {
            this.widgets.scriptOutput.innerHTML = '';
            this.widgets.templateOutputHtml.innerHTML = '';
            this.widgets.templateOutputText.innerHTML = '';
        },

        template: '<div class="display-element"><span class="display-label">{name}</span><span class="display-field">{value}</span></div>',

        appendLineArrayToOutput: function JavaScriptConsole_appendLineArrayToOutput(lineArray)
        {
            var newLines, idx;

            newLines = '';
            for (idx = 0; idx < lineArray.length; idx++)
            {
                newLines += lineArray[idx] + '\n';
            }
            this.setOutputText(newLines);
        },

        setOutputText: function JavaScriptConsole_setOutputText(text)
        {
            this.widgets.scriptOutput.innerHTML = '';
            this.widgets.scriptOutput.appendChild(document.createTextNode(text));
        },

        onEditorKeyEvent: function JavaScriptConsole_onEditorKeyEvent(i, e)
        {
            var editor, code;

            // Hook into ctrl-enter
            if (e.type === 'keyup' && e.keyCode == 13 && (e.ctrlKey || e.metaKey) && !e.altKey)
            {
                e.stop();
                i.owner.onExecuteClick(i.owner, e);
            }

            // Hook into ctrl+/ for Comment/Uncomment
            if (e.type === 'keydown' && e.keyCode == 55 && (e.ctrlKey || e.metaKey) && !e.altKey)
            {
                e.stop();
                editor = i.owner.widgets.codeMirrorScript;
                code = editor.getSelection();
                if (code.substr(0,2) === '//')
                {
                    // add a // comment before each line
                    code = code.replace(/^\/\//gm, '');
                }
                else
                {
                    // remove // comment before each line
                    code = code.replace(/^/gm, '//');
                }
                editor.replaceSelection(code);
            }
            // Hook into ctrl+shift+F for js code format
            if (e.type === 'keydown' && e.keyCode == 70 && (e.ctrlKey || e.metaKey) && !e.altKey)
            {
                e.stop();
                editor = i.owner.widgets.codeMirrorScript;
                editor.setValue(js_beautify(editor.getValue()));
            }
        },

        /**
         * Fired by YUI when parent element is available for scripting.
         * Component initialisation, including instantiation of YUI widgets and
         * event listener binding.
         *
         * @method onReady
         */
        onReady: function JavaScriptConsole_onReady()
        {
            var self = this;

            // Call super-class onReady() method
            OOTBee.JavaScriptConsole.superclass.onReady.call(this);

            this.createMenus();
            this.setupCodeEditors();
            this.setupResizableEditor();

            this.widgets.inputTabs = new YAHOO.widget.TabView(this.id + '-inputTabs');
            this.widgets.outputTabs = new YAHOO.widget.TabView(this.id + '-outputTabs');

            // enable correct initialisation when navigating to the json editor
            // -> refresh when the tab changes to active.
            var jsonView = this.widgets.codeMirrorJSON;
            this.widgets.outputTabs.getTab(3).addListener('activeChange', function(event)
            {
                if(event.newValue)
                {
                    YAHOO.lang.later(50, undefined, function()
                    {
                        jsonView.refresh();
                    });
                }
            });

            new YAHOO.widget.Tooltip('tooltip-urlargs',
                {
                    context: this.widgets.config.urlargs,
                    text: this.msg('tooltip.urlargs'),
                    showDelay: 200
                }
            );

            new YAHOO.widget.Tooltip('tooltip-runas',
                {
                    context: this.widgets.config.runas,
                    text: this.msg('tooltip.runas'),
                    showDelay: 200
                }
            );

            var tab0 = this.widgets.inputTabs.getTab(1); // 2nd tab
            tab0.addListener('click', function handleClick(e)
            {
                self.widgets.codeMirrorTemplate.refresh();
            });

            this.widgets.statsModule = new YAHOO.widget.Module('perfPanel',
                {
                    visible: true,
                    draggable: false,
                    close: false
                }
            );

            var noExecEl = YAHOO.lang.substitute(this.template,
                {
                    name: this.msg('label.stats.no.execution'),
                    value: ''
                }
            );

            this.widgets.statsModule.setBody(noExecEl);
            this.widgets.statsModule.render(this.id + '-executionStats');

            var stats = Dom.get(this.id + '-executionStatsSimple');
            myTooltip = new YAHOO.widget.Tooltip('statsTooltip',
                {
                    context: stats,
                    text: 'Please click for more details.',
                    showDelay: 500
                }
            );

            YAHOO.Bubbling.on('folderSelected', this.onDestinationSelected, this);

            this.setupLocalStorage();
            this.loadRepoDefinitions();
            this.loadRepoScriptList();

            if (this.options.documentNodeRef || this.options.documentName)
            {
                Dom.setStyle(Dom.get(this.id + '-documentDisplay'), 'display', 'inline');
                this.widgets.documentField.innerHTML = this.options.documentName + ' (' + this.options.documentNodeRef +')';
            }

            if (this.options.documentDump)
            {
                // replace text in content editor and freemarker editor
                // execute call to server onExecute
                // handle the success call to jump to the dumpTab to see the result
             	this.widgets.codeMirrorScript.setValue('dump(document);');
             	this.onExecuteClick();
             	this.widgets.outputTabs.selectTab(6);
            }
        },

        createMenus: function JavaScriptConsole_createStaticMenus()
        {
            this.createThemeMenu();
            this.createOrUpdateScriptsLoadMenu();
            this.createOrUpdateScriptsSaveMenu();
            this.createDocsMenu();
            this.createDumpDisplayMenu();

            this.widgets.exportResultsButton = Alfresco.util.createYUIButton(this,
                'exportResults-button', this.exportResultTableAsCSV);
            Dom.setStyle(this.widgets.exportResultsButton, 'display', 'none');
        },

        createDocsMenu: function JavaScriptConsole_createDocsMenu()
        {
            var docsMenuItems = [
                [
                    { text: 'Mozilla Javascript Reference', url: 'https://developer.mozilla.org/en/JavaScript/Reference', target: '_blank'},
                    { text: 'W3Schools Javascript Reference', url: 'http://www.w3schools.com/jsref/default.asp', target: '_blank'},
                    { text: 'Repository JavaScript Root Objects', url: 'https://docs.alfresco.com/content-services/latest/develop/reference/repo-root-objects-ref/', target:'_blank' },
                    { text: 'JavaScript API Cookbook (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/javascript-api-cookbook/ba-p/293260', target:'_blank' },
                    { text: '5.0 JavaScript API (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/5-0-javascript-api/ba-p/289568', target:'_blank' },
                    { text: '5.0 JavaScript Services API (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/5-0-javascript-services-api/ba-p/289295', target:'_blank' }
                ],
                [
                    { text: 'Freemarker Manual', url: 'https://freemarker.apache.org/docs/index.html', target:'_blank'},
                    { text: 'Repository Freemarker Root Objects', url: 'https://docs.alfresco.com/content-services/latest/develop/reference/freemarker-ref/', target:'_blank'}
                ],
                [
                    { text: 'Full Text Search reference (Alfresco Search Services)', url: 'https://docs.alfresco.com/search-services/latest/using/', target:'_blank' },
                    { text: 'CMIS Query Language (CMIS 1.1 Specification)', url: 'http://docs.oasis-open.org/cmis/CMIS/v1.1/errata01/os/CMIS-v1.1-errata01-os-complete.html#x1-10500014', target:'_blank' },
                    { text: 'CMIS Query Language (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/cmis-query-language/ba-p/289736', target:'_blank' },
                    { text: 'XPath Search (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/search-documentation/ba-p/289935', target:'_blank' }
                ],
                [
                    { text: 'Web Sripts Extension Point', url: 'https://docs.alfresco.com/content-services/community/develop/repo-ext-points/web-scripts/', target:'_blank' },
                    { text: 'Web Scripts Reference (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/web-scripts/ba-p/290655', target:'_blank' },
                    { text: 'Web Scripts Examples (old Alfresco wiki, now obsolete Hub page)', url: 'https://hub.alfresco.com/t5/alfresco-content-services-hub/web-scripts-examples/ba-p/290077', target:'_blank' },
                    
                ]
            ];

            this.widgets.docsMenuButton = new YAHOO.widget.Button({
                id: 'docsButton',
                name: 'docsButton',
                label: this.msg('button.docs') + (Alfresco.constants.MENU_ARROW_SYMBOL !== undefined ? ('&nbsp;' + Alfresco.constants.MENU_ARROW_SYMBOL) : ''),
                type: 'menu',
                menu: docsMenuItems,
                container: this.id + '-documentation'
            });

            this.widgets.docsMenuButton.getMenu().setItemGroupTitle('Javascript', 0);
            this.widgets.docsMenuButton.getMenu().setItemGroupTitle('Freemarker', 1);
            this.widgets.docsMenuButton.getMenu().setItemGroupTitle('Lucene', 2);
            this.widgets.docsMenuButton.getMenu().setItemGroupTitle('Webscripts', 3);
            this.widgets.docsMenuButton.getMenu().cfg.setProperty('zIndex', 10);
        },

        initSubmenuIds: function JavaScriptConsole_initSubmenuIds(entry, suffix) {
            if (entry.submenu) {
                entry.submenu.id = entry.submenu.id + suffix;
                entry.submenu.itemdata.forEach(function (f) {
                    this.initSubmenuIds(f, suffix);
                }.bind(this));
            }
        },

        createOrUpdateScriptsSaveMenu: function JavaScriptConsole_createOrUpdateScriptsSaveMenu(listOfScripts)
        {
            var scripts, saveMenuItems;

            saveMenuItems = [{
                text: this.msg('button.save.create.new'),
                value: 'NEW'
            }];

            if (listOfScripts)
            {
                scripts = JSON.parse(JSON.stringify(listOfScripts));
                scripts.forEach(function(e) {
                    this.initSubmenuIds.call(this, e, "-scriptsave");
                }.bind(this));

                saveMenuItems.push(scripts);
            }

            if (this.widgets.saveMenuButton)
            {
                this.widgets.saveMenuButton.getMenu().clearContent();
                this.widgets.saveMenuButton.getMenu().addItems(saveMenuItems);
                this.widgets.saveMenuButton.getMenu().render(this.id + '-scriptsave');
            }
            else
            {
                this.widgets.saveMenuButton  = new YAHOO.widget.Button({
                    id: 'saveButton',
                    name: 'saveButton',
                    label: this.msg('button.save.script') + (Alfresco.constants.MENU_ARROW_SYMBOL !== undefined ? ('&nbsp;' + Alfresco.constants.MENU_ARROW_SYMBOL) : ''),
                    type: 'menu',
                    menu: saveMenuItems,
                    container: this.id + '-scriptsave'
                });
                this.widgets.saveMenuButton.getMenu().subscribe('click', this.onSaveScriptClick, this);
                this.widgets.saveMenuButton.getMenu().cfg.setProperty('zIndex', 10);
            }
        },

        createOrUpdateScriptsLoadMenu: function JavaScriptConsole_createOrUpdateScriptsLoadMenu(listOfScripts)
        {
            var scripts, loadMenuItems;

            loadMenuItems = [{
                text: this.msg('button.load.create.new'),
                value: 'NEW'
            }];

            if (listOfScripts)
            {
                scripts = JSON.parse(JSON.stringify(listOfScripts));
                scripts.forEach(function(e) {
                    this.initSubmenuIds.call(this, e, "-scriptload");
                }.bind(this));

                loadMenuItems.push(scripts);
            }

            if (this.widgets.loadMenuButton)
            {
                this.widgets.loadMenuButton.getMenu().clearContent();
                this.widgets.loadMenuButton.getMenu().addItems(loadMenuItems);
                this.widgets.loadMenuButton.getMenu().render(this.id + '-scriptload');
            }
            else
            {
                this.widgets.loadMenuButton = new YAHOO.widget.Button({
                    id: 'loadButton',
                    name: 'loadButton',
                    label: this.msg('button.load.script') + (Alfresco.constants.MENU_ARROW_SYMBOL !== undefined ? ('&nbsp;' + Alfresco.constants.MENU_ARROW_SYMBOL) : ''),
                    type: 'menu',
                    menu: loadMenuItems,
                    container: this.id + '-scriptload'
                });
                this.widgets.loadMenuButton.getMenu().subscribe('click', this.onLoadScriptClick, this);
                this.widgets.loadMenuButton.getMenu().cfg.setProperty('zIndex', 10);
            }
        },

        createThemeMenu: function JavaScriptConsole_createThemeMenu()
        {
            var themeMenuItems, theme, menuItems, i, menuItem;

            themeMenuItems =   [
                { text: 'default',           value: 'default'},
                { text: 'ambiance-mobile',   value: 'ambiance-mobile'},
                { text: 'ambiance',          value: 'ambiance'},
                { text: 'blackboard',        value: 'blackboard'},
                { text: 'cobalt',            value: 'cobalt'},
                { text: 'eclipse',           value: 'eclipse'},
                { text: 'erlang-dark',       value: 'erlang-dark'},
                { text: 'lesser-dark',       value: 'lesser-dark'},
                { text: 'monokai',           value: 'monokai'},
                { text: 'neat',              value: 'neat'},
                { text: 'rubyblue',          value: 'rubyblue'},
                { text: 'solarized',         value: 'solarized'},
                { text: 'twilight',          value: 'twilight'},
                { text: 'vibrant-ink',       value: 'vibrant-ink'},
                { text: 'xq-dark',           value: 'xq-dark'}
            ];
            this.widgets.themeMenuButton = new YAHOO.widget.Button({
                id: 'themeButton',
                name: 'themeButton',
                label: this.msg('button.codemirror.theme') + (Alfresco.constants.MENU_ARROW_SYMBOL !== undefined ? ('&nbsp;' + Alfresco.constants.MENU_ARROW_SYMBOL) : ''),
                type: 'menu',
                menu: themeMenuItems,
                container: this.id + '-theme'
            });

            if (browserSupportsHtml5Storage())
            {
                // preselect item
                theme = window.localStorage['javascript.console.codemirror.theme'];
                if (theme)
                {
                    menuItems = this.widgets.themeMenuButton.getMenu().getItems();
                    for (i = 0; i < menuItems.length; i++)
                    {
                        menuItem = menuItems[i];
                        if (theme == menuItem.cfg.getProperty('text'))
                        {
                            menuItem.cfg.setProperty('checked', true);
                        }
                    }
                }
            }

            this.widgets.themeMenuButton.getMenu().subscribe('click', this.onThemeSelection, this);
            this.widgets.themeMenuButton.getMenu().cfg.setProperty('zIndex', 10);
        },

        /**
         * create the display options menu for dumps
         */
        createDumpDisplayMenu: function JavaScriptConsole_createDumpDisplayMenu()
        {
            var displayMenu = new YAHOO.widget.Menu('nowhere');
            displayMenu.addItem({text: 'Hide equal values', value: 'Differences'});
            displayMenu.addItem({text: 'Hide different values', value: 'highlightDifferences'});
            displayMenu.addItem({text: 'Hide null values', value: 'nullValues'});

            this.widgets.dumpDisplayMenu = new YAHOO.widget.Button({
                type: 'split',
                label: 'Display options',
                name: 'dumpDisplayButton',
                menu: displayMenu,
                container: 'splitButtonContainer',
                disabled: false
            });

            this.widgets.dumpDisplayMenu.getMenu().cfg.setProperty('zIndex', 10);
            this.widgets.dumpDisplayMenu.on('appendTo', function ()
            {
                menu = this.getMenu();
                menu.subscribe('click', function onMenuClick(sType, oArgs)
                {
                    var oMenuItem = oArgs[1];
                    if (oMenuItem)
                    {
                        dt.showColumn(dt.getColumn(oMenuItem.value));
                        menu.removeItem(oMenuItem.index);
                        refreshButton();
                    }
                });
            });
        },

        setupCodeEditors: function JavaScriptConsole_setupCodeEditors()
        {
            var uiMirrorScript, uiMirrorTemplate, uiMirrorJSON;
            
            CodeMirror.commands.autocomplete = function autoComplete(cm)
            {
                CodeMirror.showHint(cm, function autoComplete_hint(cm)
                    {
                        return CodeMirror.showHint(cm, CodeMirror.ternHint,
                            {
                                async: true
                            }
                        );
                    }
                );
            };

            // Attach the CodeMirror highlighting
            uiMirrorScript = new CodeMirrorUI(this.widgets.scriptInput,
                {
                    imagePath:Alfresco.constants.URL_RESCONTEXT + 'components/ootbee-support-tools/codemirror-ui/images',
                    searchMode:'no'
                },
                {
                    mode: 'javascript',
                    styleActiveLine: true,
                    showCursorWhenSelecting:true,
                    lineNumbers: true,
                    lineWrapping: true,
                    matchBrackets: true,
                    tabSize: 4,
                    indentUnit: 4,
                    indentWithTabs: true,
                    autofocus:true,
                    onKeyEvent: this.onEditorKeyEvent,
                    extraKeys: {
                        "'.'": function passAndHint(cm)
                        {
                            setTimeout(function()
                            {
                                cm.execCommand('autocomplete');
                            }, 100);
                            return CodeMirror.Pass;
                        },
                        'Ctrl-I': function showType(cm)
                        {
                            CodeMirror.tern.showType(cm);
                        },
                        'Ctrl-Space': 'autocomplete',
                        'Ctrl-Enter': function execute(cm)
                        {
                            cm.owner.onExecuteClick(cm.owner);
                        }
                    }
                }
            );

            this.widgets.codeMirrorScript = uiMirrorScript.getMirrorInstance();
            this.widgets.codeMirrorScript.on('cursorActivity', function(cm)
            {
                showStatusInfo(cm, '.scriptStatusLine');
            });

            this.widgets.codeMirrorScript.getInputField().blur();

            uiMirrorTemplate = new CodeMirrorUI(this.widgets.templateInput,
                {
                    imagePath:Alfresco.constants.URL_RESCONTEXT + 'components/ootbee-support-tools/codemirror-ui/images',
                    searchMode:'no'
                },
                {
                    lineNumbers: true,
                    lineWrapping: true,
                    mode: 'freemarker',
                    styleActiveLine: true,
                    highlightSelectionMatches: true,
                    showCursorWhenSelecting: true,
                    matchBrackets: true,
                    showTrailingSpace: true,
                    onKeyEvent: this.onEditorKeyEvent,
                    markParen: function(node, ok)
                    {
                        node.style.backgroundColor = ok ? '#CCF' : '#FCC#';
                        if (!ok)
                        {
                            node.style.color = 'red';
                        }
                    },
                    unmarkParen: function(node)
                    {
                        node.style.backgroundColor = '';
                        node.style.color = '';
                    },
                    indentUnit: 4
                }
            );

            this.widgets.codeMirrorTemplate = uiMirrorTemplate.getMirrorInstance();
            this.widgets.codeMirrorTemplate.on('cursorActivity', function(cm)
            {
                showStatusInfo(cm, '.templateStatusLine');
            });

            this.widgets.codeMirrorTemplate.getInputField().blur();


            // Attach the CodeMirror highlighting
            uiMirrorJSON = new CodeMirrorUI(this.widgets.jsonOutput,
                {
                    searchMode: 'no',
                    imagePath: Alfresco.constants.URL_RESCONTEXT + 'components/ootbee-support-tools/codemirror-ui/images'
                },
                {
                    mode: 'application/json',
                    styleActiveLine: true,
                    readOnly: true,
                    showCursorWhenSelecting: true,
                    highlightSelectionMatches: true,
                    gutters: ['CodeMirror-lint-markers'],
                    lintWith: CodeMirror.jsonValidator,
                    lineNumbers: true,
                    lineWrapping: true,
                    matchBrackets: true,
                    onKeyEvent: this.onEditorKeyEvent
                }
            );

            this.widgets.codeMirrorJSON = uiMirrorJSON.getMirrorInstance();
            this.widgets.codeMirrorJSON.on('cursorActivity', function(cm)
            {
                showStatusInfo(cm, '.jsonStatusLine');
            });

            this.widgets.codeMirrorJSON.getInputField().blur();

            // Store this for use in event
            this.widgets.codeMirrorScript.owner = this;
            this.widgets.codeMirrorTemplate.owner = this;
            this.widgets.codeMirrorJSON.owner = this;
        },

        setupLocalStorage: function JavaScriptConsole_setupLocalStorage()
        {
            var self, javascriptText, theme;

            // Store and Restore script content to and from local storage
            if (browserSupportsHtml5Storage())
            {
                self = this;
                window.onbeforeunload = function(e)
                {
                    self.beforeUnload();
                };

                if (window.localStorage['javascript.console.config.runas'])
                {
                    this.widgets.config.runas.value = window.localStorage['javascript.console.config.runas'];
                }

                if (window.localStorage['javascript.console.config.transactions'])
                {
                    this.widgets.config.transactions.value = window.localStorage['javascript.console.config.transactions'];
                }

                if (window.localStorage['javascript.console.config.urlarguments'])
                {
                    this.widgets.config.urlarguments.value = window.localStorage['javascript.console.config.urlarguments'];
                }

                if (window.localStorage['javascript.console.config.runlikecrazy'])
                {
                    this.widgets.config.runlikecrazy.value = window.localStorage['javascript.console.config.runlikecrazy'];
                }

                if (window.localStorage['javascript.console.script'])
                {
                    javascriptText = window.localStorage['javascript.console.script'];
                    this.widgets.codeMirrorScript.setValue(javascriptText);
                }

                if (window.localStorage['javascript.console.template'])
                {
                    this.widgets.codeMirrorTemplate.setValue(window.localStorage['javascript.console.template']);
                }

                if (window.localStorage['javascript.console.codemirror.theme'])
                {
                    theme = window.localStorage['javascript.console.codemirror.theme'];
                    this.widgets.codeMirrorScript.setOption('theme',theme);
                    this.widgets.codeMirrorTemplate.setOption('theme',theme);
                }
            }
        },
    
        /**
         * Generates the templates for properties, types and aspects.
         */
        generateTemplates: function JavaScriptConsole_generateTemplates(dictionary)
        {
            var templates, propertyNames, assocNames, t, cls, templDescription, parent, aspect, property, prop, propDescription, association, assoc, assocDescription;

            templates = [];
            propertyNames = [];
            assocNames = [];

            for (t in dictionary)
            {
                if (dictionary.hasOwnProperty(t))
                {
                    cls = dictionary[t];

                    templDescription = 'title:\t\t\t\t' + cls.title;
                    templDescription += '\ndescription:\t\t' + cls.description;
                    templDescription += '\nisContainer:\t\t' + cls.isContainer;

                    parent = cls.parent;
                    if (parent)
                    {
                        templDescription += '\nparent:\t\t\t' + parent.name + '(' + parent.title + ')';
                    }

                    if (cls.defaultAspects)
                    {
                        templDescription += '\ndefaultAspects:\n';
                        for (aspect in cls.defaultAspects)
                        {
                            if (cls.defaultAspects.hasOwnProperty(aspect))
                            {
                                templDescription += '\t\t\t\t' + cls.defaultAspects[aspect].name +
                                    '(' + cls.defaultAspects[aspect].title + ')\n';
                            }
                        }
                    }

                    properties = cls.properties;
                    if (properties)
                    {
                        templDescription += '\nproperties:\n';
                        for (property in cls.properties)
                        {
                            if (cls.properties.hasOwnProperty(property))
                            {
                                prop = cls.properties[property];

                                templDescription += '\t\t\t\t' + prop.name + ' (' + prop.dataType + ')\n';

                                /* jshint -W069 */
                                // yui-compressor is unable to handle prop.protected
                                propDescription = 'title:\t\t\t\t' + prop.title + '\ndescription:\t\t\t' + prop.description + '\ndataType:\t\t\t' +
                                    prop.dataType + '\ndefaultValue:\t\t' + prop.defaultValue + '\nmultivalued:\t\t' +
                                    prop.multiValued + '\nmandatory:\t\t\t' + prop.mandatory + '\nenforced:\t\t\t' +
                                    prop.enforced + '\nprotected:\t\t\t' + prop['protected'] + '\nindexed:\t\t\t' + prop.indexed;
                                /* jshint +W069 */

                                if (Alfresco.util.arrayIndex(propertyNames, prop.name) === -1)
                                {
                                    templates.push({
                                        name: 'PROP_' + prop.name.replace(/:/g,'_').toUpperCase(),
                                        description: propDescription,
                                        template: prop.name,
                                        className: 'CodeMirror-hint-alfresco'
                                    });
                                    propertyNames.push(prop.name);
                                }
                            }
                        }
                    }

                    if (cls.associations)
                    {
                        templDescription += '\nassociations:\n';
                        for (association in cls.associations)
                        {
                            if (cls.associations.hasOwnProperty(association))
                            {
                                assoc = cls.associations[association];
                                templDescription += '\t\t\t\t' + assoc.name + '(' + assoc.title + ')\n';

                                if (Alfresco.util.arrayIndex(assocNames, assoc.name) === -1)
                                {
                                    assocDescription = 'isChildAssoc:\t\tfalse\ntitle:\t\t\t\t' + assoc.title +
                                        '\nsource:\t\t\t' + '\n\tclass:\t\t' + assoc.source['class'] + '\n\tmandatory:\t' +
                                        assoc.source.mandatory + '\n\tmany:\t\t' + assoc.source.many +
                                        '\ntarget:\t\t' + '\n\tclass:\t\t' + assoc.target['class'] + '\n\tmandatory:\t' +
                                        assoc.target.mandatory + '\n\tmany:\t\t' + assoc.target.many;

                                    templates.push({
                                        name: 'ASSOC_' + assoc.name.replace(/:/g,'_').toUpperCase(),
                                        description: assocDescription,
                                        template: assoc.name,
                                        className: 'CodeMirror-hint-alfresco'
                                    });
                                    assocNames.push(assoc.name);
                                }
                            }
                        }
                    }

                    if (cls.childassociations)
                    {
                        templDescription += '\nchildassociations:\n';
                        for (association in cls.childassociations)
                        {
                            if (cls.childassociations.hasOwnProperty(association))
                            {
                                assoc = cls.childassociations[association];
                                templDescription += '\t\t\t\t' + assoc.name + '(' + assoc.title + ')\n';

                                if (Alfresco.util.arrayIndex(assocNames, assoc.name) === -1)
                                {
                                    assocDescription = 'isChildAssoc:\t\ttrue\ntitle:\t\t\t\t' + assoc.title +
                                        '\nsource:\t\t' + '\n\tclass:\t\t' + assoc.source['class'] + '\n\tmandatory:\t' +
                                        assoc.source.mandatory + '\n\tmany:\t\t' + assoc.source.many +
                                        '\ntarget:\t\t\t' + '\n\tclass:\t\t' + assoc.target['class'] + '\n\tmandatory:\t' +
                                        assoc.target.mandatory + '\n\tmany:\t\t' + assoc.target.many;

                                    templates.push({
                                        name: 'ASSOC_' + assoc.name.replace(/:/g,'_').toUpperCase(),
                                        description: assocDescription,
                                        template: assoc.name,
                                        className: 'CodeMirror-hint-alfresco'
                                    });
                                    assocNames.push(assoc.name);
                                }
                            }
                        }
                    }
                }

                templateName = (cls.isAspect ? 'ASPECT' : 'TYPE') + '_' + cls.name.replace(/:/g,'_');
                templates.push({
                    name: templateName.toUpperCase(),
                    description: templDescription,
                    template: cls.name,
                    className: 'CodeMirror-hint-alfresco'
                });
            }

            templates.sort(function(a,b)
            {
                return a.name.toLowerCase().localeCompare(b.name.toLowerCase());
            });

            return templates;
        },
    
        /**
         * Generates the templates for actions.
         */
        generateActionDefinitionTemplates: function JavaScriptConsole_generateActionDefinitionTemplates(definitions)
        {
            var templates, t, action, templDescription, p;

            templates = [];

            for (t in definitions)
            {
                if (definitions.hasOwnProperty(t))
                {
                    action = definitions[t];
                
                    templDescription = 'name:\t\t\t\t' + action.name;
                    templDescription += '\ndisplayLabel:\t\t\t' + action.displayLabel;
                    templDescription += '\ndescription:\t\t\t\t' + action.description;
                    templDescription += '\nadHocPropertiesAllowed:\t' + action.adHocPropertiesAllowed;
                    templDescription += '\nparameters:\n';

                    for (p in action.parameterDefinitions)
                    {
                        if (action.parameterDefinitions.hasOwnProperty(p))
                        {
                            param = action.parameterDefinitions[p];
                            templDescription += '\t\t\t\t\t' + param.name + ' (' + param.type + ')\n';
                        }
                    }

                    templates.push({
                        name: ('ACTION_' + action.name).toUpperCase(),
                        description: templDescription,
                        template: action.name,
                        className: 'CodeMirror-hint-alfresco'
                    });
                }
            }

            templates.sort(function(a,b)
            {
                return a.name.toLowerCase().localeCompare(b.name.toLowerCase());
            });

            return templates;
        },
    
        /**
         * Generates the templates for workflow definitions.
         */
        generateWorkflowDefinitionTemplates: function JavaScriptConsole_generateWorkflowDefinitionTemplates(definitions)
        {
            var templates, t, wfDef, templDescription;

            templates = [];

            for (t in definitions)
            {
                if (definitions.hasOwnProperty(t))
                {
                    wfDef = definitions[t];

                    templDescription = 'id:\t\t\t' + wfDef.id + '\ntitle:\t\t\t' + wfDef.title +
                        ' \ndescription: \t' + wfDef.description + ' \nversion: \t\t' + wfDef.version;

                    templates.push({
                        name: ('WFL_' + wfDef.name.replace(/\$/g,'_')).toUpperCase(),
                        description: templDescription,
                        template: wfDef.name,
                        className: 'CodeMirror-hint-alfresco'
                    });
                }
            }

            templates.sort(function(a,b)
            {
                return a.name.toLowerCase().localeCompare(b.name.toLowerCase());
            });

            return templates;
        },

        beforeUnload: function JavaScriptConsole_beforeUnload()
        {
            this.widgets.codeMirrorScript.save();
            window.localStorage['javascript.console.script'] = this.widgets.scriptInput.value;
            this.widgets.codeMirrorTemplate.save();
            window.localStorage['javascript.console.template'] = this.widgets.templateInput.value;

            if (this.widgets.config.runas)
            {
                window.localStorage['javascript.console.config.runas'] = this.widgets.config.runas.value;
            }
            if (this.widgets.config.transactions)
            {
                window.localStorage['javascript.console.config.transactions'] = this.widgets.config.transactions.value;
            }

            if (this.widgets.config.urlarguments)
            {
                window.localStorage['javascript.console.config.urlarguments'] = this.widgets.config.urlarguments.value;
            }
            if (this.widgets.config.runlikecrazy)
            {
                window.localStorage['javascript.console.config.runlikecrazy'] = this.widgets.config.runlikecrazy.value;
            }

            window.localStorage['javascript.console.codemirror.theme'] = this.widgets.codeMirrorScript.options.theme;
        },

        loadRepoDefinitions: function JavaScriptConsole_loadRepoDefinitions()
        {
            // Read the Alfresco Data Dictionary for code completion (types and
            // aspects)
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'api/classes',
                successCallback:
                {
                    fn: function(res)
                    {
                        this.dictionary = res.json;
                        CodeMirror.templatesHint.addTemplates({
                            name: 'alfresco_datatypes',
                            context: 'javascript',
                            templates: this.generateTemplates(this.dictionary)
                        });
                    },
                    scope: this
                }
            });

            // Read the Alfresco workflow definitions for code completion (types and  aspects)
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'api/workflow-definitions',
                successCallback: {
                    fn: function(res)
                    {
                        CodeMirror.templatesHint.addTemplates({
                          name: 'alfresco_wfl_templates',
                          context: 'javascript',
                          templates: this.generateWorkflowDefinitionTemplates(res.json.data)
                        });
                    },
                    scope: this
                }
            });

            // Read the Alfresco workflow definitions for code completion (types and aspects)
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'api/actiondefinitions',
                successCallback: {
                    fn: function(res)
                    {
                        CodeMirror.templatesHint.addTemplates({
                            name: 'alfresco_action_templates',
                            context: 'javascript',
                            templates: this.generateActionDefinitionTemplates(res.json.data)
                        });
                    },
                    scope: this
                }
            });
        },

        loadRepoScriptList: function JavaScriptConsole_loadRepoScriptList()
        {
            // Load Scripts from Repository
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/listscripts.json',
                successCallback:
                {
                    fn: function(res)
                    {
                        this.createOrUpdateScriptsSaveMenu(res.json.scripts);
                        this.createOrUpdateScriptsLoadMenu(res.json.scripts);
                    },
                    scope: this
                }
            });
        },

        setupResizableEditor: function() {
            var self, resize;

            self = this;
            resize = new YAHOO.util.Resize(this.id + '-inputContentArea',
                {
                    handles: ['b']
                }
            );

            function doResize(ev)
            {
                self.widgets.codeMirrorScript.setSize(null, ev.height - 50);
                self.widgets.codeMirrorTemplate.setSize(null, ev.height - 50);
                self.widgets.codeMirrorJSON.setSize(null, ev.height - 50);

                Dom.setStyle(self.id + '-inputContentArea', 'width', 'inherit');
            }

            resize.on('resize', doResize);
            resize.on('endResize', doResize);

            // Recalculate the horizontal size on a browser window resize event
            Event.on(window, 'resize', function(e)
                {
                    // YAHOO.util.Resize sets an absolute width, reset to auto width
                    Dom.setStyle(self.id + '-inputContentArea', 'width', 'inherit');
                }, this, true);
        },

        showResultTable: function JavaScriptConsole_showResultTable(resultData)
        {
            var allFields, row, fieldName, myColumnDefs, responseSchemaFields, myDataSource;

            allFields = {};

            for (row in resultData)
            {
                if (resultData.hasOwnProperty(row))
                {
                    for (fieldName in resultData[row])
                    {
                        if (resultData[row].hasOwnProperty(fieldName))
                        {
                            allFields[fieldName] = 1;
                        }
                    }
                }
            }

            myColumnDefs = [];
            responseSchemaFields = [];

            for (fieldName in allFields)
            {
                if (allFields.hasOwnProperty(fieldName))
                {
                    responseSchemaFields.push(fieldName);
                    myColumnDefs.push({
                        key: fieldName,
                        sortable: true,
                        resizeable: true
                    });
                }
            }

            if (myColumnDefs.length > 0)
            {
                myDataSource = new YAHOO.util.DataSource(resultData);
                myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
                myDataSource.responseSchema = {
                    fields: responseSchemaFields
                };

                Dom.setStyle(this.id + '-datatable', 'display', 'block');
                this.widgets.resultTable = new YAHOO.widget.DataTable(this.id + '-datatable', myColumnDefs, myDataSource,
                    {
                        draggableColumns: true
                    }
                );
                Dom.setStyle(this.widgets.exportResultsButton, 'display', 'inline-block');
            }
            else
            {
                Dom.setStyle(this.id + '-datatable', 'display', 'none');
                this.widgets.resultTable = null;
                Dom.setStyle(this.widgets.exportResultsButton, 'display', 'none');
            }
        },

        /**
         * Exports the datatable as CSV in a separate browser window taken from
         * http://stackoverflow.com/questions/2472424/exporting-data-from-a-yui-datatable
         */
        exportResultTableAsCSV: function JavaScriptConsole_exportResultTableAsCSV()
        {
            var i, j, oData, newWin, aRecs, aCols;

            if (this.widgets.resultTable)
            {
                newWin = window.open();
                aRecs = this.widgets.resultTable.getRecordSet().getRecords();
                aCols = this.widgets.resultTable.getColumnSet().keys;

                newWin.document.write('<pre>');

                for (j = 0; j < aCols.length; j++)
                {
                    newWin.document.write(aCols[j].key + '\t');
                }
                newWin.document.write('\n');

                for (i = 0; i < aRecs.length; i++)
                {
                    oData = aRecs[i].getData();

                    for (j = 0; j < aCols.length; j++)
                    {
                        newWin.document.write(oData[aCols[j].key] + '\t');
                    }
                    newWin.document.write('\n');
                }

                newWin.document.write('</pre>\n');
                newWin.document.close();
            }
        },

        onRefreshServerInfoClick: function JavaScriptConsole_onRefreshServerInfoClick()
        {
            this.widgets.repoInfoOutput.innerHTML = 'Loading ...';

            // Read Javascript API Commands for code completion
            Alfresco.util.Ajax.jsonGet({
                url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/serverInfo',
                successCallback: {
                    fn: function(res)
                    {
                        var serverInfoAsJson = res.json;
                        Dom.addClass(this.widgets.repoInfoOutput, 'jsgreen');
                        this.widgets.repoInfoOutput.innerHTML = 'host name/ip:\t\t' + serverInfoAsJson.hostName + ' (' + serverInfoAsJson.hostAddress + ')' +
                            '\nhost os:\t\t' + serverInfoAsJson.osname + ' (arch: ' + serverInfoAsJson.arch + ', version: ' + serverInfoAsJson.osversion + ')' +
                            '\nhost processors:\t' + serverInfoAsJson.processorCount +
                            '\nhost total memory (MB):\t' + serverInfoAsJson.totalMemory + ' (free: ' + serverInfoAsJson.freeMemory + ')' +
                            '\nhost current user:\t' + serverInfoAsJson.hostUserInfo+
                            '\n\njava uptime:\t\t' + serverInfoAsJson.javaUptime +
                            '\njava version:\t\t' + serverInfoAsJson.java +
                            '\njava threads:\t\t' + serverInfoAsJson.threadCount + ' (deadlocked: ' + serverInfoAsJson.deadlockThreads + ')' +
                            '\njava heap:\t\t' + serverInfoAsJson.heapUsed + ' MiB (init: ' + serverInfoAsJson.heapInit + ' MiB, committed: ' + serverInfoAsJson.heapCommitted + ' MiB, max: ' + serverInfoAsJson.heapMax + ' MiB)' +
                            '\n\nalfresco version:\t' + serverInfoAsJson.serverEdition + ' ' + serverInfoAsJson.serverVersion + ' (' + serverInfoAsJson.serverSchema + ')' +
                            '\npatches:\t\t' + serverInfoAsJson.patchCount +
                            '\ninstalled modules:\t' + serverInfoAsJson.installedModuleCount +
                            '\nmissing modules:\t' + serverInfoAsJson.missingModuleCount +
                            '\ntenants:\t\t' + serverInfoAsJson.tenantCount +
                            '\n\ntransactions:\t\t' + serverInfoAsJson.transactionsCount +
                            '\ndocuments:\t\t' + serverInfoAsJson.docsCount + ' (checked out: ' + serverInfoAsJson.checkedOutCount + ')' +
                            '\nfolder:\t\t\t' + serverInfoAsJson.folderCount +
                            '\ngroups:\t\t\t' + serverInfoAsJson.groupsCount +
                            '\nusers:\t\t\t' + serverInfoAsJson.peopleCount +
                            '\nsites:\t\t\t' + serverInfoAsJson.sitesCount +
                            '\ntags:\t\t\t' + serverInfoAsJson.tagsCount +
                            '\nclassifications:\t' + serverInfoAsJson.classifications +
                            '\n\nworkflow definitions:\t' + serverInfoAsJson.wflDefinitionCount +
                            '\nworkflow instances:\t' + serverInfoAsJson.workflowCount +
                            '\n\nrunning actions:\t' + serverInfoAsJson.runningActions +
                            '\nrunning jobs:\t\t' + serverInfoAsJson.runningJobs;
                    },
                    scope: this
                }
            });
        },

        /**
         * Fired when the user clicks on the execute button. Reads the script
         * from the input textarea and calls the execute webscript in the
         * repository to run the script.
         *
         * @method onExecuteClick
         */
        onExecuteClick: function JavaScriptConsole_onExecuteClick(e)
        {
            var scriptCode, templateCode, rq;

            // Save any changes done in CodeMirror editor before submitting
            this.widgets.codeMirrorScript.save();
            this.widgets.codeMirrorTemplate.save();

            // If something is selected, only get the selected part of the script
            if (this.widgets.codeMirrorScript.somethingSelected())
            {
                scriptCode = this.widgets.codeMirrorScript.getSelection();
            }
            else
            {
                scriptCode = this.widgets.scriptInput.value;
            }

            templateCode = this.widgets.templateInput.value;

            // Build JSON Object to send to the server
            rq = {
                script: scriptCode,
                template: templateCode,
                spaceNodeRef: this.widgets.nodeField.value,
                transaction: this.widgets.config.transaction.value ? this.widgets.config.transaction.value : 'readwrite',
                runas: this.widgets.config.runas.value ? this.widgets.config.runas.value : 'admin',
                urlargs: this.widgets.config.urlargs.value ? this.widgets.config.urlargs.value : '',
                documentNodeRef: this.options.documentNodeRef
            };

            // Disable the result textarea
            this.widgets.scriptOutput.disabled = true;
            this.widgets.executeButton.disabled = true;

            this.showLoadingAjaxSpinner(true);

            this.executeStartTime = new Date();
            rq.resultChannel = String(this.executeStartTime.getTime());

            Alfresco.util.Ajax.jsonPost({
                url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/execute',
                dataObj: rq,
                successCallback: {
                    fn: function JavaScriptConsole_onExecuteClick_success(res)
                    {
                        this.fetchResult();

                        this.fetchResultTimer.cancel();
                        this.fetchResultTimer = null;
                        this.showLoadingAjaxSpinner(false);
                        this.printExecutionStats(res.json);
                        this.printDumpInfos(res.json.dumpOutput);
                        this.clearOutput();
                        this.appendLineArrayToOutput(res.json.printOutput);
                        this.widgets.templateOutputHtml.innerHTML = res.json.renderedTemplate;
                        this.widgets.templateOutputText.innerHTML = $html(res.json.renderedTemplate);
                        this.widgets.codeMirrorJSON.setValue(formatter.formatJson(res.json.renderedTemplate,'  '));
                        this.widgets.codeMirrorJSON.focus();

                        if (res.json.spaceNodeRef)
                        {
                            this.widgets.nodeField.value = res.json.spaceNodeRef;
                            this.widgets.pathField.innerHTML = res.json.spacePath;
                        }
                        this.widgets.scriptOutput.disabled = false;
                        this.widgets.templateOutputHtml.disabled = false;
                        this.widgets.templateOutputText.disabled = false;
                        this.widgets.executeButton.disabled = false;

                        this.showResultTable(res.json.result);
                        Dom.removeClass(this.widgets.scriptOutput, 'jserror');
                        Dom.addClass(this.widgets.scriptOutput, 'jsgreen');

                        this.runLikeCrazy();
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function JavaScriptConsole_onExecuteClick_failure(res)
                    {
                        var result;

                        if (res.serverResponse.status !== 408)
                        {
                            this.fetchResult();

                            this.fetchResultTimer.cancel();
                            this.fetchResultTimer = null;
                            this.showLoadingAjaxSpinner(false);
                            this.printExecutionStats();
    
                            result = YAHOO.lang.JSON.parse(res.serverResponse.responseText);
    
                            this.markJSError(result);
                            this.markFreemarkerError(result);
    
                            this.clearOutput();
                            this.setOutputText(result.status.code + ' ' + result.status.name +
                                '\nStacktrace-Details:\n' + result.callstack+'\n\n' + result.status.description + '\n' + result.message);
    
                            this.widgets.scriptOutput.disabled = false;
                            this.widgets.executeButton.disabled = false;
                            Dom.removeClass(this.widgets.scriptOutput, 'jsgreen');
                            Dom.addClass(this.widgets.scriptOutput, 'jserror');
                            this.widgets.outputTabs.selectTab(0); // show console tab
    
                            this.runLikeCrazy();
                        }
                    },
                    scope: this
                }
            });
        
            // remove any marking
            Dom.removeClass(this.widgets.scriptOutput, 'jserror');
            Dom.removeClass(this.widgets.scriptOutput, 'jsgreen');
        
            // fetch result updates to the print output after a second
            this.fetchResultTimer = YAHOO.lang.later(1000, this, this.fetchResult, null, false);
        },

        fetchResult: function JavaScriptConsole_fetchResult()
        {
            // double check that execution is still ongoing
            if (this.widgets.executeButton.disabled)
            {
                // this is a best-effort update - we do not care about failures
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/' +
                        encodeURIComponent(String(this.executeStartTime.getTime())) + '/executionResult',
                    successCallback: {
                        fn: function(response)
                        {
                            // double check that execution is still ongoing
                            if (this.widgets.executeButton.disabled)
                            {
                                if (YAHOO.lang.isObject(response.json))
                                {
                                    if (YAHOO.lang.isArray(response.json.printOutput))
                                    {
                                        this.clearOutput();
                                        this.appendLineArrayToOutput(response.json.printOutput);
                                    }
                              
                                    // either error or result signal completion
                                    if (response.json.error !== undefined || YAHOO.lang.isArray(response.json.result))
                                    {
                                        if (this.fetchResultTimer !== null)
                                        {
                                            this.fetchResultTimer.cancel();
                                            this.fetchResultTimer = null;
                                        }
                                        this.showLoadingAjaxSpinner(false);
                                      
                                        if (YAHOO.lang.isArray(response.json.result))
                                        {
                                            this.printExecutionStats(response.json);
                                        }
                                        else
                                        {
                                            this.printExecutionStats();
                                        }
                                      
                                        if (YAHOO.lang.isArray(response.json.result))
                                        {
                                            this.widgets.templateOutputHtml.innerHTML = response.json.renderedTemplate;
                                            this.widgets.templateOutputText.innerHTML = $html(response.json.renderedTemplate);
                                            this.widgets.codeMirrorJSON.setValue(formatter.formatJson(response.json.renderedTemplate,'  '));
                                            this.widgets.codeMirrorJSON.focus();
                                          
                                            if (response.json.spaceNodeRef)
                                            {
                                                this.widgets.nodeField.value = response.json.spaceNodeRef;
                                                this.widgets.pathField.innerHTML = response.json.spacePath;
                                            }
                                        }
            
                                        this.widgets.scriptOutput.disabled = false;
                                        this.widgets.templateOutputHtml.disabled = false;
                                        this.widgets.templateOutputText.disabled = false;
                                        this.widgets.executeButton.disabled = false;
            
                                        if (YAHOO.lang.isArray(response.json.result))
                                        {
                                            this.showResultTable(response.json.result);
                                            Dom.removeClass(this.widgets.scriptOutput, 'jserror');
                                            Dom.addClass(this.widgets.scriptOutput, 'jsgreen');
                                            this.runLikeCrazy();
                                        }
                                    }
                                }

                                if (this.widgets.executeButton.disabled)
                                {
                                    // fetch further result updates to the print output after a second
                                    this.fetchResultTimer = YAHOO.lang.later(1000, this, this.fetchResult, null, false);
                                }
                            }
                        },
                        scope: this
                    }
                });
            }
        },

        runLikeCrazy: function JavaScriptConsole_runLikeCrazy()
        {
            var self = this;
            if (this.widgets.config.runlikecrazy.value > 0)
            {
                window.setTimeout(function()
                    {
                        self.onExecuteClick();
                    }, this.widgets.config.runlikecrazy.value
                );
            }
        },

        /**
         * marks the error in the code mirror editor if there is any line hint
         * in the error message.
         */
        markJSError: function JavaScriptConsole_markJSError(result)
        {
            var regex, callStackLineIndicator, line, selectionEnd, from, to;

            // the submitted script will use an MD5 hash as a name which helps us a bit in distinguishing it from other scripts being executed (i.e. a script action)
            regex = /\([a-f0-9]+\.js#(\d+)\)/;
            callStackLineIndicator = regex.exec(result.callstack);
            
            if (callStackLineIndicator)
            {
                // show the javascript window
                this.widgets.inputTabs.selectTab(0);
                this.widgets.codeMirrorScript.focus();

                // create a marker for the editor to indicate that there was an error!
                // offset by -1 for internal 0-based count
                line = parseInt(callStackLineIndicator[1]) + result.scriptOffset - 1;
                // is the line actually part of the user script?
                if (line >= 0)
                {
                    if (this.widgets.codeMirrorScript.somethingSelected())
                    {
                        line = line + this.widgets.codeMirrorScript.getCursor().line - 1;
                    }
                    selectionEnd = this.widgets.codeMirrorScript.getLineHandle(line).text.length;
                    from = {
                        line: line,
                        ch:0
                    };
                    to = {
                        line: line,
                        ch: selectionEnd
                    };
                    this.widgets.codeMirrorScript.markText(from, to,
                        {
                            clearOnEnter: 'true',
                            className: 'CodeMirror-lint-mark-error',
                            __annotation: {
                                from: from,
                                to: to,
                                severity: 'error',
                                message: result.message
                            }
                        }
                    );
                }
            }
        },

        /**
         * marks the error in the code mirror editor if there is any line hint
         * in the error message.
         */
        markFreemarkerError: function JavaScriptConsole_markFreemarkerError(result)
        {
            var regex, line, selectionEnd;

            if (result.callstack)
            {
                regex = /line (\d*), column (\d*)/;
                callStackLineIndicator = regex.exec(result.callstack);
                if(callStackLineIndicator)
                {
                    this.widgets.inputTabs.selectTab(1);
                    this.widgets.codeMirrorTemplate.focus();
                    line = callStackLineIndicator[1] - 1;
                    selectionEnd = this.widgets.codeMirrorTemplate.getLineHandle(line).text.length;
                    this.widgets.codeMirrorTemplate.markText(
                        {
                            line: line,
                            ch: 0
                        },
                        {
                            line: line,
                            ch: selectionEnd
                        },
                        {
                            clearOnEnter: 'true',
                            className: 'CodeMirror-lint-mark-error',
                            __annotation: {
                                message: result.message
                            }
                        }
                    );
                }
            }
        },

        showLoadingAjaxSpinner: function JavaScriptConsole_showLoadingAjaxSPinner(showSpinner) {
            var spinner = Dom.get(this.id + '-spinner');
            Dom.setStyle(spinner, 'display', showSpinner ? 'inline' : 'none');
        },

        printExecutionStats: function JavaScriptConsole_printExecutionStats(json)
        {
            var overallPerf, stats, now, nowAsString, text, webscriptPerf, fmPerf, scriptPerf, networkPerf, serverCodePerf, overallEl, scriptEl, fmEl, codeEl, networkEl;

            this.executeEndTime = new Date();
            overallPerf = this.executeEndTime - this.executeStartTime;

            stats = Dom.get(this.id + '-executionStatsSimple');
            now = new Date();
            nowAsString = now.getFullYear() + '-' + (now.getMonth() < 9 ? '0' : '') + (now.getMonth() + 1) + '-' + (now.getDate() < 10 ? '0' : '') + now.getDate() + ' ' + (now.getHours() < 10 ? '0' : '') + now.getHours() + ':' + (now.getMinutes() < 10 ? '0' : '') + now.getMinutes() + ':' + (now.getSeconds() < 10 ? '0' : '') + now.getSeconds();
            text = ' - ' + this.msg('label.stats.executed.last') + ' ' + (overallPerf) + 'ms (' + nowAsString + ')';
            stats.innerHTML = '';
            stats.appendChild(document.createTextNode(text));

            if (!Event.getListeners(stats, 'click'))
            {
                Event.on(stats, 'click', function()
                {
                    this.widgets.outputTabs.selectTab(4);
                }, null, this);
            }

            webscriptPerf = '-';
            fmPerf = '-';
            scriptPerf = '-';
            networkPerf = '-';
            serverCodePerf = '-';

            if (json)
            {
                scriptPerf = parseInt(json.scriptPerf || '0', 10);
                fmPerf = parseInt(json.freemarkerPerf || '0', 10);

                if (fmPerf === undefined)
                {
                    fmPerf = 0;
                }

                webscriptPerf = parseInt(json.webscriptPerf || '0', 10);
                serverCodePerf = webscriptPerf - scriptPerf - fmPerf;

                networkPerf = overallPerf - webscriptPerf;
            }

            overallEl = YAHOO.lang.substitute(this.template, {
                name: this.msg('label.stats.executed.in'),
                value: overallPerf + 'ms'
            });

            scriptEl = YAHOO.lang.substitute(this.template, {
                name: this.msg('label.stats.jscript.executed.in'),
                value: scriptPerf + 'ms'
            });

            fmEl = YAHOO.lang.substitute(this.template, {
                name: this.msg('label.stats.freemarker.executed.in'),
                value: fmPerf + 'ms'
            });

            codeEl = YAHOO.lang.substitute(this.template, {
                name: this.msg('label.stats.serverCode.executed.in'),
                value: serverCodePerf + 'ms'
            });

            networkEl = YAHOO.lang.substitute(this.template, {
                name: this.msg('label.stats.network.executed.in'),
                value: networkPerf + 'ms'
            });

            this.widgets.statsModule.setBody(overallEl + scriptEl + fmEl + codeEl + networkEl);
        },

        printDumpInfos: function JavaScriptConsole_printDumpInfos(dumpOutput)
        {
            var _this, formatterDispatcher, myColumnDefs, ds, responseFields, rows, key, columns, i, j, dump, rowId, prop, row, aspects, tags, dt, tt, showTimer, hideTimer, refreshButton;
            _this = this;

            formatterDispatcher = function (elCell, oRecord, oColumn, oData)
            {
                elCell.innerHTML = oData;
            };

            myColumnDefs = [{
                key: 'Rows',
                label: 'Data',
                className: 'th',
                resizeable: true,
                minWidth: 150
            }];
            ds = new YAHOO.util.DataSource();

            responseFields = ['Rows'];
            rows = {};

            for (i = 0; i < dumpOutput.length; i++) {
                dump = JSON.parse(typeof dumpOutput[i] !== 'object' ? dumpOutput[i] : dumpOutput[i].json);

                rowId = i + ' ' + dump.properties['cm:name'] + ' ('+ dump.nodeRef + ')';
                myColumnDefs.push({
                    key: rowId,
                    label: dump.properties['cm:name'] || dump.nodeRef,
                    resizeable: true,
                    minWidth: 200,
                    formatter: formatterDispatcher,
                    editor: new YAHOO.widget.BaseCellEditor()
                });
                responseFields.push(rowId);

                for (prop in dump.properties)
                {
                    if (dump.properties.hasOwnProperty(prop))
                    {
                        row = rows[prop];
                        if (!row)
                        {
                            rows[prop] = row = {
                                Rows: prop
                            };
                        }
                        row[rowId] = dump.properties[prop];
                    }
                }
                delete dump.properties;

                aspects = dump.aspects.join(',<br/>');
                row = rows.aspects;
                if (!row)
                {
                    rows.aspects = row = {
                        Rows: 'aspects'
                    };
                }
                row[rowId] = aspects;
                delete dump.aspects;

                tags = dump.tags.join(',<br/>');
                row = rows.tags;
                if (!row)
                {
                    rows.tags = row = {
                        Rows: 'tags'
                    };
                }
                row[rowId] = tags;
                delete dump.tags;

                for (j = 0; j < dump.permissions.length; j++)
                {
                    permission = dump.permissions[j];
                    row = rows['permission ' + j];
                    if (!row)
                    {
                        rows['permission ' + j] = row = {
                            Rows: 'permission ' + j
                        };
                    }
                    row[rowId] = permission.authority + '(' + permission.authorityType + ') ' + permission.accessStatus +
                        ' ' + permission.permission + ' (directly:' + permission.directly + ')';
                }
                delete dump.permissions;

                for (prop in dump)
                {
                    if (dump.hasOwnProperty(prop))
                    {
                        row = rows[prop];
                        if (!row)
                        {
                            rows[prop] = row = {
                                Rows: prop
                            };
                        }
                        row[rowId] = dump[prop];
                    }
                }
            }

            columns = [];

            // add the data to the datasource for the table
            for (key in rows)
            {
                if (rows.hasOwnProperty(key))
                {
                    columns.push(key);
                }
            }
            columns.sort();

            for (i = 0; i < columns.length; i++)
            {
                ds.liveData.push(rows[columns[i]]);
            }

            ds.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            ds.responseSchema = {
                fields: responseFields
            };

            // this is the filter function
            ds.doBeforeCallback = function (req, raw, res, cb)
            {
                var data, filtered, i, l, row, prop, value;
                
                data = res.results || [];
                filtered = [];

                if (req)
                {
                    req = req.toLowerCase();
                    for (i = 0, l = data.length; i < l; ++i)
                    {
                        row = data[i];
                        for (prop in row)
                        {
                            if (row.hasOwnProperty(prop))
                            {
                                value = row[prop];
                                if (value)
                                {
                                    value = String(value);
                                    if (value.toLowerCase().indexOf(req) > -1 && !Alfresco.util.arrayContains(filtered, data[i]))
                                    {
                                        filtered.push(data[i]);
                                    }
                                }
                            }
                        }
                    }
                    res.results = filtered;
                }

                return res;
            };


            dt = new YAHOO.widget.ScrollingDataTable(this.id + '-dump', myColumnDefs, ds, {
                draggableColumns: true,
                width: '100%',
                height: '1350px'
            });

            filterTimeout = null;
            updateFilter = function ()
            {
                // Reset timeout
                filterTimeout = null;

                // Reset sort
                state = dt.getState();

                // Get filtered data
                ds.sendRequest(YAHOO.util.Dom.get('js-filter').value, {
                    success: dt.onDataReturnInitializeTable,
                    failure: dt.onDataReturnInitializeTable,
                    scope: dt,
                    argument: state
                });
            };

            YAHOO.util.Event.on('js-filter','keyup',function (e) {
                clearTimeout(filterTimeout);
                setTimeout(updateFilter,600);
            });

            if (YAHOO.util.Dom.get('js-filter').value)
            {
                updateFilter();
            }

            // tooltip support for
            tt = new YAHOO.widget.Tooltip('myTooltip');

            dt.on('cellMouseoverEvent', function (oArgs)
            {
                var target, column, record, propertyName, description, xy;

                if (showTimer)
                {
                    window.clearTimeout(showTimer);
                    showTimer = 0;
                }

                target = oArgs.target;
                column = this.getColumn(target);
                if (column.key == 'Rows')
                {
                    record = this.getRecord(target);
                    propertyName = CodeMirror.tern.getDef()[1].Properties.prototype[record._oData.Rows];
                    if (propertyName)
                    {
                        description = propertyName['!doc'];
                        xy = [parseInt(oArgs.event.pageX, 10) + 10, parseInt(oArgs.event.pageY, 10) - 10 ];
                        console.log(xy);
                        showTimer = window.setTimeout(function()
                        {
                            tt.setBody(description.replace(/\n/g , '<br/>'));
                            tt.cfg.setProperty('xy',xy);
                            tt.show();
                            hideTimer = window.setTimeout(function()
                            {
                                tt.hide();
                            },5000);
                        },500);
                    }
                }
            });

            dt.on('cellMouseoutEvent', function (oArgs)
            {
                if (showTimer)
                {
                    window.clearTimeout(showTimer);
                    showTimer = 0;
                }
                if (hideTimer)
                {
                    window.clearTimeout(hideTimer);
                    hideTimer = 0;
                }
                tt.hide();
            });

            refreshButton = function()
            {
                if (Dom.inDocument('nowhere'))
                {
                    menu.render();
                }
                else
                {
                    menu.render(document.body);
                }
                _this.widgets.showColumns.set('disabled', menu.getItems().length === 0);
                dt.render();
            };

            if (this.widgets.showColumns)
            {
                this.widgets.showColumns.destroy();
            }

            menu = new YAHOO.widget.Menu('nowhere');

            dt.on('headerCellClickEvent', function(ev)
            {
                var column;

                column = this.getColumn(ev.target);
                this.hideColumn(column);
                menu.addItem({
                    text: column.label || column.key,
                    value: column.key
                });
                refreshButton();
            });

            this.widgets.showColumns = new YAHOO.widget.Button({
                type: 'split',
                label: 'Dump Selection',
                name: 'showColumnsButton',
                menu: menu,
                container: 'splitButtonContainer',
                disabled: true
            });

            this.widgets.showColumns.on('click', function ()
            {
                var m, i;

                m = menu.getItems();
                for (i = 0; i < m.length; i++)
                {
                    dt.showColumn(dt.getColumn(m[i].value));
                }
                menu.clearContent();
                refreshButton();
            });

            this.widgets.showColumns.on('appendTo', function ()
            {
                var menu, oMenuItem;

                menu = this.getMenu();
                menu.subscribe('click', function onMenuClick(sType, oArgs)
                {
                    oMenuItem = oArgs[1];

                    if (oMenuItem)
                    {
                        dt.showColumn(dt.getColumn(oMenuItem.value));
                        menu.removeItem(oMenuItem.index);
                        refreshButton();
                    }
                });
            });

            // post render event
            dt.on('postRenderEvent', function()
            {
                var RS, len, oRec, data, i, prop, value, lastValue;

                RS = this.getRecordSet();
                len = RS.getLength();
                oRec = null;
                data = null;

                // iterate over all rows in the datatable
                for (i = 0; i < len; i++)
                {
                    oRec = RS.getRecord(i);
                    data = oRec._oData;

                    // inspect all values of a row
                    lastValue = null;
                    for (prop in data)
                    {
                        if (data.hasOwnProperty(prop) && prop !== 'Rows' && !dt.getColumn(prop).hidden)
                        {
                            value = data[prop];
                            if (lastValue === null || value === null || lastValue === value)
                            {
                                lastValue = value;
                            }
                            else
                            {
                                YAHOO.util.Dom.addClass(this.getTrEl(oRec), 'different-colors');
                                break;
                            }
                        }

                        YAHOO.util.Dom.removeClass(this.getTrEl(oRec), 'different-colors');
                    }
                }
            });
        },

        loadDemoScript: function JavaScriptConsole_loadDemoScript()
        {
            this.widgets.codeMirrorScript.setValue(
                'var nodes = search.query({\n'+
                '    language: \'fts-alfresco\',' +
                '    query: \'cm:name:alfresco\'' +
                '});\n'+
                '\n'+
                'for (var idx = 0; idx < nodes.length; idx++) {\n'+
                '    var node = nodes[idx];\n'+
                '    logger.log(node.name + \' (\' + node.typeShort + \'): \' + node.nodeRef);\n'+
                '}\n');
        },

        /**
         * Fired when the user selects a script from the load scripts drop down
         * menu. Calls a repository webscript to retrieve the script contents.
         *
         * @method onLoadScriptClick
         */
        onLoadScriptClick: function JavaScriptConsole_onLoadScriptClick(p_sType, p_aArgs, self)
        {
            var callback, callbackFreemarker, nodeRef;

            callback = {
                success: function(o)
                {
                    // set the new editor content
                    this.widgets.codeMirrorScript.setValue(o.responseText);
                },
                failure: function(o)
                {
                    Alfresco.util.PopupManager.displayMessage({
                        text: this.msg('error.script.load', o.status + ':' + o.statusText)
                    });
                },
                scope: self
            };

            callbackFreemarker = {
                success: function(o)
                {
                    // set the new editor content
                    this.widgets.codeMirrorTemplate.setValue(o.responseText);
                },
                failure: function(o)
                {
                    // can fail as stored freemarker code is optional
                    this.widgets.codeMirrorTemplate.setValue('');
                },
                scope: self
            };

            nodeRef = p_aArgs[1].value;

            if (nodeRef === 'NEW')
            {
                self.loadDemoScript();
            }
            else
            {
                YAHOO.util.Connect.asyncRequest('GET', Alfresco.constants.PROXY_URI + 'api/node/content/' + nodeRef.replace('://', '/'), callback);
                YAHOO.util.Connect.asyncRequest('GET', Alfresco.constants.PROXY_URI + 'api/node/content;jsc:freemarkerScript/' + nodeRef.replace('://', '/'), callbackFreemarker);
            }
        },

        /**
         * Fired when the user selects a theme from the theme drop down menu.
         * Changes the theme of all code mirror editors.
         *
         * @method onThemeSelection
         */
        onThemeSelection: function JavaScriptConsole_onThemeSelection(p_sType, p_aArgs, self)
        {
            var theme = p_aArgs[1].value;
            self.widgets.codeMirrorScript.setOption('theme', theme);
            self.widgets.codeMirrorTemplate.setOption('theme', theme);
        },

        saveAsExistingScript: function JavaScriptConsole_saveAsExistingScript(filename, nodeRef)
        {
            Alfresco.util.Ajax.jsonPut({
                url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/savescript.json?nodeRef=' + encodeURIComponent(nodeRef),
                dataObj: {
                    jsScript: this.widgets.codeMirrorScript.getValue(),
                    fmScript: this.widgets.codeMirrorTemplate.getValue()
                },
                successCallback: {
                    fn: function(res)
                    {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg('message.save.successful', filename)
                        });
                        this.createOrUpdateScriptsSaveMenu(res.json.scripts);
                        this.createOrUpdateScriptsLoadMenu(res.json.scripts);
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function(res)
                    {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: this.msg('message.failure'),
                            text: res.json && res.json.message ? res.json.message : this.msg('error.script.save', filename),
                            zIndex: 10 // added to internal default - high enough offset to ensure it should overlay any part of editor
                        });
                        
                    },
                    scope: this
                }
            });
        },

        /**
         *
         * @param filename either file name or path relative from scripts folder
         */
        saveAsNewScript: function JavaScriptConsole_saveAsNewScript(filename)
        {
            Alfresco.util.Ajax.jsonPut({
                url: Alfresco.constants.PROXY_URI + 'ootbee/jsconsole/savescript.json?namePath=' + encodeURIComponent(filename),
                dataObj: {
                    jsScript: this.widgets.codeMirrorScript.getValue(),
                    fmScript: this.widgets.codeMirrorTemplate.getValue()
                },
                successCallback: {
                    fn: function(res)
                    {
                        Alfresco.util.PopupManager.displayMessage({
                            text: this.msg('message.save.successful', filename)
                        });
                        this.createOrUpdateScriptsSaveMenu(res.json.scripts);
                        this.createOrUpdateScriptsLoadMenu(res.json.scripts);
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function(res)
                    {
                        Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: this.msg('message.failure'),
                            text: res.json && res.json.message ? res.json.message : this.msg('error.script.save', filename),
                            zIndex: 10 // added to internal default - high enough offset to ensure it should overlay any part of editor
                        });
                        
                    },
                    scope: this
                }
            });
        },

        /**
         * Fired when the user selects a script from the save scripts drop down
         * menu. Calls a repository webscript to store the script contents.
         *
         * @method onSaveScriptClick
         */
        onSaveScriptClick: function JavaScriptConsole_onSaveScriptClick(p_sType, p_aArgs, self)
        {
            var menuItem, filename, nodeRef;

            self.widgets.codeMirrorScript.save();

            menuItem = p_aArgs[1];
            filename = menuItem.cfg.getProperty('text');
            nodeRef = menuItem.value;

            if (nodeRef == 'NEW')
            {
                Alfresco.util.PopupManager.getUserInput({
                    title: self.msg('title.save.choose.filename'),
                    text: self.msg('message.save.choose.filename'),
                    input: 'text',
                    callback: {
                        fn: self.saveAsNewScript,
                        scope: self
                    }
                });
            }
            else
            {
                Alfresco.util.PopupManager.displayPrompt({
                    title: self.msg('title.confirm.save'),
                    text: self.msg('message.confirm.save', filename),
                    buttons: [{
                        text: self.msg('button.save'),
                        handler: function JavaScriptConsole_onSaveScriptClick_save()
                        {
                            this.destroy();
                            self.saveAsExistingScript(filename, nodeRef);
                        }
                    },
                    {
                        text: self.msg('button.cancel'),
                        handler: function JavaScriptConsole_onSaveScriptClick_cancel()
                        {
                            this.destroy();
                        },
                        isDefault: true
                    }],
                    zIndex: 10 // added to internal default - high enough offset to ensure it should overlay any part of editor
                });
            }
        },

        /**
         * Dialog select destination button event handler
         *
         * @method onSelectDestinationClick
         * @param e
         *            {object} DomEvent
         * @param p_obj
         *            {object} Object passed back from addListener method
         */
        onSelectDestinationClick: function JavaScriptConsole_onSelectDestinationClick(e, p_obj)
        {
            var pathNodeRef;

            // Set up select destination dialog
            if (!this.widgets.destinationDialog)
            {
                this.widgets.destinationDialog = new Alfresco.module.DoclibGlobalFolder(this.id + '-selectDestination');
                this.widgets.destinationDialog.setOptions({
                    allowedViewModes: [
                        Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY
                    ],
                    siteId: this.options.siteId,
                    containerId: this.options.containerId,
                    title: this.msg('title.destinationDialog'),
                    nodeRef: 'alfresco://company/home'
                });
            }

            // Make sure correct path is expanded
            pathNodeRef = this.widgets.nodeField.value;
            this.widgets.destinationDialog.setOptions({
                pathNodeRef: pathNodeRef ? new Alfresco.util.NodeRef(pathNodeRef) : null
            });

            // Show dialog
            this.widgets.destinationDialog.showDialog();
        },

        /**
         * Folder selected in destination dialog
         *
         * @method onDestinationSelected
         * @param layer
         *            {object} Event fired
         * @param args
         *            {array} Event parameters (depends on event type)
         */
        onDestinationSelected: function JavaScriptConsole_onDestinationSelected(layer, args)
        {
            var obj;

            // Check the event is directed towards this instance
            if ($hasEventInterest(this.widgets.destinationDialog, args))
            {
                obj = args[1];
                if (obj !== null)
                {
                    this.widgets.nodeField.value = obj.selectedFolder.nodeRef;
                    this.widgets.pathField.innerHTML = obj.selectedFolder.path;
                }
            }
        }

    });

})();
