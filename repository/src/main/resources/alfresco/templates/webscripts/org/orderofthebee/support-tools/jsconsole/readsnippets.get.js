/**
 * Copyright (C) 2016 - 2021 Order of the Bee
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
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
 * 
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */

var prepareOutput= function prepareOutput(folder) {
  var scriptlist = [];

  var children = folder.children;
  
  for (c in children) {
    var node = children[c];
    if(node.isDocument && node.mimetype='application/json'){
        scriptlist.push(node.content);        
    }
  }
  
  return scriptlist;
}

var findAvailableSnippets= function findAvailableScripts(){
    var snippetsFolder = search.selectNodes("/app:company_home/app:dictionary/cm:jsconsole/cm:snippets")[0];
    if (snippetsFolder) {
    	model.scripts = jsonUtils.toJSONString(prepareOutput(snippetsFolder));
    }
    else {
    	model.scripts = "[]";
    }
}

findAvailableScripts();
