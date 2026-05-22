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
package org.orderofthebee.addons.support.tools.repo.jscript.rules;

import com.google.common.base.Preconditions;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;

import java.util.List;

/**
 * wrapper around the ruleservice of alfresco.
 *
 * Created by jgoldhammer on 06.11.16.
 */
public class ScriptRulesService extends BaseScopableProcessorExtension
{

    RuleService ruleService;

    public void setRuleService(RuleService ruleService)
    {
        this.ruleService = ruleService;
    }

    /**
     * checks wheter ruleservice is enabled
     * @return true if ruleservice is enabled, false if not.
     */
    public boolean isEnabled()
    {
        return ruleService.isEnabled();
    }

    /**
     * Enable rules for the current thread.
     */
    public void enableRules()
    {
        ruleService.enableRules();
    }

    /**
     * Disable rules for the current thread
     */
    public void disableRules()
    {
        ruleService.disableRules();
    }

    /**
     * enable rules for a certain node.
     * @param node
     */
    public void enableRules(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        ruleService.enableRules(node.getNodeRef());
    }

    public void enableRule(Rule rule)
    {
        ruleService.enableRule(rule);
    }

    /**
     * disable all rules for a certain node via ScriptNode
     * @param node
     */
    public void disableRules(ScriptNode node)
    {
        disableRules(node.getNodeRef());
    }

    /**
     * disable all rules for a certain node via Noderef
     * @param node
     */
    public void disableRules(NodeRef node)
    {
        Preconditions.checkNotNull(node);
        ruleService.disableRules(node);
    }

    /**
     * disable all rules for a certain node via String
     * @param node
     */
    public void disableRules(String node)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(NodeRef.isNodeRef(node));
        disableRules(new NodeRef(node));
    }

    /**
     * checks whether the rules are enabled for a script node
     * @param node
     */
    public boolean rulesEnabled(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return rulesEnabled(node.getNodeRef());
    }


    /**
     * checks whether the rules are enabled for a node (via noderef)
     * @param node
     */
    public boolean rulesEnabled(NodeRef node)
    {
        Preconditions.checkNotNull(node);
        return ruleService.rulesEnabled(node);
    }

    /**
     * checks whether the rules are enabled for a node (via string)
     * @param node
     */
    public boolean rulesEnabled(String node)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(NodeRef.isNodeRef(node));
        return rulesEnabled(new NodeRef(node));
    }

    public List<Rule> getRules(ScriptNode node)
    {
        return ruleService.getRules(node.getNodeRef());
    }

    /**
     * checks whether the node has rules attached
     * @param node
     * @return true if there are any rules, false if not.
     */
    public boolean hasRules(ScriptNode node)
    {
        return ruleService.hasRules(node.getNodeRef());
    }

    /**
     * checks whether the node has any direct rules attached
     * @param node
     * @return true if there are any direct rules, false if not.
     */
    public boolean hasDirectRules(ScriptNode node)
    {
        return ruleService.getRules(node.getNodeRef(), false).size() >0;
    }

    /**
     * disables a certain rule for now.
     */
    public void disableRule(Rule rule)
    {
        ruleService.disableRule(rule);
    }

    /**
     * get back the number of rules of a node.
     * @param node
     * @return
     */
    public int countRules(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        return ruleService.countRules(node.getNodeRef());
    }

    /**
     * get back the number of rules of a node.
     * @param node
     * @return
     */
    public int countRules(NodeRef node)
    {
        Preconditions.checkNotNull(node);
        return ruleService.countRules(node);
    }

    /**
     * get back the number of rules of a node.
     * @param node
     * @return number of rules attached
     */
    public int countRules(String node)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(NodeRef.isNodeRef(node));
        return ruleService.countRules(new NodeRef(node));
    }

    /**
     * remove all rules for a given noderef
     * @param node
     */
    public void removeAllRules(ScriptNode node)
    {
        Preconditions.checkNotNull(node);
        ruleService.removeAllRules(node.getNodeRef());
    }

    /**
     * remove all rules for a given noderef
     * @param node
     */
    public void removeAllRules(NodeRef node)
    {
        Preconditions.checkNotNull(node);
        ruleService.removeAllRules(node);
    }

    /**
     * remove all rules for a given noderef
     * @param node
     */
    public void removeAllRules(String node)
    {
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(NodeRef.isNodeRef(node));
        ruleService.removeAllRules(new NodeRef(node));
    }

}
