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

import org.alfresco.repo.batch.BatchProcessor;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.rule.RuleService;
import org.apache.commons.logging.Log;
import org.mozilla.javascript.*;

import java.util.List;

/**
 * Container class for all worker implementations used by
 * {@link ScriptBatchExecuter}.
 *
 * @author Bulat Yaminov
 */
public class Workers
{

    /**
     * A de.jgoldhammer.alfresco.jscript.batch processor worker which can be canceled. When canceled it will skip any
     * processing requests.
     */
    public interface CancellableWorker<T> extends BatchProcessor.BatchProcessWorker<T>
    {
        /**
         * Notifies this worker to skip processing of any entries.
         * @return true if this worker was not canceled before.
         */
        boolean cancel();
    }

    private abstract static class BaseProcessWorker<T> extends BatchProcessor.BatchProcessWorkerAdaptor<T>
        implements CancellableWorker<T>
    {

        protected Scriptable scope;
        private String userName;
        private boolean disableRules;
        private RuleService ruleService;
        protected Log logger;
        private BaseScopableProcessorExtension scopable;
        private boolean canceled;

        protected Function processFunction;

        private BaseProcessWorker(Function processFunction, Scriptable scope,
                                  String userName, boolean disableRules,
                                  RuleService ruleService, Log logger,
                                  BaseScopableProcessorExtension scopable)
        {
            this.processFunction = processFunction;
            this.scope = scope;
            this.userName = userName;
            this.disableRules = disableRules;
            this.ruleService = ruleService;
            this.logger = logger;
            this.scopable = scopable;
        }

        @Override
        public void beforeProcess() throws Throwable
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("beforeProcess: entering context");
            }
            Context.enter();
            scopable.setScope(scope);
            AuthenticationUtil.setRunAsUser(userName);
            if (disableRules)
            {
                ruleService.disableRules();
            }
        }

        @Override
        public void afterProcess() throws Throwable
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("afterProcess: exiting context");
            }
            Context.exit();
            if (disableRules)
            {
                ruleService.enableRules();
            }
        }

        @Override
        public final void process(T entry) throws Throwable
        {
            if (!canceled)
            {
                doProcess(entry);
            }
        }

        public synchronized boolean cancel()
        {
            if (!canceled)
            {
                canceled = true;
                return true;
            }
            return false;
        }

        protected abstract void doProcess(T entry) throws Throwable;
    }

    public static class ProcessNodeWorker extends BaseProcessWorker<Object>
    {
        public ProcessNodeWorker(Function processFunction, Scriptable scope, String userName,
                                 boolean disableRules, RuleService ruleService, Log logger,
                                 BaseScopableProcessorExtension scopable)
        {
            super(processFunction, scope, userName, disableRules, ruleService, logger, scopable);
        }

        @Override
        protected void doProcess(Object entry) throws Throwable
        {
            Object result = processFunction.call(Context.getCurrentContext(),
                                                 scope, scope, new Object[] {entry});
            if (logger.isTraceEnabled())
            {
                logger.trace(String.format("call on %s %s", entry, result == null ? "skipped" : "done"));
            }
        }

        @Override
        public String getIdentifier(Object entry)
        {
            if (entry instanceof NativeJavaObject)
            {
                Object o = ((NativeJavaObject) entry).unwrap();
                if (o instanceof ScriptNode)
                {
                    return String.format("%s (%s)", ((ScriptNode) o).getName(), ((ScriptNode) o).getNodeRef());
                }
            }
            return super.getIdentifier(entry);
        }
    }

    public static class ProcessBatchWorker extends BaseProcessWorker<List<Object>>
    {
        public ProcessBatchWorker(Function processFunction, Scriptable scope, String userName,
                                  boolean disableRules, RuleService ruleService, Log logger,
                                  BaseScopableProcessorExtension scopable)
        {
            super(processFunction, scope, userName, disableRules, ruleService, logger, scopable);
        }

        @Override
        protected void doProcess(List<Object> entry) throws Throwable
        {
            Scriptable itemsArray = Context.getCurrentContext().newArray(scope, entry.toArray());
            Object resultArray = processFunction.call(Context.getCurrentContext(),
                                 scope, scope, new Object[] { itemsArray });
            if (logger.isTraceEnabled() && resultArray instanceof NativeArray)
            {
                logger.trace(String.format("call on function gave %d results out of %d",
                                           ((NativeArray) resultArray).getIds().length, entry.size()));
            }
        }
    }

}

