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
/**
 *
 */
package org.orderofthebee.addons.support.tools.repo.jscript.favorites;

import java.util.Collections;
import java.util.List;

import org.alfresco.query.PagingRequest;
import org.alfresco.query.PagingResults;
import org.alfresco.repo.favourites.PersonFavourite;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.favourites.FavouritesService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.springframework.extensions.webscripts.annotation.ScriptClass;
import org.springframework.extensions.webscripts.annotation.ScriptClassType;
import org.springframework.extensions.webscripts.annotation.ScriptMethod;
import org.springframework.extensions.webscripts.annotation.ScriptMethodType;

import com.google.common.base.Preconditions;

/**
 * script object for handling the FavouritesService.
 * Currently, it allows to add a node (folder, document and) and remove a favorite from a use
 *
 * @author Jens Goldhammer (fme AG)
 */

@ScriptClass(types=ScriptClassType.JavaScriptRootObject, code= "favorites", help="the root object for the favorites service")
public class ScriptFavoritesService extends BaseScopableProcessorExtension
{
    private FavouritesService favouritesService;
    private ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void setFavouritesService(FavouritesService favouritesService)
    {
        this.favouritesService = favouritesService;
    }

    @ScriptMethod(
        help = "adds the given script node as favorite for the current authenticated user and returns the favorite as scriptnode",
        output = "ScriptNode",
        code = "favorites.add(<Scriptnode>)",
        type = ScriptMethodType.WRITE)
    public ScriptNode add(ScriptNode node)
    {

        Preconditions.checkNotNull(node,"Node parameter must be given");
        ScriptNode result=null;
        String username = AuthenticationUtil.getRunAsUser();

        if(!favouritesService.isFavourite(username, node.getNodeRef()))
        {
            NodeRef nodeRef = favouritesService.addFavourite(username, node.getNodeRef()).getNodeRef();
            result = new ScriptNode(nodeRef, serviceRegistry,getScope());
        }

        return result;
    }

    @ScriptMethod(
        help = "adds the given script node as favorite for the given user and returns the favorite as scriptnode",
        output = "ScriptNode",
        code = "favorites.add(<Scriptnode>)",
        type = ScriptMethodType.WRITE)
    public ScriptNode add(ScriptNode node, final String username)
    {

        Preconditions.checkNotNull(node,"Node parameter must be given");
        Preconditions.checkNotNull(username,"username must be given");

        ScriptNode result = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<ScriptNode>()
        {
            @Override
            public ScriptNode doWork() throws Exception
            {
                ScriptNode result=null;
                if(!favouritesService.isFavourite(username, node.getNodeRef()))
                {
                    NodeRef nodeRef = favouritesService.addFavourite(username, node.getNodeRef()).getNodeRef();
                    result = new ScriptNode(nodeRef, serviceRegistry, getScope());
                }
                return result;
            }
        }, username);

        return result;
    }

    @ScriptMethod(
        help = "removes the given script node as favorite for the current authenticated user",
        output = "void",
        code = "",
        type = ScriptMethodType.WRITE)
    public void remove(ScriptNode node)
    {
        Preconditions.checkNotNull(node,"Node parameter must be given");
        String username = AuthenticationUtil.getRunAsUser();
        if(favouritesService.isFavourite(username, node.getNodeRef()))
        {
            favouritesService.removeFavourite(username, node.getNodeRef());
        }
    }

    @ScriptMethod(
        help = "checks if the given script node is a favorite for the current authenticated user",
        output = "void",
        code = "",
        type = ScriptMethodType.READ)
    public boolean isFavorite(ScriptNode node)
    {
        Preconditions.checkNotNull(node,"Node parameter must be given");
        return favouritesService.isFavourite(AuthenticationUtil.getRunAsUser(), node.getNodeRef());
    }

    @ScriptMethod(
        help = "get favorites for the current authenticated user",
        output = "void",
        code = "",
        type = ScriptMethodType.READ)
    public Scriptable getFavorites(int startCount, int limit)
    {

        PagingResults<PersonFavourite> favourites = favouritesService.getPagedFavourites(
                AuthenticationUtil.getRunAsUser(),
                FavouritesService.Type.ALL_FILTER_TYPES,
                Collections.emptyList(),
                new PagingRequest(startCount, limit));

        List<PersonFavourite> favouritesList = favourites.getPage();
        Object[] favoritesArray = favouritesList.toArray(new Object[favouritesList.size()]);

        return Context.getCurrentContext().newArray(getScope(), favoritesArray);


    }






}
