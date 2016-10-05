<import resource="classpath:alfresco/templates/webscripts/org/orderofthebee/ootbee-support-tools/admin/support-tools/scheduled-jobs.lib.js">

try {
    executeJobNow(args.jobName, args.groupName);
    model.success = true;
}
catch(e)
{
	model.success = false;
}