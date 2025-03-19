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

function addResetDashboardAction(actionsMenuGroup, services)
{
    if (actionsMenuGroup && actionsMenuGroup.config && actionsMenuGroup.config.widgets)
    {
        actionsMenuGroup.config.widgets.push({
            name : 'alfresco/menus/AlfMenuItem',
            config : {
                label : msg.get('button.site-reset-dashboard.label'),
                iconClass : 'alf-configure-icon',
                // not really a create but ALF_CRUD_CREATE uses POST
                publishTopic : 'ALF_CRUD_CREATE',
                publishGlobal : true,
                publishPayloadType : 'PROCESS',
                publishPayloadModifiers : [ 'processCurrentItemTokens' ],
                publishPayload : {
                    urlType : 'SHARE',
                    url : 'data/console/ootbee-support-tools/site/{shortName}/dashboard/reset',
                    // just to avoid an unnecessary warning
                    alfResponseTopic : String(Packages.java.util.UUID.randomUUID())
                }
            }
        });

        services.push('alfresco/services/CrudService');
    }
}

function findActionsMenuGroup(widgets)
{
    var actionsPopup, actionsMenuGroup;
    // target widget has no ID, we cannot select by label due to crappy widgetUtils implementation
    // here's hoping the page continues to use alfresco/menus/AlfMenuBarPopup only for actions
    actionsPopup = widgetUtils.findObject(widgets, 'name', 'alfresco/menus/AlfMenuBarPopup');
    if (actionsPopup && actionsPopup.config && String(actionsPopup.config.label) === String(msg.get('message.actions-header-label')))
    {
        if (actionsPopup.config.widgets && actionsPopup.config.widgets.length === 1
                && actionsPopup.config.widgets[0].name === 'alfresco/menus/AlfMenuGroup')
        {
            actionsMenuGroup = actionsPopup.config.widgets[0];
        }
    }

    return actionsMenuGroup;
}

addResetDashboardAction(findActionsMenuGroup(model.jsonModel.widgets), model.jsonModel.services);
