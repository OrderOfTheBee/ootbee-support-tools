<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">
<import resource="classpath:alfresco/templates/webscripts/org/orderofthebee/support-tools/admin/ootbee-support-tools/rendition-service2-transform.lib.js">
/**
 * Copyright (C) 2016 - 2021 Order of the Bee
 *
 * This file is part of OOTBee Support Tools
 *
 * OOTBee Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OOTBee Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OOTBee Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005 - 2021 Alfresco Software Limited.
 */
 
// operation should be reflected
// name prefix since .lib.js might be imported together with contentService related file
// (some operations might overlap)
var operation = 'renditionService2' + url.templateArgs.operation;
if (operation in this)
{
    this[operation]();
}
else
{
    status.setCode(404, 'Operation not found');
}