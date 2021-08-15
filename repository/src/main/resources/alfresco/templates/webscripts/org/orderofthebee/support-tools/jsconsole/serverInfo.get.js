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
 
var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();

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
    model.licenseDaysLeft = licenseService.license.remainingDays;
}

function prepareModules()
{
    var modules, modulesList, moduleCount, moduleDesc, idx, module;

    modules = ctx.getBean('ModuleService', Packages.org.alfresco.service.cmr.module.ModuleService);
    modulesList = modules.allModules;

    moduleCount = modulesList.size();
    moduleDesc = '';

    for (idx = 0; idx < moduleCount; idx++) {
        if (idx !== 0) {
            moduleDesc += ', ';
        }
        module = modulesList.get(idx);
        moduleDesc += module.id + ' v' + module.version;
    }

    model.modules = moduleCount + ' (' + moduleDesc + ')';
}

function prepareOSDetails()
{
    var osMXBean = Packages.java.lang.management.ManagementFactory.operatingSystemMXBean;

    model.osname = osMXBean.name;
    model.arch = osMXBean.arch;
    model.osversion = osMXBean.version;
    model.processorCount = osMXBean.availableProcessors;
    model.systemLoad = osMXBean.systemLoadAverage >= 0 ? osMXBean.systemLoadAverage : 'n/a';

    model.freeMemory = Math.floor(osMXBean.getFreePhysicalMemorySize() / (1024 * 1024),'.');
    model.totalMemory = Math.floor(osMXBean.getTotalPhysicalMemorySize() / (1024 * 1024),'.');
}

function prepareJVMDetails()
{
    var runtimeMXBean, system, idx, dateFormat, uptime;

    runtimeMXBean = Packages.java.lang.management.ManagementFactory.runtimeMXBean;
    system = Packages.java.lang.System;

    model.java = runtimeMXBean.vmName + ' (version: ' + system.getProperty('java.version') + '- ' + runtimeMXBean.vmVersion+' ,' + runtimeMXBean.name + ', vendor:' + runtimeMXBean.vmVendor;

    model.javaArgs = '';
    for (idx = 0; idx < runtimeMXBean.inputArguments.size(); idx++)
    {
        model.javaArgs += runtimeMXBean.inputArguments.get(idx) + '\n';
    }

    dateFormat = new Packages.java.text.SimpleDateFormat('HH\'h\':mm\'min\':ss\'sec\'');
    dateFormat.setTimeZone(Packages.java.util.TimeZone.getTimeZone('UTC'));
    uptime = runtimeMXBean.getUptime();
    uptime = uptime / (3600 * 1000 * 24);
    model.javaUptime = Math.floor(uptime) + 'd:' + dateFormat.format(uptime);

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

function prepareJavaScriptDerivedCounts()
{
    var definitions, idx, activeInstanceCount;
    // TODO: configuration property to disable / customise bulk-query based count retrieval

    model.sitesCount = siteService.listSites('', '').length;
    model.groupsCount = groups.getGroups('', utils.createPaging(100000, 0)).length;
    model.groupId = search.selectNodes('/sys:system/sys:authorities')[0].nodeRef;
    model.peopleCount = people.getPeople('', 100000).length;
    model.peopleId = search.selectNodes('/sys:system/sys:people')[0].nodeRef;

    try
    {
        model.tagsCount = taggingService.getTags('workspace://SpacesStore').length;
    }
    catch(e)
    {
        // TaggingService is index-dependant via CategoryService, so it may fail without an active index
        model.tagsCount = -1;
    }

    model.workflowDefinitions = workflow.latestDefinitions.length;
    model.workflowAllDefinitions = workflow.allDefinitions.length;
    
    definitions = workflow.allDefinitions;
    activeInstanceCount = 0;
    for (idx = 0; idx < definitions.length; idx++)
    {
        activeInstanceCount += definitions[idx].activeInstances.length;
    }
    model.workflowCount = activeInstanceCount;

    try
    {
        model.folderCount = getSearchCount('TYPE:"cm:folder"');
        model.docsCount = getSearchCount('TYPE:"cm:content"');
        model.checkedOutCount = getSearchCount('ASPECT:"cm:checkedOut"');
    }
    catch(e)
    {
        // the queries are simple enough to be executed via DB FTS, but that feature may not be available / enabled
        model.folderCount = -1;
        model.docsCount = -1;
        model.checkedOutCount = -1;
    }

    model.classifications= classification.allClassificationAspects.length;
    model.runningActions = actionTrackingService.allExecutingActions.length;
}

function prepareJavaDerivedCounts()
{
    var scheduler, jobCount, jobDesc, idx, scheduledActions, policyComponent, nodeDAO, tenantDAO, patchService;

    scheduler = ctx.getBean('schedulerFactory', Packages.org.quartz.Scheduler);
    jobCount = scheduler.currentlyExecutingJobs.size();
    jobDesc = '';

    for (idx = 0; idx < jobCount; idx++) {
        if (jobDesc.length > 0) {
            jobDesc += ', ';
        }
        jobDesc += scheduler.currentlyExecutingJobs.get(idx).trigger.fullName;
    }

    model.runningJobs = jobCount + ' (' + jobDesc + ')';

    scheduledActions = ctx.getBean('scheduledPersistedActionService', Packages.org.alfresco.service.cmr.action.scheduled.ScheduledPersistedActionService);
    model.scheduledActions = scheduledActions.listSchedules().size();

    policyComponent = ctx.getBean('policyComponent', Packages.org.alfresco.repo.policy.PolicyComponent);
    model.registeredPolicies = policyComponent.getRegisteredPolicies().size();

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