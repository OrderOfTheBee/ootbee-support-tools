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
 *
 * Linked to Alfresco
 * Copyright (C) 2005 - 2025 Alfresco Software Limited.
 *
 * This file is part of code forked from the alfresco-jscript-extensions project
 * by Jens Goldhammer, which was licensed under the Apache License, Version 2.0.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jscript.jobs;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;

/**
 * Valid cron expressions for usage in the jobs service.
 *
 * @author jgoldhammer
 * @author Order of the Bee
 */
@ScriptClass(types = ScriptClassType.JavaScriptRootObject, code = "cronExpressions", help = "Useful cron expressions")
public class CronExpressions extends BaseScopableProcessorExtension
{

    public final String EVERY_TEN_SECONDS = "0/10 * * * * ?";
    public final String EVERY_TWENTY_SECONDS = "0/20 * * * * ?";

    public final String EVERY_MINUTE = "0 0/1 * 1/1 * ? *";
    public final String EVERY_TWO_MINUTES = "0 0/2 * 1/1 * ? *";
    public final String EVERY_FIVE_MINUTES = "0 0/5 * 1/1 * ? *";

    public final String EVERY_HOUR = "0 0 0/1 1/1 * ? *";
    public final String EVERY_TWO_HOURS = "0 0 0/2 1/1 * ? *";
    public final String EVERY_THREE_HOURS = "0 0 0/3 1/1 * ? *";
}
