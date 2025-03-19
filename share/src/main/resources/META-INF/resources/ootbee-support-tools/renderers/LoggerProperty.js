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

/* global define: false */
define([ 'dojo/_base/declare', 'alfresco/renderers/Property', 'dojo/_base/lang', 'alfresco/core/ObjectTypeUtils', 'dojox/html/entities',
        'dojo/dom-attr' ], function ootbeeSupportTools_renderers_LoggerProperty(declare, Property, lang, ObjectTypeUtils, htmlEntities,
        domAttr)
{
    return declare([ Property ], {

        propertyToRender : 'loggerCompressedName',

        propertyToRenderTitle : 'loggerName',

        renderedTitle : null,

        postMixInProperties : function ootbeeSupportTools_renderers_LoggerProperty__postMixInProperties()
        {
            this.inherited(arguments);

            if (ObjectTypeUtils.isString(this.propertyToRenderTitle) && ObjectTypeUtils.isObject(this.currentItem)
                    && lang.exists(this.propertyToRenderTitle, this.currentItem))
            {
                this.originalRenderedTitle = this.getRenderedProperty(lang.getObject(this.propertyToRenderTitle, false, this.currentItem),
                        false);
                this.renderedTitle = this.mapValueToDisplayValue(this.originalRenderedTitle);

                // unfortunately getRenderedProperty already applies HTML encoding which it shouldn't
                // TODO Report enhancement - consolidate HTML encoding into generateRendering or via HTML template token subsitution
                this.renderedTitle = htmlEntities.decode(this.renderedTitle);
            }
        },

        postCreate : function ootbeeSupportTools_renderers_LoggerProperty__postCreate()
        {
            this.inherited(arguments);

            if (ObjectTypeUtils.isString(this.renderedTitle))
            {
                domAttr.set(this.renderedValueNode, 'title', this.renderedTitle);
            }
        }

    });
});
