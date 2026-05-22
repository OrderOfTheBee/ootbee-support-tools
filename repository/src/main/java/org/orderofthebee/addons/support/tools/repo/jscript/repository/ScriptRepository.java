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
package org.orderofthebee.addons.support.tools.repo.jscript.repository;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.site.DocLibNodeLocator;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteService;

/**
 * wraps the repository bean to have quick methods to access certain nodes in the repository.
 */
public class ScriptRepository extends BaseScopableProcessorExtension
{

    Repository repository;
    ServiceRegistry serviceRegistry;
    SiteService siteService;
    DocLibNodeLocator docLibNodeLocator;
    PersonService personService;

    public void setPersonService(PersonService personService)
    {
        this.personService = personService;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }

    public void setSiteService(SiteService siteService)
    {
        this.siteService = siteService;
    }

    public void setDocLibNodeLocator(DocLibNodeLocator docLibNodeLocator)
    {
        this.docLibNodeLocator = docLibNodeLocator;
    }

    public ScriptNode getCompanyHome()
    {
        return new ScriptNode(repository.getCompanyHome(),serviceRegistry, getScope());
    }

    public ScriptNode getRootHome()
    {
        return new ScriptNode(repository.getRootHome(), serviceRegistry, getScope());
    }

    public ScriptNode getUserHome()
    {
        return new ScriptNode(repository.getUserHome(repository.getPerson()), serviceRegistry,getScope());
    }

    public ScriptNode getPerson()
    {
        return new ScriptNode(repository.getPerson(), serviceRegistry,getScope());
    }

    public ScriptNode getPeopleContainer()
    {
        return new ScriptNode(personService.getPeopleContainer(), serviceRegistry, getScope());
    }

    public ScriptNode getSitesRoot()
    {
        return new ScriptNode(siteService.getSiteRoot(), serviceRegistry,getScope());
    }

    public ScriptNode getForDocLibForNode(ScriptNode source)
    {
        return new ScriptNode(docLibNodeLocator.getNode(source.getNodeRef(),null), serviceRegistry,getScope());
    }


}
