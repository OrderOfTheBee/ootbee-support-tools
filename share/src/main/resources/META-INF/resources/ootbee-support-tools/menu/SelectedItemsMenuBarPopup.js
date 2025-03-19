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
define([ 'dojo/_base/declare', 'alfresco/documentlibrary/AlfSelectedItemsMenuBarPopup', 'dojo/_base/lang', 'dojo/_base/array' ],
        function ootbeeSupportTools_menu_SelectedItemsMenuBarPopup(declare, AlfSelectedItemsMenuBarPopup, lang, array)
        {
            return declare([ AlfSelectedItemsMenuBarPopup ], {

                onSelectedDocumentsAction : function ootbeeSupportTools_menu_SelectedItemsMenuBarPopup__onSelectedDocumentsAction(payload)
                {
                    var selectedItemKeys, clonedPayload;
                    
                    selectedItemKeys = [];
                    array.forEach(this.selectedItems,
                            function ootbeeSupportTools_menu_SelectedItemsMenuBarPopup__onSelectedDocumentsAction_collectItemKey(item)
                            {
                                selectedItemKeys.push(lang.getObject(this.itemKeyProperty, false, item));
                            }, this);

                    this.currentItem = {
                        itemKeys : selectedItemKeys
                    };

                    clonedPayload = lang.clone(payload);
                    this.processObject([ 'processCurrentItemTokens' ], clonedPayload);

                    this.getInherited(arguments).call(this, clonedPayload);
                }
            });
        });
