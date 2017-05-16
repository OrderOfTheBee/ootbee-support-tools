<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">

/**
 * Copyright (C) 2016 Axel Faust / Markus Joos
 * Copyright (C) 2016 Order of the Bee
 *
 * This file is part of Community Support Tools
 *
 * Community Support Tools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Community Support Tools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Community Support Tools. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * Linked to Alfresco
 * Copyright (C) 2005-2016 Alfresco Software Limited.
 */

var ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var solr4 =  ctxt.getBean('solr4',
Packages.org.alfresco.repo.search.impl.solr.SolrChildApplicationContextFactory);
var childCtx = solr4.getApplicationContext();

// SOLR Admin HTTP Client
var adminClient = childCtx.getBean('search.solrAdminHTTPCLient', Packages.org.alfresco.repo.search.impl.solr.SolrAdminHTTPClient);

// Args for SUMMARY report 
var args = new Packages.java.util.HashMap();
args['action']  = 'SUMMARY';
args['wt']      = 'json';

// Args for STATUS report 
// For numDocs, maxDoc, deletedDocs and Heap Memory
var args2 = new Packages.java.util.HashMap();
args2['action'] = 'STATUS';
args2['wt'] = 'json';

var jsonSummary = JSON.parse(adminClient.execute(args));
var jsonStatus  = JSON.parse(adminClient.execute(args2));

model.summary1 = jsonSummary.Summary.alfresco;
model.summary2 = jsonSummary.Summary.archive;
model.status1  = jsonStatus.status.alfresco.index;
model.status2  = jsonStatus.status.archive.index;

model.tools     = Admin.getConsoleTools("solr-tracking");
model.metadata  = Admin.getServerMetaData();