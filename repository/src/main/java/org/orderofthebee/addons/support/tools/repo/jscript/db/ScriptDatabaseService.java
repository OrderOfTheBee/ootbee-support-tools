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
package org.orderofthebee.addons.support.tools.repo.jscript.db;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.AuthorityService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.google.common.base.Preconditions;

/**
 * JavaScript root object for database operations.
 * Only admin users are allowed to execute database queries.
 *
 * @author jgoldhammer
 */
@ScriptClass(types = ScriptClassType.JavaScriptRootObject, code = "database", help = "Root object for database service")
public class ScriptDatabaseService extends BaseProcessorExtension implements ApplicationContextAware
{

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    private void checkAdminAuthority()
    {
        AuthorityService authorityService = (AuthorityService) applicationContext.getBean("authorityService");
        if (!authorityService.isAdminAuthority(AuthenticationUtil.getFullyAuthenticatedUser()))
        {
            throw new RuntimeException("Only admin users are allowed to execute database queries");
        }
    }

    @ScriptMethod()
    public int update(String dataSourceName, String sql, Object... params)
    {
        checkAdminAuthority();
        JdbcDaoSupport daoSupport = getDaoSupport(dataSourceName);
        Preconditions.checkNotNull(daoSupport, " daosupport is null- please check the datasource name");
        return daoSupport.getJdbcTemplate().update(sql, params);
    }

    public Map<String, Object>[] query(String dataSourceName, String sql, Object... params)
    {
        checkAdminAuthority();
        JdbcDaoSupport daoSupport = getDaoSupport(dataSourceName);
        Preconditions.checkNotNull(daoSupport, " daosupport is null- please check the datasource name");

        List<Map<String, Object>> result = daoSupport.getJdbcTemplate().queryForList(sql, params);
        @SuppressWarnings("unchecked")
        Map<String, Object>[] arr = new Map[result.size()];
        for (int i = 0; i < result.size(); i++)
        {
            arr[i] = result.get(i);
        }
        return arr;
    }

    private JdbcDaoSupport getDaoSupport(String dataSourceName)
    {
        Object dsBean = applicationContext.getBean(dataSourceName);

        if (dsBean instanceof DataSource)
        {
            JdbcDaoSupport daoSupport = new NamedParameterJdbcDaoSupport();
            daoSupport.setDataSource((DataSource) dsBean);
            return daoSupport;
        }
        else
        {

            throw new AlfrescoRuntimeException("dataSource '" + dataSourceName + "' not found.");
        }
    }

}