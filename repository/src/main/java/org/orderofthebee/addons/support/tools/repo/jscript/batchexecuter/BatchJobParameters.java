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
package org.orderofthebee.addons.support.tools.repo.jscript.batchexecuter;

import org.orderofthebee.addons.support.tools.repo.jscript.RhinoUtils;
import org.alfresco.repo.jscript.ScriptNode;
import org.apache.commons.lang3.RandomStringUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;
import java.util.Map;

/**
 * Bean describing a batch job being executed.
 *
 * @author Bulat Yaminov
 */
public abstract class BatchJobParameters
{

    private static final String PARAM_ITEMS = "items";
    private static final String PARAM_ROOT = "root";
    private static final String PARAM_BATCH_SIZE = "batchSize";
    private static final String PARAM_THREADS = "threads";
    private static final String PARAM_ON_NODE = "onNode";
    private static final String PARAM_ON_BATCH = "onBatch";
    private static final String PARAM_DISABLE_RULES = "disableRules";

    private static final int DEFAULT_BATCH_SIZE = 200;
    private static final int DEFAULT_THREADS = 4;

    private String id;
    private String name;
    private int threads;
    private int batchSize;
    private boolean disableRules;
    private String onNodeFunction;
    private String onBatchFunction;
    private Function onNode;
    private Function onBatch;

    private Status status;

    public enum Status
    {
        RUNNING, FINISHED, CANCELED
    }

    /** New instance can only be created using static factory methods */
    protected BatchJobParameters()
    {
    }

    /**
     * Parse JavaScript object with job parameters and return
     * a node-processing or de.jgoldhammer.alfresco.jscript.batch-processing job details.
     * Parameters must be for array processing.
     *
     * @param params JavaScript object with parameters.
     * @return Parsed job parameters object.
     * @throws IllegalArgumentException when parameters are incorrect.
     */
    public static ProcessArrayJobParameters parseArrayParameters(Object params) throws IllegalArgumentException
    {
        Map<String, Object> paramsMap = getParametersMap(params);
        final List<Object> items = RhinoUtils.getArray(paramsMap, PARAM_ITEMS);
        if (items == null)
        {
            throw new IllegalArgumentException(PARAM_ITEMS + " must be specified and be an array");
        }

        ProcessArrayJobParameters job = new ProcessArrayJobParameters();
        generateJobNameAndId(job, items.size() + "-items");
        job.setItems(items);

        parseCommonParameters(job, paramsMap);

        return job;
    }

    /**
     * Parse JavaScript object with job parameters and return
     * a node-processing or de.jgoldhammer.alfresco.jscript.batch-processing job details.
     * Parameters must be for a folder recursive processing.
     *
     * @param params JavaScript object with parameters.
     * @return Parsed job parameters object.
     * @throws IllegalArgumentException when parameters are incorrect.
     */
    public static ProcessFolderJobParameters parseFolderParameters(Object params) throws IllegalArgumentException
    {
        Map<String, Object> paramsMap = getParametersMap(params);
        final ScriptNode root = RhinoUtils.getScriptNode(paramsMap, PARAM_ROOT);
        if (root == null)
        {
            throw new IllegalArgumentException(PARAM_ROOT + " must be specified and be a node");
        }

        ProcessFolderJobParameters job = new ProcessFolderJobParameters();
        generateJobNameAndId(job, root.getName() + "-folder");
        job.setRoot(root);

        parseCommonParameters(job, paramsMap);

        return job;
    }

    private static void parseCommonParameters(BatchJobParameters job, Map<String, Object> paramsMap)
    {
        /* Parse common parameters */
        job.setBatchSize(RhinoUtils.getInteger(paramsMap, PARAM_BATCH_SIZE, DEFAULT_BATCH_SIZE));
        job.setThreads(RhinoUtils.getInteger(paramsMap, PARAM_THREADS, DEFAULT_THREADS));
        job.setDisableRules(RhinoUtils.getBoolean(paramsMap, PARAM_DISABLE_RULES, false));

        final Function onNode = RhinoUtils.getFunction(paramsMap, PARAM_ON_NODE);
        final Function onBatch = RhinoUtils.getFunction(paramsMap, PARAM_ON_BATCH);
        if (onNode == null && onBatch == null)
        {
            throw new IllegalArgumentException("one of " + PARAM_ON_NODE + " or " + PARAM_ON_BATCH +
                                               " function is required");
        }
        if (onNode != null && onBatch != null)
        {
            throw new IllegalArgumentException("only one of " + PARAM_ON_NODE + " or " + PARAM_ON_BATCH +
                                               " function can be specified");
        }

        job.setOnNode(onNode);
        job.setOnBatch(onBatch);
    }

    public static Map<String, Object> getParametersMap(Object params)
    {
        if (!(params instanceof ScriptableObject))
        {
            throw new IllegalArgumentException("first parameter must be an object but was: " + params);
        }
        return RhinoUtils.convertToMap((ScriptableObject) params);
    }

    private static void generateJobNameAndId(BatchJobParameters job, String shortTitle)
    {
        if (shortTitle == null)
            shortTitle = "";
        String random = RandomStringUtils.randomAlphabetic(20);
        job.setId(random);
        // Make name shorter for nicer logs
        job.setName(String.format("BatchExecuter_%s_%s",
                                  shortTitle.substring(0, Math.min(20, shortTitle.length())),
                                  random.substring(0, 4).toLowerCase()));
    }


    /* Getters and setters */

    public String getName()
    {
        return name;
    }

    public void setName(String id)
    {
        this.name = id;
    }

    public int getThreads()
    {
        return threads;
    }

    public void setThreads(int threads)
    {
        this.threads = threads;
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public boolean getDisableRules()
    {
        return disableRules;
    }

    public void setDisableRules(boolean disableRules)
    {
        this.disableRules = disableRules;
    }

    public String getOnNodeFunction()
    {
        return onNodeFunction;
    }

    public String getOnBatchFunction()
    {
        return onBatchFunction;
    }

    public void setOnNode(Function onNode)
    {
        this.onNode = onNode;
        if (onNode != null)
        {
            this.onNodeFunction = Context.getCurrentContext().decompileFunction(onNode, 2);
        }
    }

    public Function getOnNode()
    {
        return onNode;
    }

    public void setOnBatch(Function onBatch)
    {
        this.onBatch = onBatch;
        if (onBatch != null)
        {
            this.onBatchFunction = Context.getCurrentContext().decompileFunction(onBatch, 2);
        }
    }

    public Function getOnBatch()
    {
        return onBatch;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Status getStatus()
    {
        return status;
    }

    protected void setStatus(Status status)
    {
        this.status = status;
    }


    /* Subclasses */

    public static class ProcessArrayJobParameters extends BatchJobParameters
    {

        private List<Object> items;

        /** New instance can only be created using static factory methods */
        private ProcessArrayJobParameters()
        {
        }

        public void setItems(List<Object> items)
        {
            this.items = items;
        }

        public List<Object> getItems()
        {
            return items;
        }
    }

    public static class ProcessFolderJobParameters extends BatchJobParameters
    {

        private ScriptNode root;

        /** New instance can only be created using static factory methods */
        private ProcessFolderJobParameters()
        {
        }

        public void setRoot(ScriptNode root)
        {
            this.root = root;
        }

        public ScriptNode getRoot()
        {
            return root;
        }
    }
}
