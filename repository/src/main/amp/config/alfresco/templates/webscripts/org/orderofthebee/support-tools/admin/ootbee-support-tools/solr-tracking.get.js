<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">

var ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var solr4 =  ctxt.getBean('solr4',
Packages.org.alfresco.repo.search.impl.solr.SolrChildApplicationContextFactory);
var childCtx = solr4.getApplicationContext();

// SUMMARY report 
var adminClient = childCtx.getBean('search.solrAdminHTTPCLient', Packages.org.alfresco.repo.search.impl.solr.SolrAdminHTTPClient);
var args = new Packages.java.util.HashMap();
args['action']  = 'SUMMARY';
args['wt']      = 'json';

// STATUS report
// For numDocs, maxDoc, deletedDocs and Heap Memory
var adminClient2 = childCtx.getBean('search.solrAdminHTTPCLient', Packages.org.alfresco.repo.search.impl.solr.SolrAdminHTTPClient);
var args2 = new Packages.java.util.HashMap();
args2['action'] = 'STATUS';
args2['wt'] = 'json';

var json  = JSON.parse(adminClient.execute(args));
var json2 = JSON.parse(adminClient2.execute(args2));

model.summary   = json.Summary;
model.searcher1 = json2.status.alfresco.index;
model.searcher2 = json2.status.archive.index;

model.tools     = Admin.getConsoleTools("solr-tracking");
model.metadata  = Admin.getServerMetaData();