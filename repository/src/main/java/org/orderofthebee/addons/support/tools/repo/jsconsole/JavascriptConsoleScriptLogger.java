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
package org.orderofthebee.addons.support.tools.repo.jsconsole;

import org.alfresco.repo.jscript.ScriptLogger;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptParameter;

/**
 * This class is based upon {@link ScriptLogger the default Repository-tier script logger} and its
 * {@link org.springframework.extensions.webscripts.ScriptLogger web-scripts clone} and has been modified to allow printing of messages to
 * the JavaScript Console result.
 * 
 * @author Kevin Roast
 * @author davidc
 * @author Florian Maul (fme AG)
 */
@ScriptClass(help = "Provides functions to aid debugging of scripts.", code = "logger.log(\"Command Processor: isEmailed=\" + isEmailed);", types = {
        ScriptClassType.JavaScriptRootObject })
public final class JavascriptConsoleScriptLogger
{

    // NOTE: keep compatibility with repository script logger
    private static final Logger logger = LoggerFactory.getLogger(ScriptLogger.class);

    private final SystemOut systemOut = new SystemOut();

    private final JavascriptConsoleScriptObject jsConsole;

    public JavascriptConsoleScriptLogger(JavascriptConsoleScriptObject jsConsole)
    {
        this.jsConsole = jsConsole;
    }

    @ScriptMethod(help = "Returns true if logging is enabled.", code = "var loggerStatus = logger.isLoggingEnabled();", output = "true if logging is enabled")
    public boolean isLoggingEnabled()
    {
        return logger.isDebugEnabled();
    }

    @ScriptMethod(help = "Logs a message")
    public void log(@ScriptParameter(help = "Message to log") String str)
    {
        logger.debug(str);
        jsConsole.print("DEBUG - " + str);
    }

    @ScriptMethod(help = "Returns true if debug logging is enabled.", code = "var loggerStatus = logger.isDebugEnabled();", output = "true if debug logging is enabled")
    public boolean isDebugLoggingEnabled()
    {
        return logger.isDebugEnabled();
    }

    @ScriptMethod(help = "Logs a debug message")
    public void debug(@ScriptParameter(help = "Message to log") String str)
    {
        logger.debug(str);
        jsConsole.print("DEBUG - " + str);
    }

    @ScriptMethod(help = "Returns true if info logging is enabled.", code = "var loggerStatus = logger.isInfoEnabled();", output = "true if info logging is enabled")
    public boolean isInfoLoggingEnabled()
    {
        return logger.isInfoEnabled();
    }

    @ScriptMethod(help = "Logs an info message")
    public void info(@ScriptParameter(help = "Message to log") String str)
    {
        logger.info(str);
        jsConsole.print(str);
    }

    @ScriptMethod(help = "Returns true if warn logging is enabled.", code = "var loggerStatus = logger.isWarnLoggingEnabled();", output = "true if warn logging is enabled")
    public boolean isWarnLoggingEnabled()
    {
        return logger.isWarnEnabled();
    }

    @ScriptMethod(help = "Logs a warning message")
    public void warn(@ScriptParameter(help = "Message to log") String str)
    {
        logger.warn(str);
        jsConsole.print("WARN - " + str);
    }

    @ScriptMethod(help = "Returns true if error logging is enabled.", code = "var loggerStatus = logger.isErrorLoggingEnabled();", output = "true if error logging is enabled")
    public boolean isErrorLoggingEnabled()
    {
        return logger.isErrorEnabled();
    }

    @ScriptMethod(help = "Logs an error message")
    public void error(@ScriptParameter(help = "Message to log") String str)
    {
        logger.error(str);
        jsConsole.print("ERROR - " + str);
    }

    public SystemOut getSystem()
    {
        return systemOut;
    }

    public class SystemOut
    {

        public void out(String str)
        {
            System.out.println(str);
            jsConsole.print(str);
        }
    }

    public void setLevel(String loggerName, String level)
    {
        org.apache.log4j.Logger underlyingLogger = org.apache.log4j.Logger.getLogger(loggerName);
        Level logLevel = Level.toLevel(level);
        underlyingLogger.setLevel(logLevel);
    }

    public String getLevel(String loggerName)
    {
        return org.apache.log4j.Logger.getLogger(loggerName).getLevel().toString();
    }
}
