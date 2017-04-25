<import resource="classpath:alfresco/templates/webscripts/org/alfresco/repository/admin/admin-common.lib.js">


var ctxt = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var solr4 =  ctxt.getBean('solr4',
Packages.org.alfresco.repo.search.impl.solr.SolrChildApplicationContextFactory);
var childCtx = solr4.getApplicationContext();
var adminClient = childCtx.getBean('search.solrAdminHTTPCLient', Packages.org.alfresco.repo.search.impl.solr.SolrAdminHTTPClient);


var args = new Packages.java.util.HashMap();
args['action'] = 'SUMMARY';
args['wt'] = 'json';

var json = JSON.parse(adminClient.execute(args));

model.summary = json.Summary;


model.tools = Admin.getConsoleTools("solr-tracking");
model.metadata = Admin.getServerMetaData();