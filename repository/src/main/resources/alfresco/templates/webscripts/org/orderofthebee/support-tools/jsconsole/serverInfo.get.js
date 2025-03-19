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
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
 
var ctx, globalProperties, placeholderHelper, propertyGetter, nodeService, internalNodeService;

ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
globalProperties = ctx.getBean('global-properties', Packages.java.util.Properties);
placeholderHelper = new Packages.org.springframework.util.PropertyPlaceholderHelper('${', '}', ':', true);

propertyGetter = function(propertyName, defaultValue)
{
    var propertyValue;

    propertyValue = globalProperties[propertyName];
    if (propertyValue)
    {
        propertyValue = placeholderHelper.replacePlaceholders(propertyValue, globalProperties);
    }

    // native JS strings are always preferrable
    if (propertyValue !== undefined && propertyValue !== null)
    {
        propertyValue = String(propertyValue);
    }
    else if (defaultValue !== undefined)
    {
        propertyValue = defaultValue;
    }
    
    return propertyValue;
};

nodeService = ctx.getBean('NodeService', Packages.org.alfresco.service.cmr.repository.NodeService);
internalNodeService = ctx.getBean('nodeService', Packages.org.alfresco.service.cmr.repository.NodeService);

function prepareServerAndLicenseDetails()
{
    var localHost, licenseService;

    localHost = Packages.java.net.InetAddress.localHost;
    model.hostAddress = localHost.getHostAddress();
    model.hostName = localHost.getHostName();

    model.edition = server.edition;
    model.schema = server.schema;
    model.version = server.version;

    licenseService = ctx.getBean('licenseService', Packages.org.alfresco.service.license.LicenseService);
    model.licenseDaysLeft = licenseService.license ? licenseService.license.remainingDays : -1;
}

function prepareModules()
{
    var moduleService = ctx.getBean('ModuleService', Packages.org.alfresco.service.cmr.module.ModuleService);
    model.installedModuleCount = moduleService.allModules.size();
    model.missingModuleCount = moduleService.missingModules.size();
}

function prepareOSDetails()
{
    var osMXBean = Packages.java.lang.management.ManagementFactory.operatingSystemMXBean;

    model.osname = osMXBean.name;
    model.arch = osMXBean.arch;
    model.osversion = osMXBean.version;
    model.processorCount = osMXBean.availableProcessors;

    model.freeMemory = Math.floor(osMXBean.getFreePhysicalMemorySize() / (1024 * 1024));
    model.totalMemory = Math.floor(osMXBean.getTotalPhysicalMemorySize() / (1024 * 1024));
}

function prepareJVMDetails()
{
    var runtimeMXBean, system, dateFormat, uptime, days, memoryMXBean, heapUsage;

    runtimeMXBean = Packages.java.lang.management.ManagementFactory.runtimeMXBean;
    system = Packages.java.lang.System;

    model.java = runtimeMXBean.vmName + ' (Java version: ' + system.getProperty('java.version') + ', JVM version: ' + runtimeMXBean.vmVersion + ', ' + runtimeMXBean.name + ', vendor: ' + runtimeMXBean.vmVendor + ')';

    dateFormat = new Packages.java.text.SimpleDateFormat('HH\'h\':mm\'min\':ss\'sec\'');
    dateFormat.setTimeZone(Packages.java.util.TimeZone.getTimeZone('UTC'));
    uptime = runtimeMXBean.getUptime();
    days = Math.floor(uptime / (3600 * 1000 * 24));
    model.javaUptime = days + 'd:' + dateFormat.format(uptime);

    memoryMXBean = Packages.java.lang.management.ManagementFactory.memoryMXBean;
    heapUsage = memoryMXBean.heapMemoryUsage;
    model.javaHeapInit = Math.floor(heapUsage.init / (1024 * 1024));
    model.javaHeapMax = Math.floor(heapUsage.max / (1024 * 1024));
    model.javaHeapCommitted = Math.floor(heapUsage.comitted / (1024 * 1024));
    model.javaHeapUsed = Math.floor(heapUsage.used / (1024 * 1024));

    model.hostUserInfo = system.getProperty('user.name') + ' (' + system.getProperty('user.home') + ')';
}

function prepareThreadDetails()
{
    var threadMXBean = Packages.java.lang.management.ManagementFactory.threadMXBean;

    model.threadCount = threadMXBean.threadCount;
    model.deadlockedThreadCount = (threadMXBean.findDeadlockedThreads() || []).length;
}

function getSearchCount(query)
{
    var count, def;

    def = {
        query: query,
        store: 'workspace://SpacesStore',
        language: 'fts-alfresco',
        page: {
            skipCount : 0,
            limit : 0
        }
    };

    count = search.queryResultSet(def).meta.numberFound;
    return count;
}

function findCategoryRoot(categoryAspect)
{
    var nodes, idx, categories, categoryRoot;

    nodes = search.query({
        language: 'fts-alfresco',
        query: 'TYPE:"cm:category_root"'
    });

    for (idx = 0; idx < nodes.length; idx++)
    {
        categories = nodes[idx].childrenByXPath('./' + categoryAspect);
        if (categories)
        {
            categoryRoot = categories[0];
            break;
        }
    }

    return categoryRoot;
}

function countWorkflows()
{
    var workflowService, bpmEngineRegistry, engineIds, workflowCount, idx, query;

    workflowService = ctx.getBean('WorkflowService', Packages.org.alfresco.service.cmr.workflow.WorfklowService);
    bpmEngineRegistry = ctx.getBean('bpm_engineRegistry', Packages.org.alfresco.repo.workflow.BPMEngineRegistry);
    engineIds = bpmEngineRegistry.workflowComponents;

    workflowCount = 0;
    for (idx = 0; idx < engineIds.length; idx++)
    {
        query = new Packages.org.alfresco.service.cmr.workflow.WorkflowInstanceQuery();
        query.active = true;
        query.engineId = engineIds[idx];

        workflowCount += workflowService.countWorkflows(query);
    }
    return workflowCount;
}

function prepareJavaScriptDerivedCounts()
{
    var nodeCountsViaSOLR, tagsRoot;

    nodeCountsViaSOLR = propertyGetter('ootbee-support-tools.js-console.serverInfo.nodeCountsViaSOLR', 'true').toLowerCase() === 'true';

    model.groupId = search.selectNodes('/sys:system/sys:authorities')[0].nodeRef;
    // using internalNodeService because countChildAssocs is not handled as read-only operation by transaction AOP advice (Alfresco bug)
    model.groupsCount = internalNodeService.countChildAssocs(model.groupId, true);
    model.peopleId = search.selectNodes('/sys:system/sys:people')[0].nodeRef;
    model.peopleCount = internalNodeService.countChildAssocs(model.peopleId, true);

    tagsRoot = findCategoryRoot('cm:taggable');
    model.tagsCount = internalNodeService.countChildAssocs(tagsRoot.nodeRef, true);

    model.workflowDefinitionsCount = workflow.latestDefinitions.length;
    model.workflowAllDefinitionsCount = workflow.allDefinitions.length;
    model.workflowCount = countWorkflows();

    try
    {
        model.sitesCount = getSearchCount(nodeCountsViaSOLR ? 'TYPE:"st:site" AND ISNODE:T' : 'TYPE:"st:site"');
        model.folderCount = getSearchCount(nodeCountsViaSOLR ? 'TYPE:"cm:folder" AND ISNODE:T' : 'TYPE:"cm:folder"');
        model.docsCount = getSearchCount(nodeCountsViaSOLR ? 'TYPE:"cm:content" AND ISNODE:T' : 'TYPE:"cm:content"');
        model.checkedOutCount = getSearchCount(nodeCountsViaSOLR ? 'ASPECT:"cm:checkedOut" AND ISNODE:T' : 'ASPECT:"cm:checkedOut"');
    }
    catch(e)
    {
        model.sitesCount = -1;
        model.folderCount = -1;
        model.docsCount = -1;
        model.checkedOutCount = -1;
    }

    model.classificationCount= classification.allClassificationAspects.length;
    model.runningActionCount = actionTrackingService.allExecutingActions.length;
}

function prepareJavaDerivedCounts()
{
    var scheduler, scheduledActions, policyComponent, nodeDAO, tenantDAO, patchService;

    scheduler = ctx.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    model.runningJobCount = scheduler.currentlyExecutingJobs.size();

    scheduledActions = ctx.getBean('scheduledPersistedActionService', Packages.org.alfresco.service.cmr.action.scheduled.ScheduledPersistedActionService);
    model.scheduledActionCount = scheduledActions.listSchedules().size();

    policyComponent = ctx.getBean('policyComponent', Packages.org.alfresco.repo.policy.PolicyComponent);
    model.registeredPolicyCount = policyComponent.getRegisteredPolicies().size();

    nodeDAO = ctx.getBean('nodeDAO', Packages.org.alfresco.repo.domain.node.NodeDAO);
    model.transactionsCount = nodeDAO.transactionCount;

    tenantDAO = ctx.getBean('tenantAdminDAO', Packages.org.alfresco.repo.domain.tenant.TenantAdminDAO);
    model.tenantCount = tenantDAO.listTenants(true).size();

    patchService = ctx.getBean('PatchService', Packages.org.alfresco.repo.admin.patch.PatchService);
    model.patchCount = patchService.getPatches(null,null).size();
}

prepareServerAndLicenseDetails();
prepareModules();

prepareOSDetails();
prepareJVMDetails();
prepareThreadDetails();

prepareJavaScriptDerivedCounts();
prepareJavaDerivedCounts();