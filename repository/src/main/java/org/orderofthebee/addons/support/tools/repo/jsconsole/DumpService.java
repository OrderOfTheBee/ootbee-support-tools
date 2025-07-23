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
 * This file is part of code forked from the JavaScript Console project
 * which was licensed under the Apache License, Version 2.0 at the time.
 * In accordance with that license, the modifications / derivative work
 * is now being licensed under the LGPL as part of the OOTBee Support Tools
 * addon.
 */
package org.orderofthebee.addons.support.tools.repo.jsconsole;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.model.ContentModel;
import org.alfresco.model.RenditionModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.jscript.ScriptLogger;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.lock.mem.LockState;
import org.alfresco.service.cmr.audit.AuditQueryParameters;
import org.alfresco.service.cmr.audit.AuditService;
import org.alfresco.service.cmr.audit.AuditService.AuditQueryCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeRef.Status;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.rule.Rule;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.extensions.webscripts.ScriptValueConverter;

/**
 * Implements the 'dumpService' JavaScript extension object that is available in the JavaScript Console and is used for quickly dumping the
 * state of nodes in lieu of inspecting it via the Node Browser.
 *
 * @author Florian Maul (fme AG)
 *
 */
public class DumpService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptLogger.class);

    private NodeService nodeService;

    private PermissionService permissionService;

    private NamespaceService namespaceService;

    private VersionService versionService;

    private ContentService contentService;

    private DictionaryService dictionaryService;

    private RuleService ruleService;

    private WorkflowService workflowService;

    private TaggingService tagService;

    private WebDavService webDavService;

    private AuditService auditService;

    private SysAdminParams sysAdminParams;

    private final AtomicInteger dumpCounter = new AtomicInteger();

    private int dumpLimit;

    private LockService lockService;

    public List<Dump> addDump(final Object obj)
    {
        final List<Dump> dumpOutput = new LinkedList<>();

        if (obj != null)
        {
            final Object value = ScriptValueConverter.unwrapValue(obj);

            if (value instanceof Collection<?>)
            {
                final Collection<?> col = (Collection<?>) value;
                int currentValue = this.dumpCounter.get();
                for (final Object element : col)
                {
                    if (this.dumpLimit == -1 || currentValue <= this.dumpLimit)
                    {
                        dumpOutput.add(this.dumpObject(element));
                        currentValue = this.dumpCounter.incrementAndGet();
                    }
                    else
                    {
                        LOGGER.warn("Reached dump limit");
                    }
                }
            }
            else
            {
                final int currentValue = this.dumpCounter.getAndIncrement();
                if (this.dumpLimit == -1 || currentValue <= this.dumpLimit)
                {
                    dumpOutput.add(this.dumpObject(value));
                }
                else
                {
                    LOGGER.warn("Reached dump limit");
                }
            }
        }
        return dumpOutput;
    }

    private Dump dumpObject(final Object value)
    {
        final NodeRef nodeRef = this.extractNodeRef(value);

        // This method is used by the /api/metadata web script
        String jsonStr = "{}";

        if (this.nodeService.exists(nodeRef))
        {
            final JSONObject json = new JSONObject();

            try
            {
                json.put("nodeRef", nodeRef.toString());
                final QName type = this.nodeService.getType(nodeRef);
                final String typeString = type.toPrefixString(this.namespaceService);
                json.put("type", typeString);
                json.put("path", this.nodeService.getPath(nodeRef));
                json.put("displayPath", this.nodeService.getPath(nodeRef).toDisplayPath(this.nodeService, this.permissionService));

                final Status nodeStatus = this.nodeService.getNodeStatus(nodeRef);
                json.put("transactionId", nodeStatus.getDbTxnId());
                json.put("isDeleted", nodeStatus.isDeleted());

                this.extractProperties(nodeRef, json);
                this.extractAspects(nodeRef, json);
                this.extractPermissionInformation(nodeRef, json);
                this.extractVersionInformation(nodeRef, json);
                this.extractContentInformation(nodeRef, json, type);
                this.extractRulesInformation(nodeRef, json);
                this.extractWorkflowInformation(nodeRef, json);
                this.extractRenditionInformation(nodeRef, json);
                this.extractTagsInformation(nodeRef, json);
                this.extractLockInformation(nodeRef, json);

                json.put("webdav url",
                        this.sysAdminParams.getAlfrescoProtocol() + "://" + this.sysAdminParams.getAlfrescoHost() + ":"
                                + this.sysAdminParams.getAlfrescoPort() + "/" + this.sysAdminParams.getAlfrescoContext()
                                + this.webDavService.getWebdavUrl(nodeRef));

                json.put("audits", this.getAudits(nodeRef, true));
                json.put("audit count", this.getAudits(nodeRef, false).size());

            }
            catch (final JSONException error)
            {
                LOGGER.warn("JSON error creating node dump", error);
            }

            jsonStr = json.toString();
        }

        return new Dump(nodeRef.toString(), jsonStr);
    }

    /**
     * Extracts tags information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract tags information
     * @param json
     *     the JSON object structure into which to add tags information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractTagsInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        final List<String> tags = this.tagService.getTags(nodeRef);
        json.put("tags count", tags.size());

        final JSONArray tagsJson = new JSONArray();
        for (final String tag : tags)
        {
            tagsJson.put(tag);

        }
        json.put("tags", tagsJson);
    }

    /**
     * Extracts workflow counts information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract workflow counts information
     * @param json
     *     the JSON object structure into which to add workflow counts information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractWorkflowInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        json.put("workflows (active/completed)", this.workflowService.getWorkflowsForContent(nodeRef, true).size() + " / "
                + this.workflowService.getWorkflowsForContent(nodeRef, false).size());
    }

    /**
     * Extracts {@link ContentModel#PROP_CONTENT content} information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract content information
     * @param json
     *     the JSON object structure into which to add content information
     * @param type
     *     the qualified type of the node
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractContentInformation(final NodeRef nodeRef, final JSONObject json, final QName type) throws JSONException
    {
        if (this.dictionaryService.isSubClass(type, ContentModel.TYPE_CONTENT))
        {
            final ContentReader contentReader = this.contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
            if (contentReader != null)
            {
                json.put("content encoding", contentReader.getEncoding());
                json.put("content mimetype", contentReader.getMimetype());
                json.put("content size", byteCountToDisplaySize(contentReader.getSize()));
                json.put("content locale", contentReader.getLocale());
                json.put("content lastModified", new Date(contentReader.getLastModified()));
                json.put("content url", contentReader.getContentUrl());
            }
        }
    }

    // similar result to commons-io FileUtils#byteCountToDisplaySize(BigInteger)
    // implemented here to avoid hard dependency (flagged by extension inspector)
    private static String byteCountToDisplaySize(final long size)
    {
        String displaySize = String.valueOf(size) + " bytes";
        if (size > 1024)
        {
            final BigInteger bis = BigInteger.valueOf(size);
            final BigInteger unitStep = BigInteger.valueOf(2 ^ 10);
            BigInteger unitDivisor = BigInteger.valueOf(2 ^ 60);
            final String[] unitSuffixes = { " EB", " PB", " TB", " GB", " MB", " KB" };
            for (final String unitSuffix : unitSuffixes)
            {
                if (bis.compareTo(unitDivisor) > 0)
                {
                    displaySize = String.valueOf(bis.divide(unitDivisor)) + unitSuffix;
                    break;
                }
                unitDivisor = unitDivisor.divide(unitStep);
            }
        }
        return displaySize;
    }

    /**
     * Extracts lock status information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract lock information
     * @param json
     *     the JSON object structure into which to add lock information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractLockInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        final Set<QName> nodeAspects = this.nodeService.getAspects(nodeRef);

        if (nodeAspects.contains(ContentModel.ASPECT_LOCKABLE))
        {
            final LockState lockState = this.lockService.getLockState(nodeRef);
            json.put("lock Status", this.lockService.getLockStatus(nodeRef).toString());
            json.put("lock Type", this.lockService.getLockType(nodeRef));
            json.put("lock Owner", lockState.getOwner());
            json.put("lock ExpireDate", lockState.getExpires());
            json.put("lock LifeTime", lockState.getLifetime());
            json.put("lock additional info", lockState.getAdditionalInfo());
        }
        else
        {
            json.put("lock Status", "-");

        }
    }

    /**
     * Extracts version information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract version information
     * @param json
     *     the JSON object structure into which to add version information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractVersionInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        json.put("isAVersion", this.versionService.isAVersion(nodeRef));
        json.put("isVersioned", this.versionService.isVersioned(nodeRef));

        final VersionHistory versionHistory = this.versionService.getVersionHistory(nodeRef);
        if (versionHistory != null)
        {
            json.put("version count", versionHistory.getAllVersions().size());
            final Collection<Version> allVersions = versionHistory.getAllVersions();
            final List<String> tooltipFragments = new ArrayList<>(allVersions.size());
            for (final Version version : allVersions)
            {
                tooltipFragments.add(version.getVersionProperties().toString());
            }
            json.put("version count tooltip", tooltipFragments);
        }
        else
        {
            json.put("version count", "0");
        }
    }

    /**
     * Extracts properties information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract properties information
     * @param json
     *     the JSON object structure into which to add properties information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractProperties(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        // add properties
        final Map<QName, Serializable> nodeProperties = this.nodeService.getProperties(nodeRef);

        final Map<String, Serializable> nodePropertiesShortQNames = new TreeMap<>();
        for (final Entry<QName, Serializable> entry : nodeProperties.entrySet())
        {
            final QName qn = entry.getKey();
            final Serializable value = entry.getValue();
            try
            {
                nodePropertiesShortQNames.put(qn.toPrefixString(this.namespaceService), value);
            }
            catch (final NamespaceException ne)
            {
                LOGGER.debug("Ignoring property '{}' as it's namespace is not registered", qn);
            }
        }

        json.put("properties", nodePropertiesShortQNames);
    }

    /**
     * Extracts aspects information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract aspects information
     * @param json
     *     the JSON object structure into which to add aspects information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractAspects(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        // add aspects as an array
        final Set<QName> nodeAspects = this.nodeService.getAspects(nodeRef);
        final Set<String> nodeAspectsShortQNames = new LinkedHashSet<>(nodeAspects.size());
        for (final QName nextLongQName : nodeAspects)
        {
            nodeAspectsShortQNames.add(nextLongQName.toPrefixString(this.namespaceService));
        }
        json.put("aspects", nodeAspectsShortQNames);
    }

    /**
     * Extracts permission information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract permission information
     * @param json
     *     the JSON object structure into which to add permission information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractPermissionInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        json.put("inheritPermissions", this.permissionService.getInheritParentPermissions(nodeRef));

        final JSONArray permissionJson = new JSONArray();

        final Set<AccessPermission> permissions = this.permissionService.getAllSetPermissions(nodeRef);
        for (final AccessPermission accessPermission : permissions)
        {
            final JSONObject permission = new JSONObject();
            permission.put("authority", accessPermission.getAuthority());
            permission.put("authorityType", accessPermission.getAuthorityType());
            permission.put("accessStatus", accessPermission.getAccessStatus());
            permission.put("permission", accessPermission.getPermission());
            permission.put("directly", accessPermission.isSetDirectly());
            permissionJson.put(permission);
        }

        json.put("permissions", permissionJson);
    }

    /**
     * Extracts renditions information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract renditions information
     * @param json
     *     the JSON object structure into which to add renditions information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractRenditionInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        // cannot use RenditionService or RenditionService2
        // not consistently available / supported across the various ACS versions
        final List<ChildAssociationRef> renditions = this.nodeService.getChildAssocs(nodeRef, RenditionModel.ASSOC_RENDITION,
                RegexQNamePattern.MATCH_ALL);
        json.put("renditions count", renditions.size());

        final JSONArray renditionsJson = new JSONArray();
        for (final ChildAssociationRef rendition : renditions)
        {
            final JSONObject rendtionJson = new JSONObject();
            rendtionJson.put("typeName", rendition.getTypeQName().toPrefixString(this.namespaceService));
            rendtionJson.put("qName", rendition.getQName().toPrefixString(this.namespaceService));
            rendtionJson.put("childType", this.nodeService.getType(rendition.getChildRef()).toPrefixString(this.namespaceService));
            renditionsJson.put(rendtionJson);
        }
        json.put("renditions", renditionsJson);
    }

    /**
     * Extracts rules information of a node into the aggregate dump JSON object structure.
     *
     * @param nodeRef
     *     the reference to the node from which to extract rules information
     * @param json
     *     the JSON object structure into which to add rules information
     * @throws JSONException
     *     if an error occurs filling in the JSON object structure
     */
    private void extractRulesInformation(final NodeRef nodeRef, final JSONObject json) throws JSONException
    {
        final List<Rule> rulesLocal = this.ruleService.getRules(nodeRef, false);
        json.put("rules local ", rulesLocal.size());

        final List<Rule> rules = this.ruleService.getRules(nodeRef, true);
        json.put("rules inherited ", rules.size() - rulesLocal.size());
        final JSONArray rulesJson = new JSONArray();

        for (final Rule rule : rules)
        {
            final JSONObject ruleJson = new JSONObject();
            ruleJson.put("title", rule.getTitle());
            ruleJson.put("description", rule.getDescription());
            ruleJson.put("asynchronous", rule.getExecuteAsynchronously());
            ruleJson.put("disabled", rule.getRuleDisabled());
            ruleJson.put("ruleNode", rule.getNodeRef());
            ruleJson.put("ruleTypes", rule.getRuleTypes().toString());
            ruleJson.put("action", rule.getAction().getTitle());
            ruleJson.put("inherit", rule.isAppliedToChildren());
            final NodeRef owningNodeRef = this.ruleService.getOwningNodeRef(rule);
            ruleJson.put("owningNodeRef", owningNodeRef.toString());
            rulesJson.put(ruleJson);
        }

        json.put("rules", rulesJson);
    }

    /**
     * Extracts the node reference representing a particular value object, which can be either a {@link ScriptNode}, {@link NodeRef regular
     * node reference} or a textual value.
     *
     * @param value
     *     the value from which to extract the node reference
     * @return the node reference
     */
    private NodeRef extractNodeRef(final Object value)
    {
        final NodeRef nodeRef;
        if (value instanceof ScriptNode)
        {
            nodeRef = ((ScriptNode) value).getNodeRef();
        }
        else if (value instanceof NodeRef)
        {
            nodeRef = (NodeRef) value;
        }
        else if (value instanceof String)
        {
            nodeRef = new NodeRef((String) value);
        }
        else
        {
            throw new IllegalArgumentException("value of type " + value.getClass().getSimpleName() + " is not supported for dump");
        }
        return nodeRef;
    }

    private Collection<Map<String, Object>> getAudits(final NodeRef nodeRef, final boolean limited)
    {
        // Execute the query
        final AuditQueryParameters params = new AuditQueryParameters();
        params.setForward(false);
        params.addSearchKey(null, nodeRef.toString());

        final List<Map<String, Object>> entries = new ArrayList<>();
        final AuditQueryCallback callback = new AuditQueryCallback()
        {

            /**
             *
             * {@inheritDoc}
             */
            @Override
            public boolean valuesRequired()
            {
                if (limited)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

            /**
             *
             * {@inheritDoc}
             */
            @Override
            public boolean handleAuditEntryError(final Long entryId, final String errorMsg, final Throwable error)
            {
                return true;
            }

            /**
             *
             * {@inheritDoc}
             */
            @Override
            public boolean handleAuditEntry(final Long entryId, final String applicationName, final String user, final long time, final Map<String, Serializable> values)
            {

                final Map<String, Object> entry = new HashMap<>(11);
                if (limited)
                {

                    entry.put(JavascriptConsoleScriptObject.JSON_KEY_ENTRY_ID, entryId);
                    entry.put(JavascriptConsoleScriptObject.JSON_KEY_ENTRY_APPLICATION, applicationName);
                    if (user != null)
                    {
                        entry.put(JavascriptConsoleScriptObject.JSON_KEY_ENTRY_USER, user);
                    }
                    entry.put(JavascriptConsoleScriptObject.JSON_KEY_ENTRY_TIME, new Date(time));
                    if (values != null)
                    {
                        // Convert values to Strings
                        final Map<String, String> valueStrings = new HashMap<>(values.size() * 2);
                        for (final Map.Entry<String, Serializable> mapEntry : values.entrySet())
                        {
                            final String key = mapEntry.getKey();
                            final Serializable value = mapEntry.getValue();
                            try
                            {
                                final String valueString = DefaultTypeConverter.INSTANCE.convert(String.class, value);
                                valueStrings.put(key, valueString);
                            }
                            catch (final TypeConversionException e)
                            {
                                valueStrings.put(key, value.toString());
                            }

                        }
                        entry.put(JavascriptConsoleScriptObject.JSON_KEY_ENTRY_VALUES, valueStrings);
                    }
                    entries.add(entry);
                    return true;
                }
                else
                {
                    entries.add(entry);
                    return true;
                }
            }
        };
        int limit;
        if (limited)
        {
            limit = 5;
        }
        else
        {
            limit = 100;
        }
        this.auditService.auditQuery(callback, params, limit);
        return entries;
    }

    public void setNodeService(final NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setPermissionService(final PermissionService permissionService)
    {
        this.permissionService = permissionService;
    }

    public void setNamespaceService(final NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    public void setVersionService(final VersionService versionService)
    {
        this.versionService = versionService;
    }

    public void setContentService(final ContentService contentService)
    {
        this.contentService = contentService;
    }

    public void setDictionaryService(final DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setRuleService(final RuleService ruleService)
    {
        this.ruleService = ruleService;
    }

    public void setWorkflowService(final WorkflowService workflowService)
    {
        this.workflowService = workflowService;
    }

    public void setTagService(final TaggingService tagService)
    {
        this.tagService = tagService;
    }

    public void setWebDavService(final WebDavService webDavService)
    {
        this.webDavService = webDavService;
    }

    public void setAuditService(final AuditService auditService)
    {
        this.auditService = auditService;
    }

    public void setSysAdminParams(final SysAdminParams sysAdminParams)
    {
        this.sysAdminParams = sysAdminParams;
    }

    public void setDumpLimit(final int dumpLimit)
    {
        this.dumpLimit = dumpLimit;
    }

    public void setLockService(final LockService lockService)
    {
        this.lockService = lockService;
    }

}
