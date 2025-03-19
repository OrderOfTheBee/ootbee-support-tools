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
define([ 'dojo/_base/declare', 'alfresco/menus/AlfSelectedItemsMenuItem', 'dojo/_base/lang', 'dojo/_base/array' ],
        function ootbeeSupportTools_menu_SelectedItemsMenuItem(declare, AlfSelectedItemsMenuItem, lang, array)
        {
            return declare([ AlfSelectedItemsMenuItem ], {

                itemKeyProperty : null,

                onItemsSelected : function ootbeeSupportTools_menu_SelectedItemsMenuItem__onItemsSelected(payload)
                {
                    var selectedItemKeys;
                    // base module is annoying in that it always directly modifies the publishPayload instead of allowing
                    // PublishPayloadMixin + configuration handle it
                    if (payload.selectedItems)
                    {
                        this.currentItem = {
                            selectedItems : payload.selectedItems
                        };

                        if (this.itemKeyProperty)
                        {
                            selectedItemKeys = [];
                            array.forEach(payload.selectedItems,
                                    function ootbeeSupportTools_menu_SelectedItemsMenuItem__onItemsSelected_collectItemKey(item)
                                    {
                                        selectedItemKeys.push(lang.getObject(this.itemKeyProperty, false, item));
                                    }, this);
                            this.currentItem.selectedItemKeys = selectedItemKeys;
                        }

                    }
                }
            });
        });
