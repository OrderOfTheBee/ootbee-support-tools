/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
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
 * Linked to Alfresco Copyright
 * (C) 2005-2016 Alfresco Software Limited.
 */

/* global Admin: false, el: false, moment: false */

/**
 * Admin Log Files
 */
var AdminLF = AdminLF || {};

/* Page load handler */
Admin.addEventListener(window, 'load', function()
{
    AdminLF.adaptTimes();
});

(function()
{
    var serviceContext;
    
    AdminLF.setServiceContext = function setServiceContext(context)
    {
        serviceContext = context;
    };
    
    AdminLF.adaptTimes = function adaptTimes()
    {
        var table, i, cell;

        table = el("log-files-table");
        
        // i starting at 1 to jump over the table header!
        for (i = 1; i < table.rows.length; i++)
        {
            cell = table.rows[i].cells[3];

            cell.title = moment().to(cell.innerHTML);
        }
    };
    
    AdminLF.deleteLogFile = function deleteLogFile(logFilePath, rowId)
    {
        var pathFragments, idx, realPath;

        pathFragments = logFilePath.split(/\//);
        for (idx = 0; idx < pathFragments.length; idx++)
        {
            pathFragments[idx] = encodeURIComponent(pathFragments[idx]).replace(/:/g, '%3A');
        }
        realPath = pathFragments.join('/');

        Admin.request({
            url : serviceContext + '/ootbee/admin/log4j-log-file/' + realPath,
            method : 'DELETE',
            fnSuccess : function deleteLogFile_success()
            {
                var checkBox, row;
                
                row = el(rowId);
                // just hide it - row deletion is kind of weird (compared to other DOM operations)
                Admin.toggleHiddenElement(row);
                
                checkBox = el(rowId + '-check');
                checkBox.parent.removeChild(checkBox);
            }
        });
    };

}());
