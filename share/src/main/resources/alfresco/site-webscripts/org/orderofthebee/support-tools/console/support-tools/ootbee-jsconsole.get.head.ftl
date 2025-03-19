<#--
Copyright (C) 2016 - 2025 Order of the Bee

This file is part of OOTBee Support Tools

OOTBee Support Tools is free software: you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

OOTBee Support Tools is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with OOTBee Support Tools. If not, see
<http://www.gnu.org/licenses/>.

Linked to Alfresco
Copyright (C) 2005 - 2025 Alfresco Software Limited.
 
This file is part of code forked from the JavaScript Console project
which was licensed under the Apache License, Version 2.0 at the time.
In accordance with that license, the modifications / derivative work
is now being licensed under the LGPL as part of the OOTBee Support Tools
addon.

 -->
<#include "/org/alfresco/components/component.head.inc">
<!-- Admin Console Javascript Console -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/jsconsole.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/console/consoletool.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/jsconsole.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/modules/documentlibrary/global-folder.css" />
<@script type="text/javascript" src="${page.url.context}/res/modules/documentlibrary/global-folder.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/common/common-component-style-filter-chain.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/beautify.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/yui/resize/resize.js"></@script>

<link href="https://fonts.googleapis.com/css?family=Source+Code+Pro:300" rel="stylesheet" type="text/css"/>

<!-------------------------->
<!-- Codemirror-->
<!-------------------------->
<script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/lib/codemirror.js"></script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/lib/codemirror.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/default.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/keymap/extra.js"></@script>

<!-------------------------->
<!-- Codemirror Addons-->
<!-------------------------->

<!-- display addons -->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/display/placeholder.js"></@script>

<!-- edit addons -->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/edit/trailingspace.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/edit/matchbrackets.js"></@script>

<!-- hint addons-->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/show-hint.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/show-hint-eclipse.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/show-hint.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/show-context-info.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/show-context-info.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/templates-hint.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/templates-hint.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/javascript/javascript-templates.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hint/javascript/alfresco-templates.js"></@script>

<!-- hover addons-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hover/text-hover.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hover/text-hover.css" />

<!-- hyperlink addons-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hyperlink/hyperlink.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/hyperlink/hyperlink.css" />

<!-- lint addons-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/jsonlint/jsonlint.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/jsonlint/json-formatter.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/lint/lint.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/lint/lint.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/lint/json-lint.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/lint/javascript-lint.js"></@script>

<!-- search addons-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/match-highlighter.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/match-highlighter.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/searchcursor.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/search.js"></@script>
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/matchesonscrollbar.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/search/matchesonscrollbar.js"></@script>

<!-- selection addons-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/selection/active-line.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/selection/mark-selection.js"></@script>

<!-- mode-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/mode/javascript/javascript.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/mode/htmlmixed/htmlmixed.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/mode/freemarker/freemarkercolors.css" />

 <!-- Acorn / Tern JS -->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/acorn/acorn.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/acorn/acorn_loose.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/acorn/util/walk.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/tern/lib/signal.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/tern/lib/tern.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/tern/lib/def.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/tern/lib/comment.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/ternjs/tern/lib/infer.js"></@script>

<!-- CodeMirror addon with Tern JS -->
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern.js"></@script>

<!-- codemirror extension for ternjs by angelo zerr-->
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern-extension.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern-extension.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern-hover.js"></@script>
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/tern-hyperlink.js"></@script>

<!-- tern definitions for ecma5 -->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/defs/ecma5.json.js"></@script>

<!-- dynamic tern definitions for alfresco -->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror/addon/tern/defs/alfresco-json-dynamic.js"></@script>

<!-- codemirror ui-->
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror-ui/js/codemirror-ui.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror-ui/css/codemirror-ui.css" />
<@script type="text/javascript" src="${page.url.context}/res/components/ootbee-support-tools/codemirror-ui/js/codemirror-ui-find.js"></@script>
<@link rel="stylesheet" media="screen" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror-ui/css/codemirror-ui-find.css" />


<!-- codemirror themes -->
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/neat.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/elegant.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/erlang-dark.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/night.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/monokai.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/cobalt.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/eclipse.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/rubyblue.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/lesser-dark.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/xq-dark.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/ambiance.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/blackboard.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/vibrant-ink.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/solarized.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/twilight.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/zenburn.css" />
<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/ootbee-support-tools/codemirror/theme/neo.css" />
