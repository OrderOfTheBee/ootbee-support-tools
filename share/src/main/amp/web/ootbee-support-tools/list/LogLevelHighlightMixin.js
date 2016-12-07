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

/* global define: false */
define([ 'dojo/_base/declare', 'dojo/_base/lang' ], function ootbeeSupportTools_list_LogLevelHighlightMixin(declare, lang)
{
    return declare([ ], {
        
        cssRequirements: [{cssFile: './css/LogLevelHighlightMixin.css'}],
        
        logLevelPropertyKey : 'level',

        postMixInProperties : function ootbeeSupportTools_list_LogLevelHighlightMixin__postMixInProperties()
        {
            this.inherited(arguments);

            if (this.currentItem && lang.exists(this.logLevelPropertyKey, this.currentItem))
            {
                this.additionalCssClasses = this.additionalCssClasses || '';
                this.additionalCssClasses += ' ootbee-support-tools-list-LogLevelHighlightMixin logMessage-' + lang.getObject(this.logLevelPropertyKey, false, this.currentItem);
            }
        }

    });
});
