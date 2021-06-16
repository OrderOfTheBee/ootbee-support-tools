/**
 * 
 */
package de.fme.jsconsole.serverInfo;

import java.io.IOException;
import java.util.List;

import org.alfresco.repo.tenant.TenantService;
import org.alfresco.service.cmr.module.ModuleDetails;
import org.alfresco.service.cmr.module.ModuleService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.license.LicenseService;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * The Class ServerInfoWebscript.
 *
 * @author jgoldhammer
 */
public class ServerInfoWebscript extends AbstractWebScript {

	/** The workflow service. */
	private WorkflowService workflowService;

	/**
	 * Sets the workflow service.
	 *
	 * @param workflowService the new workflow service
	 */
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	/** The license service. */
	private LicenseService licenseService;
	
	/** The tenant service. */
	private TenantService tenantService;
	
	/** The module service. */
	private ModuleService moduleService;

	/**
	 * Gets the modules.
	 *
	 * @return the modules
	 */
	public String getModules() {
		List<ModuleDetails> allModules = moduleService.getAllModules();

		for (ModuleDetails moduleDetails : allModules) {
			
//			modulesText += module.id + " v" + module.version;
//			if (i != modulesList.size() - 1) {
//				modulesText += " - ";
//			}
		}
		return null;
	}

	/**
	 * Execute.
	 *
	 * @param req the req
	 * @param res the res
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {

		int workflowDefinitionSize = workflowService.getAllDefinitions().size();
		Integer remainingDays = licenseService.getLicense().getRemainingDays();

	}

}
