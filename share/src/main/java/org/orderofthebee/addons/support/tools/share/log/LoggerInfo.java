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
 */
package org.orderofthebee.addons.support.tools.share.log;

import java.util.ArrayList;
import java.util.List;

/**
 * Instances of this class abstract the essential information about a logger in either log4j implementation version.
 *
 * @author Axel Faust
 */
public class LoggerInfo
{

    private final String name;

    private final boolean root;

    private final String parentName;

    private final boolean parentRoot;

    private final String level;

    private final String effectiveLevel;

    private final boolean additivity;

    private final List<String> appenderNames = new ArrayList<>();

    /**
     * Constructs a new instance of this class.
     *
     * @param name
     *     the name of the logger
     * @param root
     *     {@code true} if this logger is the root logger, {@code false} otherwise
     * @param parentName
     *     the name of the parent to this logger, or {@code null} if it has no parent
     * @param parentRoot
     *     {@code true} if the parent to this logger is the root logger, {@code false} otherwise (including if it has no parent)
     * @param level
     *     the configured level for this logger
     * @param effectiveLevel
     *     the effective level for this logger
     * @param additivity
     *     {@code true} whether the additivity flag for this logger is set, {@code false} otherwise
     */
    public LoggerInfo(final String name, final boolean root, final String parentName, final boolean parentRoot, final String level,
            final String effectiveLevel, final boolean additivity)
    {
        super();
        this.name = name;
        this.root = root;
        this.parentName = parentName;
        this.parentRoot = parentRoot;
        this.level = level;
        this.effectiveLevel = effectiveLevel;
        this.additivity = additivity;
    }

    /**
     * Adds the name of an appender to this instance.
     *
     * @param appenderName
     *     the name of the appender associated with the logger
     */
    public void addAppenderName(final String appenderName)
    {
        this.appenderNames.add(appenderName);
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the root
     */
    public boolean isRoot()
    {
        return this.root;
    }

    /**
     * @return the parentName
     */
    public String getParentName()
    {
        return this.parentName;
    }

    /**
     * @return the parentRoot
     */
    public boolean isParentRoot()
    {
        return this.parentRoot;
    }

    /**
     * @return the level
     */
    public String getLevel()
    {
        return this.level;
    }

    /**
     * @return the effectiveLevel
     */
    public String getEffectiveLevel()
    {
        return this.effectiveLevel;
    }

    /**
     * @return the additivity
     */
    public boolean isAdditivity()
    {
        return this.additivity;
    }

    /**
     * @return the appenderNames
     */
    public List<String> getAppenderNames()
    {
        return new ArrayList<>(this.appenderNames);
    }

}
