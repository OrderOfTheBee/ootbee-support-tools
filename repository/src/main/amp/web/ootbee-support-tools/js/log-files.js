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
    AdminLF.adaptTimes = function()
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

}());
