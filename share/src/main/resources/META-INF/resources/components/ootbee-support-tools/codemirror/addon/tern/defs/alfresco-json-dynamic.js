(function() {
	Alfresco.util.Ajax.jsonGet({
        url : Alfresco.constants.PROXY_URI + "ootbee/jsconsole/tern-definitions/alfresco-script-api",
        successCallback : {
            fn : function(response) {
            	if (response.json != undefined && response.json.typeDefinitions != undefined) {
            		CodeMirror.tern.addDef(response.json.typeDefinitions[0]);
            		CodeMirror.tern.addDef(response.json.typeDefinitions[1]);
            	}
            }
        },
        failureCallback : {
            fn : function(response) {
                alert("Can not load tern definitions");
            }
        }
    });

})();
