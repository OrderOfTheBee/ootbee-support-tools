<@markup id="widgets">
    <@processJsonModel group="share"/>
</@>

<@markup id="html">
    <#-- looks like an undocumented feature that Page picks up a div with id 'content' -->
    <div data-dojo-attach-point="containerNode" id="content"></div>
</@>