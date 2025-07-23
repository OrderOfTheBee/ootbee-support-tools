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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.alfresco.processor.ProcessorExtension;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.processor.BaseProcessor;
import org.alfresco.repo.processor.BaseProcessorExtension;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.repo.workflow.jscript.JscriptWorkflowTask;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.ScriptService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.alfresco.util.PropertyCheck;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * @author Axel Faust
 */
public class AlfrescoScriptAPITernGet extends DeclarativeWebScript implements InitializingBean
{

    private static final Logger LOGGER = LoggerFactory.getLogger(AlfrescoScriptAPITernGet.class);

    private static final Collection<Class<?>> PRIMITIVE_NUMBER_CLASSES = Collections
            .unmodifiableList(Arrays.<Class<?>> asList(byte.class, short.class, int.class, long.class, float.class, double.class));

    private static final Collection<Class<?>> CUTOFF_CLASSES = Collections.unmodifiableList(
            Arrays.<Class<?>> asList(Object.class, org.springframework.extensions.webscripts.processor.BaseProcessorExtension.class,
                    BaseProcessorExtension.class, BaseScopableProcessorExtension.class, ScriptableObject.class, NativeObject.class));

    private static final Collection<Class<?>> CUTOFF_INTERFACES = Collections
            .unmodifiableList(Arrays.<Class<?>> asList(Scriptable.class, ProcessorExtension.class,
                    org.springframework.extensions.surf.core.processor.ProcessorExtension.class, WebScript.class, Scopeable.class));

    private static final Collection<String> INIT_METHOD_NAMES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.<String> asList("init", "register")));

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER;
    static
    {
        // Alfresco 5.0.d (earliest version we want to try to support) contains Spring 3.x
        // StandardReflectionParameterNameDiscoverer is only available from Spring 4.x on
        Class<?> cls = null;
        try
        {
            cls = Class.forName("org.springframework.core.StandardReflectionParameterNameDiscoverer");
        }
        catch (final ClassNotFoundException ex)
        {
            try
            {
                cls = Class.forName("org.springframework.core.LocalVariableTableParameterNameDiscoverer");
            }
            catch (final ClassNotFoundException ex2)
            {
                LOGGER.info("No valid Spring parameter name discoverer class found");
            }
        }

        ParameterNameDiscoverer pnd = null;
        if (cls != null)
        {
            try
            {
                pnd = (ParameterNameDiscoverer) cls.newInstance();
            }
            catch (final InstantiationException | IllegalAccessException ex)
            {
                LOGGER.warn("Failed to instantiate Spring paramater name discoverer", ex);
            }
        }
        PARAMETER_NAME_DISCOVERER = pnd;
    }

    protected NamespaceService namespaceService;

    protected DictionaryService dictionaryService;

    protected ScriptService scriptService;

    protected PersonService personService;

    protected ServiceRegistry serviceRegistry;

    protected BaseProcessor scriptProcessor;

    protected Properties properties;

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet()
    {
        PropertyCheck.mandatory(this, "namespaceService", this.namespaceService);
        PropertyCheck.mandatory(this, "dictionaryService", this.dictionaryService);
        PropertyCheck.mandatory(this, "scriptService", this.scriptService);
        PropertyCheck.mandatory(this, "personService", this.personService);
        PropertyCheck.mandatory(this, "serviceRegistry", this.serviceRegistry);
        PropertyCheck.mandatory(this, "scriptProcessor", this.scriptProcessor);

        PropertyCheck.mandatory(this, "properties", this.properties);
    }

    /**
     * @param namespaceService
     *     the namespaceService to set
     */
    public void setNamespaceService(final NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }

    /**
     * @param dictionaryService
     *     the dictionaryService to set
     */
    public void setDictionaryService(final DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /**
     * @param scriptService
     *     the scriptService to set
     */
    public void setScriptService(final ScriptService scriptService)
    {
        this.scriptService = scriptService;
    }

    /**
     * @param personService
     *     the personService to set
     */
    public void setPersonService(final PersonService personService)
    {
        this.personService = personService;
    }

    /**
     * @param serviceRegistry
     *     the serviceRegistry to set
     */
    public void setServiceRegistry(final ServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * @param scriptProcessor
     *     the scriptProcessor to set
     */
    public void setScriptProcessor(final BaseProcessor scriptProcessor)
    {
        this.scriptProcessor = scriptProcessor;
    }

    /**
     * @param properties
     *     the properties to set
     */
    public void setProperties(final Properties properties)
    {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        final Map<String, Object> model = new HashMap<>();

        this.prepareCoreScriptAPIJavaTypeDefinitions(model);
        final Map<String, Object> coreScriptModel = this.prepareCoreScriptAPIGlobalDefinitions(model);
        this.preparePropertyDefinitions(model);

        this.prepareWebScriptAPIJavaTypeDefinitions(req, model, coreScriptModel);
        this.prepareWebScriptAPIGlobalDefinitions(req, model, coreScriptModel);

        return model;
    }

    /**
     * Prepares the type definitions for the core script API of Alfresco (common across all use cases)
     *
     * @param model
     *     the current web script model into which to insert the definitions
     */
    protected void prepareCoreScriptAPIJavaTypeDefinitions(final Map<String, Object> model)
    {
        final Map<String, Object> scriptModel = this.buildScriptAPIModel();
        model.put("scriptAPIJavaTypeDefinitions", this.prepareJavaTypeDefinitions(scriptModel));
    }

    /**
     * Prepares the type definitions for the script API specific to Alfresco web scripts
     *
     * @param req
     *     the current web script request
     * @param model
     *     the current web script model into which to insert the definitions
     * @param coreScriptModel
     *     the core script API model in order to detect / avoid duplicate root object definitions
     */
    protected void prepareWebScriptAPIJavaTypeDefinitions(final WebScriptRequest req, final Map<String, Object> model,
            final Map<String, Object> coreScriptModel)
    {
        final ScriptDetails script = this.getExecuteScript(req.getContentType());
        final Map<String, Object> scriptModel = this.createScriptParameters(req, null, script, Collections.<String, Object> emptyMap());

        this.removeCoreScriptAPIGlobalsFromWebScriptAPI(scriptModel, coreScriptModel);

        model.put("webScriptAPIJavaTypeDefinitions", this.prepareJavaTypeDefinitions(scriptModel));
    }

    /**
     * Removes core script API globals from a script model specific to web script execution
     *
     * @param scriptModel
     *     the script model of a web script
     * @param coreScriptModel
     *     the core script API model in order to detect / avoid duplicate root object definitions
     */
    protected void removeCoreScriptAPIGlobalsFromWebScriptAPI(final Map<String, Object> scriptModel,
            final Map<String, Object> coreScriptModel)
    {
        // avoid unnecessary overlap between web script and standard script API model
        // remove identical key-value pairs handled by core script API
        final Collection<String> keysToRemove = new HashSet<>();
        for (final Entry<String, Object> entry : scriptModel.entrySet())
        {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            if (coreScriptModel.containsKey(key) && value.getClass().equals(scriptModel.get(key).getClass()))
            {
                keysToRemove.add(entry.getKey());
            }
        }
    }

    /**
     * Prepares the type definitions for Java classes found in a specific model.
     *
     * @param model
     *     the model containing objects exposed to scripts
     * @return the list of models for each type found directly or transitively (via public properties / methods) in the model elements
     */
    protected List<Map<String, Object>> prepareJavaTypeDefinitions(final Map<String, Object> model)
    {
        final List<Map<String, Object>> typeDefinitions = new ArrayList<>();

        final Comparator<? super Class<?>> clsComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());

        final Collection<Class<?>> classesToDescribe = new HashSet<>();
        final Collection<Class<?>> classesDescribed = new HashSet<>();

        for (final Entry<String, Object> modelEntry : model.entrySet())
        {
            if (modelEntry.getValue() instanceof NodeRef)
            {
                modelEntry.setValue(new ScriptNode((NodeRef) modelEntry.getValue(), this.serviceRegistry));
            }
        }

        for (final String propertyName : this.properties.stringPropertyNames())
        {
            if (propertyName.startsWith("type.") && propertyName.endsWith(".forceExposeClass")
                    && Boolean.parseBoolean(this.properties.getProperty(propertyName)))
            {
                final String className = propertyName.substring(propertyName.indexOf('.') + 1, propertyName.lastIndexOf('.'));
                LOGGER.trace("Class {} configured for forced exposure", className);
                try
                {
                    final Class<?> cls = Class.forName(className);
                    classesToDescribe.add(cls);
                }
                catch (final ClassNotFoundException cnf)
                {
                    LOGGER.debug("Class {} with configured forced exposure not found in classpath", className);
                }
            }
        }

        for (final Entry<String, Object> globalEntry : model.entrySet())
        {
            final String globalPrefix = "global." + globalEntry.getKey();
            final String skip = this.properties.getProperty(globalPrefix + ".skip");
            if (!Boolean.parseBoolean(skip))
            {
                final Class<?> realValueType = globalEntry.getValue().getClass();
                final Class<?> effectiveValueType = this.determineEffectiveType(realValueType, globalPrefix);
                this.determineType(effectiveValueType, classesToDescribe);
            }
        }

        while (classesToDescribe.size() > classesDescribed.size())
        {
            final Collection<Class<?>> remainingClasses = new TreeSet<>(clsComparator);
            remainingClasses.addAll(classesToDescribe);
            remainingClasses.removeAll(classesDescribed);

            for (final Class<?> cls : remainingClasses)
            {
                if (!(CUTOFF_CLASSES.contains(cls) || CUTOFF_INTERFACES.contains(cls)))
                {
                    final String typeClsName = cls.getName();
                    final String typePrefix = "type." + typeClsName;
                    final String skip = this.properties.getProperty(typePrefix + ".skip");

                    if (!Boolean.parseBoolean(skip))
                    {
                        final Map<String, Object> typeDefinition = new TreeMap<>();
                        // TODO Handle potential class name conflicts (different packages, same base name)
                        final Collection<Class<?>> relatedClasses = this.fillClassTypeDefinition(cls, typeDefinition);
                        classesToDescribe.addAll(relatedClasses);
                        typeDefinitions.add(typeDefinition);
                    }
                }
                classesDescribed.add(cls);
            }
        }

        // all types have a name
        Collections.sort(typeDefinitions, (td1, td2) -> ((String) td1.get("name")).compareTo((String) td2.get("name")));

        return typeDefinitions;
    }

    /**
     * Prepares the definitions for global / root scope objects found in the core script API model (common across all use cases)
     *
     * @param model
     *     the model into which to insert the global definitions
     * @return the scriptModel
     */
    protected Map<String, Object> prepareCoreScriptAPIGlobalDefinitions(final Map<String, Object> model)
    {
        final Map<String, Object> scriptModel = this.buildScriptAPIModel();
        final List<Map<String, Object>> globalDefinitions = this.prepareGlobalDefinitions(scriptModel);

        // add our "print()" / "dump()" root scope functions
        Map<String, Object> globalDefinition = new HashMap<>();
        globalDefinition.put("name", "print");
        globalDefinition.put("type", "fn(obj: ?)");
        this.findAndAddTernDoc("global.print", globalDefinition);
        globalDefinitions.add(globalDefinition);

        globalDefinition = new HashMap<>();
        globalDefinition.put("name", "dump");
        globalDefinition.put("type", "fn(obj: ?)");
        this.findAndAddTernDoc("global.dump", globalDefinition);
        globalDefinitions.add(globalDefinition);

        model.put("scriptAPIGlobalDefinitions", globalDefinitions);

        return scriptModel;
    }

    /**
     * Prepares the definitions for global / root scope objects found in the script API model specific to Alfresco web scripts
     *
     * @param req
     *     the current web script request
     * @param model
     *     the model into which to insert the global definitions
     * @param coreScriptModel
     *     the core script API model in order to detect / avoid duplicate root object definitions
     */
    protected void prepareWebScriptAPIGlobalDefinitions(final WebScriptRequest req, final Map<String, Object> model,
            final Map<String, Object> coreScriptModel)
    {
        final ScriptDetails script = this.getExecuteScript(req.getContentType());
        final Map<String, Object> scriptModel = this.createScriptParameters(req, null, script, Collections.<String, Object> emptyMap());

        this.removeCoreScriptAPIGlobalsFromWebScriptAPI(scriptModel, coreScriptModel);

        model.put("webScriptAPIGlobalDefinitions", this.prepareGlobalDefinitions(scriptModel));
    }

    /**
     * Prepares the global definitions for Java objects found in a specific model.
     *
     * @param model
     *     the model containing objects exposed to scripts
     * @return the list of models for each global value
     */
    protected List<Map<String, Object>> prepareGlobalDefinitions(final Map<String, Object> model)
    {
        final List<Map<String, Object>> globalDefinitions = new ArrayList<>();

        for (final Entry<String, Object> modelEntry : model.entrySet())
        {
            if (modelEntry.getValue() instanceof NodeRef)
            {
                modelEntry.setValue(new ScriptNode((NodeRef) modelEntry.getValue(), this.serviceRegistry));
            }
        }

        final Collection<Class<?>> dummyClasses = new HashSet<>();

        for (final Entry<String, Object> globalEntry : model.entrySet())
        {
            final String globalPrefix = "global." + globalEntry.getKey();

            final String skip = this.properties.getProperty(globalPrefix + ".skip");
            if (!Boolean.parseBoolean(skip))
            {
                final Map<String, Object> globalDefinition = new HashMap<>();
                globalDefinition.put("name", globalEntry.getKey());

                final Object value = globalEntry.getValue();
                final Class<?> realValueType = value.getClass();
                final Class<?> effectiveValueType = this.determineEffectiveType(realValueType, globalPrefix);
                final Class<?> valueType = this.determineType(effectiveValueType, dummyClasses);

                String type = this.findTernName(valueType);
                final String globalTernName = this.properties.getProperty(globalPrefix + ".ternName");
                if (globalTernName != null && !globalTernName.isEmpty())
                {
                    type = globalTernName;
                }
                globalDefinition.put("type", type);

                this.findAndAddTernDoc(globalPrefix, globalDefinition);

                globalDefinitions.add(globalDefinition);
            }
        }

        return globalDefinitions;
    }

    protected Collection<Class<?>> fillClassTypeDefinition(final Class<?> cls, final Map<String, Object> typeDefinition)
    {
        final Collection<Class<?>> relatedClasses = new HashSet<>();

        final String clsName = cls.getName();
        final String commonPrefix = "type." + clsName;

        final String name = this.findTernName(cls);
        typeDefinition.put("name", name);

        this.findAndAddTernDoc(commonPrefix, typeDefinition);

        final String ternUrl = this.properties.getProperty(commonPrefix + ".ternUrl");
        if (ternUrl != null && !ternUrl.isEmpty())
        {
            typeDefinition.put("url", ternUrl);
        }

        String superName = this.properties.getProperty(commonPrefix + ".proto");
        if ((superName == null || superName.isEmpty()))
        {
            if (cls.isInterface())
            {
                Class<?> parentCls = null;

                final Class<?>[] interfaceClasses = cls.getInterfaces();
                for (final Class<?> interfaceClass : interfaceClasses)
                {
                    if (!CUTOFF_INTERFACES.contains(interfaceClass))
                    {
                        relatedClasses.add(interfaceClass);
                        parentCls = interfaceClass;
                    }
                    break;
                }

                if (parentCls != null)
                {
                    superName = this.findTernName(parentCls);
                }
            }
            else
            {
                final Class<?> parentCls = cls.getSuperclass();
                if (parentCls != null && !CUTOFF_CLASSES.contains(parentCls))
                {
                    relatedClasses.add(parentCls);
                    superName = this.findTernName(parentCls);
                }
            }
        }

        if (superName != null && !superName.isEmpty())
        {
            typeDefinition.put("prototype", superName);
        }

        final String nameOnly = this.properties.getProperty(commonPrefix + ".nameOnly");
        if (nameOnly == null || nameOnly.isEmpty() || !Boolean.parseBoolean(nameOnly))
        {
            this.fillClassTypeMemberDefinitions(cls, typeDefinition, relatedClasses, commonPrefix);
        }

        return relatedClasses;
    }

    protected void fillClassTypeMemberDefinitions(final Class<?> cls, final Map<String, Object> typeDefinition,
            final Collection<Class<?>> relatedClasses, final String commonPrefix)
    {
        final List<Map<String, Object>> memberDefinitions = new ArrayList<>();
        final Map<String, AtomicInteger> usedMemberNames = new HashMap<>();
        final Collection<String> handledProperties = new HashSet<>();

        final List<Method> methods = this.collectDocumentableMethods(cls);

        for (final Method method : methods)
        {
            final String methodName = method.getName();
            String memberName;
            Map<String, Object> memberDefinition = null;

            // treat as property only if there are no overloaded variants
            final Class<?>[] parameterTypes = method.getParameterTypes();
            // note: Rhino allows "isXYZ" for any properties, not just boolean-typed ones
            if (methodName.matches("^(get|is)[A-Z].*$") && parameterTypes.length == 0
                    && methods.stream().map(Method::getName).filter(methodName::equals).count() == 1)
            {
                final int startIdx = methodName.startsWith("is") ? 2 : 3;
                memberName = methodName.substring(startIdx, startIdx + 1).toLowerCase(Locale.ENGLISH) + methodName.substring(startIdx + 1);

                if (!handledProperties.contains(memberName) && !methods.stream().map(Method::getName).anyMatch(memberName::equals))
                {
                    memberDefinition = this.buildClassPropertyMemberDefinition(cls, method, commonPrefix, memberName, relatedClasses);
                    if (memberDefinition != null)
                    {
                        handledProperties.add(memberName);
                    }

                    // in any case, getter is always callable directly as well
                    final Map<String, Object> methodMemberDefinition = this.buildClassMethodMemberDefinition(cls, method, commonPrefix,
                            relatedClasses);
                    // we know there is no conflict
                    memberDefinitions.add(methodMemberDefinition);
                    usedMemberNames.put(methodName, new AtomicInteger(1));
                }
                else
                {
                    // potential corner cases:
                    // 1) rare conflict between getXY and isXY (i.e. AuthorityType.getFixedString vs. AuthorityType.isFixedString)
                    // due to method ordering, isXY will always be after getXY (Rhino prioritises getXY over isXY as well)
                    // 2) property name shadows a method

                    // add as explicitly callable method then
                    memberName = methodName;
                    memberDefinition = this.buildClassMethodMemberDefinition(cls, method, commonPrefix, relatedClasses);
                }
            }
            // no need for special treatment of setters
            // same as getters, setters are always callable directly
            else
            {
                memberName = methodName;
                memberDefinition = this.buildClassMethodMemberDefinition(cls, method, commonPrefix, relatedClasses);
            }

            if (memberDefinition != null)
            {
                memberDefinitions.add(memberDefinition);

                AtomicInteger count = usedMemberNames.get(memberName);
                if (count == null)
                {
                    count = new AtomicInteger(1);
                    usedMemberNames.put(memberName, count);
                }
                else
                {
                    memberDefinition.put("originalName", memberName);
                    memberDefinition.put("name", memberName + count.incrementAndGet());
                }
            }
        }

        // all members have a name
        Collections.sort(memberDefinitions, (md1, md2) -> ((String) md1.get("name")).compareTo((String) md2.get("name")));
        typeDefinition.put("members", memberDefinitions);
    }

    protected List<Method> collectDocumentableMethods(final Class<?> cls)
    {
        // collect classes in hierarchy from base to special
        final List<Class<?>> fullClassHierarchy = new LinkedList<>();
        final Set<Class<?>> linearClassHierarchy = new HashSet<>();
        Class<?> curCls = cls;
        while (curCls != null && (Object.class.equals(curCls) || !CUTOFF_CLASSES.contains(curCls)))
        {
            linearClassHierarchy.add(curCls);
            fullClassHierarchy.add(0, curCls);

            final List<Class<?>> interfaces = new ArrayList<>(Arrays.asList(curCls.getInterfaces()));
            final List<Class<?>> cutoffInterfaces = new ArrayList<>(interfaces);

            interfaces.removeAll(CUTOFF_INTERFACES);
            cutoffInterfaces.retainAll(CUTOFF_INTERFACES);

            // include interfaces before curCls so we capture method definitions before implementations / redefinition
            if (!interfaces.isEmpty())
            {
                // interfaces are the collection of superclasses for an interface
                if (curCls.isInterface())
                {
                    fullClassHierarchy.removeAll(interfaces);
                    if (interfaces.size() > 1)
                    {
                        fullClassHierarchy.addAll(0, interfaces.subList(1, interfaces.size()));
                    }
                }
                else
                {
                    // move all interfaces to the front
                    fullClassHierarchy.removeAll(interfaces);
                    fullClassHierarchy.addAll(0, interfaces);
                }
            }

            // we found at least one interface telling us to stop class hierarchy traversal (cutoff)
            if (!cutoffInterfaces.isEmpty())
            {
                curCls = Object.class;
            }
            else if (curCls.isInterface())
            {
                curCls = !interfaces.isEmpty() ? interfaces.get(0) : Object.class;
            }
            else
            {
                curCls = curCls.getSuperclass();
            }
        }

        // collect declared public methods (other than cls.getMethods we don't want overridden methods, only initial implementations)
        final Map<Pair<String, List<Class<?>>>, Method> methodsByNameAndParameterTypes = new HashMap<>();
        final Set<Pair<String, List<Class<?>>>> skippedHierarchyMethods = new HashSet<>();

        while (!fullClassHierarchy.isEmpty())
        {
            curCls = fullClassHierarchy.remove(0);
            // anything in linear hierarchy would be handled via prototype reference
            if (cls.equals(curCls) || !linearClassHierarchy.contains(curCls))
            {
                final Method[] declaredMethods = curCls.getDeclaredMethods();
                for (final Method declaredMethod : declaredMethods)
                {
                    final String methodName = declaredMethod.getName();
                    final Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                    final Pair<String, List<Class<?>>> key = new Pair<>(methodName, Arrays.asList(parameterTypes));

                    if (!skippedHierarchyMethods.contains(key))
                    {
                        if (this.isMemberSkipped(curCls, declaredMethod, true))
                        {
                            skippedHierarchyMethods.add(key);
                        }
                        else
                        {
                            final boolean isPotentialSetter = methodName.matches("^set[A-Z].*") && declaredMethod.getParameterCount() == 1;
                            final boolean isPotentialGetter = methodName.matches("^get[A-Z].*") && declaredMethod.getParameterCount() == 0;
                            if (Modifier.isPublic(declaredMethod.getModifiers()) && !Modifier.isStatic(declaredMethod.getModifiers())
                                    && !INIT_METHOD_NAMES.contains(methodName)
                                    && (curCls.isInterface() || !(isPotentialSetter || isPotentialGetter) || (!(
                                    // any getter/setter on BaseProcessorExtension should not be exposed (would drag in a lot of classes)
                                    // one class incorrectly extends from BaseProcessorExtension though
                                    (ProcessorExtension.class.isAssignableFrom(curCls)
                                            && !JscriptWorkflowTask.class.isAssignableFrom(curCls))
                                            || org.springframework.extensions.surf.core.processor.ProcessorExtension.class
                                                    .isAssignableFrom(curCls))
                                            && !(
                                            // anything with typical names hinting at complex beans should not be exposed (would drag in a
                                            // lot
                                            // of
                                            // classes)
                                            methodName.matches(
                                                    "^[gs]et([A-Z].*)*(Service|DAO|Helper|Cache|Exception|Factory|Provider|Component).*$")
                                                    || methodName.matches("^[gs]etRepository([A-Z].*)*$")))))
                            {
                                if (!methodsByNameAndParameterTypes.containsKey(key))
                                {
                                    methodsByNameAndParameterTypes.put(key, declaredMethod);
                                }
                            }
                        }
                    }
                }
            }
        }

        final List<Method> documentableMethods = new ArrayList<>(methodsByNameAndParameterTypes.values());
        Collections.sort(documentableMethods, (a, b) -> {
            final String aName = a.getName();
            final String bName = b.getName();

            int result = aName.compareTo(bName);
            if (result == 0)
            {
                final Class<?>[] aParamTypes = a.getParameterTypes();
                final Class<?>[] bParamTypes = b.getParameterTypes();

                if (aParamTypes.length != bParamTypes.length)
                {
                    result = aParamTypes.length - bParamTypes.length;
                }
                else
                {
                    // last chance for reproducible order: compare each pair of parameter types
                    int idx = 0;
                    while (result == 0 && idx < aParamTypes.length)
                    {
                        result = aParamTypes[idx].getName().compareTo(bParamTypes[idx].getName());
                        idx++;
                    }
                }
            }

            return result;
        });

        return documentableMethods;
    }

    protected Map<String, Object> buildClassMethodMemberDefinition(final Class<?> cls, final Method method, final String commonPrefix,
            final Collection<Class<?>> relatedClasses)
    {
        final Map<String, Object> memberDefinition;

        final Class<?> realReturnType = method.getReturnType();
        final Class<?>[] realParameterTypes = method.getParameterTypes();

        final String methodName = method.getName();
        final String simpleMethodPrefix = commonPrefix + "." + methodName;
        final String methodPrefix = this.buildMethodPrefix(commonPrefix, realReturnType, realParameterTypes, methodName);

        String skip = this.properties.getProperty(methodPrefix + ".skip");
        if (skip == null || skip.isEmpty())
        {
            skip = this.properties.getProperty(simpleMethodPrefix + ".skip");
        }

        if (!Boolean.parseBoolean(skip))
        {
            memberDefinition = new HashMap<>();
            final Class<?> effectiveReturnType = this.determineEffectiveType(realReturnType, methodPrefix);
            final Class<?> returnType = this.determineType(effectiveReturnType, relatedClasses);

            final Class<?>[] effectiveParameterTypes = new Class<?>[realParameterTypes.length];
            final Class<?>[] parameterTypes = new Class<?>[realParameterTypes.length];

            for (int idx = 0; idx < parameterTypes.length; idx++)
            {
                effectiveParameterTypes[idx] = this.determineEffectiveType(realParameterTypes[idx], methodPrefix + ".arg" + idx);
                parameterTypes[idx] = this.determineType(effectiveParameterTypes[idx], relatedClasses);
            }

            memberDefinition.put("name", methodName);

            final String methodType = this.buildMethodTypeDescription(method, realReturnType, effectiveReturnType, returnType,
                    effectiveParameterTypes, parameterTypes, methodPrefix);
            memberDefinition.put("type", methodType);

            this.findAndAddTernDoc(methodPrefix, memberDefinition);
        }
        else
        {
            memberDefinition = null;
        }

        return memberDefinition;
    }

    protected Map<String, Object> buildClassPropertyMemberDefinition(final Class<?> cls, final Method getter, final String commonPrefix,
            final String propertyName, final Collection<Class<?>> relatedClasses)
    {
        final Map<String, Object> memberDefinition;

        final String propertyPrefix = commonPrefix + "." + propertyName;

        final Class<?> realReturnType = getter.getReturnType();
        final Class<?> effectiveReturnType = this.determineEffectiveType(realReturnType, propertyPrefix);

        final String skip = this.properties.getProperty(propertyPrefix + ".skip");

        if (!Boolean.parseBoolean(skip))
        {
            memberDefinition = new HashMap<>();
            final Class<?> returnType = this.determineType(effectiveReturnType, relatedClasses);

            String returnTypeName = this.properties.getProperty(propertyPrefix + ".typeTernName");
            if (returnTypeName == null || returnTypeName.isEmpty())
            {
                returnTypeName = this.findTernName(returnType, "returnTypeTernName");
            }

            if (effectiveReturnType.isArray() && !(returnTypeName.startsWith("[") && returnTypeName.endsWith("[")))
            {
                returnTypeName = "[" + returnTypeName + "]";
            }

            memberDefinition.put("name", propertyName);
            memberDefinition.put("type", returnTypeName);

            this.findAndAddTernDoc(propertyPrefix, memberDefinition);

            boolean readOnly = true;
            final String setterName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.ENGLISH) + propertyName.substring(1);
            // first attempt
            try
            {
                cls.getMethod(setterName, realReturnType);
                readOnly = false;
            }
            catch (final NoSuchMethodException nsmex)
            {
                // alternative attempt
                if (!realReturnType.equals(effectiveReturnType))
                {
                    try
                    {
                        cls.getMethod(setterName, effectiveReturnType);
                        readOnly = false;
                    }
                    catch (final NoSuchMethodException ignore)
                    {
                    }
                }
            }
            memberDefinition.put("readOnly", Boolean.valueOf(readOnly));
        }
        else
        {
            memberDefinition = null;
        }

        return memberDefinition;
    }

    protected Class<?> determineEffectiveType(final Class<?> realType, final String prefix)
    {
        Class<?> effectiveType = realType;

        if (!(void.class.equals(realType) || Void.class.equals(realType)))
        {
            // due to bad return type in Java code we may need to specify overrides on a per-use-case level
            final String typeClassName = this.properties.getProperty(prefix + ".typeClassName");
            if (typeClassName != null && !typeClassName.isEmpty())
            {
                try
                {
                    effectiveType = Class.forName(typeClassName);
                }
                catch (final ClassNotFoundException cnfex)
                {
                    LOGGER.warn("Failed to find class {} configured as replacement for {}", typeClassName, realType);
                }
            }
        }

        return effectiveType;
    }

    protected Class<?> determineType(final Class<?> inType, final Collection<Class<?>> relatedClasses)
    {
        Class<?> type = inType;
        if (PRIMITIVE_NUMBER_CLASSES.contains(type) || Number.class.isAssignableFrom(type))
        {
            type = Number.class;
        }
        else if (boolean.class.equals(type))
        {
            type = Boolean.class;
        }
        else if (char.class.equals(type))
        {
            type = Character.class;
        }
        else if (Date.class.isAssignableFrom(type))
        {
            type = Date.class;
        }
        else if (CharSequence.class.equals(type))
        {
            type = String.class;
        }
        else if (type.isArray())
        {
            type = this.determineType(type.getComponentType(), relatedClasses);
        }
        else if (Map.class.isAssignableFrom(type) && !Scriptable.class.isAssignableFrom(type))
        {
            type = Map.class;
        }
        else if (List.class.isAssignableFrom(type))
        {
            type = List.class;
        }
        else if (Set.class.isAssignableFrom(type))
        {
            type = Set.class;
        }
        else if (!(void.class.equals(type) || Void.class.equals(type)))
        {
            relatedClasses.add(type);
        }
        return type;
    }

    protected String buildMethodPrefix(final String commonPrefix, final Class<?> realReturnType, final Class<?>[] realParameterTypes,
            final String methodName)
    {
        final StringBuilder signatureKeyBuilder = new StringBuilder();
        if (!(void.class.equals(realReturnType) || Void.class.equals(realReturnType)))
        {
            signatureKeyBuilder.append(".out.");
            final String realReturnTypeName = realReturnType.getName();
            signatureKeyBuilder.append(realReturnTypeName.substring(realReturnTypeName.lastIndexOf('.') + 1));
        }
        if (realParameterTypes.length > 0)
        {
            signatureKeyBuilder.append(".in");
            for (final Class<?> realParameterType : realParameterTypes)
            {
                signatureKeyBuilder.append(".");
                final String realParameterTypeName = realParameterType.getName();
                signatureKeyBuilder.append(realParameterTypeName.substring(realParameterTypeName.lastIndexOf('.') + 1));
            }
        }
        final String methodPrefix = commonPrefix + "." + methodName + signatureKeyBuilder;
        return methodPrefix;
    }

    protected String buildMethodTypeDescription(final Method method, final Class<?> realReturnType, final Class<?> effectiveReturnType,
            final Class<?> returnType, final Class<?>[] effectiveParameterTypes, final Class<?>[] parameterTypes, final String methodPrefix)
    {
        final StringBuilder typeBuilder = new StringBuilder();
        typeBuilder.append("fn(");
        if (parameterTypes.length > 0)
        {
            final String[] parameterNames = PARAMETER_NAME_DISCOVERER != null ? PARAMETER_NAME_DISCOVERER.getParameterNames(method) : null;

            for (int idx = 0; idx < parameterTypes.length; idx++)
            {
                String parameterName = this.properties.getProperty(methodPrefix + ".arg" + idx + ".name");
                if ((parameterName == null || parameterName.isEmpty()) && parameterNames != null)
                {
                    parameterName = parameterNames[idx];
                }
                if (parameterName == null || parameterName.isEmpty())
                {
                    parameterName = "arg" + idx;
                }

                String typeName = this.properties.getProperty(methodPrefix + "." + parameterName + ".typeTernName");
                if ((typeName == null || typeName.isEmpty()) && !parameterName.equals("arg" + idx))
                {
                    typeName = this.properties.getProperty(methodPrefix + ".arg" + idx + ".typeTernName");
                }

                if (typeName == null || typeName.isEmpty())
                {
                    typeName = this.findTernName(parameterTypes[idx], "paramTypeTernName");
                }

                if (effectiveParameterTypes[idx].isArray() && !(typeName.startsWith("[") && typeName.endsWith("[")))
                {
                    typeName = "[" + typeName + "]";
                }

                if (idx != 0)
                {
                    typeBuilder.append(", ");
                }

                typeBuilder.append(parameterName);
                typeBuilder.append(": ");
                typeBuilder.append(typeName);
            }
        }
        typeBuilder.append(")");
        if (!(void.class.equals(realReturnType) || Void.class.equals(realReturnType)))
        {
            typeBuilder.append(" -> ");

            String returnTypeName = this.properties.getProperty(methodPrefix + ".returnTypeTernName");
            if (returnTypeName == null || returnTypeName.isEmpty())
            {
                returnTypeName = this.findTernName(returnType, "returnTypeTernName");
            }

            if (effectiveReturnType.isArray() && !(returnTypeName.startsWith("[") && returnTypeName.endsWith("[")))
            {
                returnTypeName = "[" + returnTypeName + "]";
            }

            typeBuilder.append(returnTypeName);
        }
        final String methodType = typeBuilder.toString();
        return methodType;
    }

    protected Map<String, Object> buildScriptAPIModel()
    {
        final String userName = AuthenticationUtil.getFullyAuthenticatedUser();
        final NodeRef person = this.personService.getPerson(userName);
        // the specific values of companyHome, userHome, script, document and space are irrelevant for type analysis
        final Map<String, Object> defaultModel = this.scriptService.buildDefaultModel(person, person, person, person, person, person);

        final Collection<ProcessorExtension> processorExtensions = this.scriptProcessor.getProcessorExtensions();
        for (final ProcessorExtension extension : processorExtensions)
        {
            if (!defaultModel.containsKey(extension.getExtensionName()))
            {
                defaultModel.put(extension.getExtensionName(), extension);
            }
        }

        // add our extensions only available in the JS Console context
        final JavascriptConsoleScriptObject javascriptConsoleScriptObject = new JavascriptConsoleScriptObject();
        defaultModel.put("jsconsole", javascriptConsoleScriptObject);
        defaultModel.put("logger", javascriptConsoleScriptObject.getLogger());
        defaultModel.put("dumpService", new DumpService());

        return defaultModel;
    }

    /**
     * Prepares the definitions of node and task properties based on the {@link DictionaryService dictionary} active in the current context
     * (authentication / tenant)
     *
     * @param model
     *     the model into which to insert the definitions
     */
    protected void preparePropertyDefinitions(final Map<String, Object> model)
    {
        final Collection<QName> taskTypes = this.dictionaryService.getSubTypes(WorkflowModel.TYPE_TASK, true);
        final Collection<QName> taskAspects = this.collectTaskAspects(taskTypes);

        model.put("taskProperties", this.buildPropertyDefinitions(taskTypes, taskAspects));

        // though task types could technically be used for nodes (task derives from cm:content) they rarely are
        final Collection<QName> nodeTypes = new TreeSet<>(this.dictionaryService.getAllTypes());
        nodeTypes.removeAll(nodeTypes);
        final Collection<QName> nodeAspects = this.dictionaryService.getAllAspects();

        model.put("nodeProperties", this.buildPropertyDefinitions(nodeTypes, nodeAspects));
    }

    protected List<PropertyDefinition> buildPropertyDefinitions(final Collection<QName> types, final Collection<QName> aspects)
    {
        final List<PropertyDefinition> propertyDefinitions = new ArrayList<>();

        final Collection<QName> allClasses = new TreeSet<>();
        allClasses.addAll(types);
        allClasses.addAll(aspects);

        for (final QName cls : allClasses)
        {
            propertyDefinitions.addAll(this.buildPropertyDefinitions(cls));
        }

        return propertyDefinitions;
    }

    protected List<PropertyDefinition> buildPropertyDefinitions(final QName cls)
    {
        final ClassDefinition classDefinition = this.dictionaryService.getClass(cls);

        final Map<QName, PropertyDefinition> properties = classDefinition.getProperties();
        final Map<QName, PropertyDefinition> parentProperties = classDefinition.getParentName() != null
                ? classDefinition.getParentClassDefinition().getProperties()
                : Collections.<QName, PropertyDefinition> emptyMap();

        final List<PropertyDefinition> propertyDefinitions = new ArrayList<>();

        for (final Entry<QName, PropertyDefinition> propertyEntry : properties.entrySet())
        {
            if (!parentProperties.containsKey(propertyEntry.getKey()))
            {
                propertyDefinitions.add(propertyEntry.getValue());
            }
        }

        return propertyDefinitions;
    }

    protected Collection<QName> collectTaskAspects(final Collection<QName> taskTypes)
    {
        final Collection<QName> taskAspects = new TreeSet<>();
        for (final QName taskType : taskTypes)
        {
            final TypeDefinition type = this.dictionaryService.getType(taskType);
            taskAspects.addAll(type.getDefaultAspectNames());
        }

        boolean taskAspectsChanged = true;
        while (taskAspectsChanged)
        {
            taskAspectsChanged = false;
            for (final QName taskAspect : taskAspects)
            {
                final AspectDefinition aspect = this.dictionaryService.getAspect(taskAspect);
                taskAspectsChanged = taskAspects.addAll(aspect.getDefaultAspectNames()) || taskAspectsChanged;
                if (aspect.getParentName() != null)
                {
                    taskAspectsChanged = taskAspects.add(aspect.getParentName()) || taskAspectsChanged;
                }
            }
        }

        return taskAspects;
    }

    protected void findAndAddTernDoc(final String prefix, final Map<String, Object> definition)
    {
        // support I18n for any documentation
        final String i18nKey = "javascript-console.tern." + definition + ".ternDoc";
        String ternDoc = I18NUtil.getMessage(i18nKey);
        if (ternDoc == null || ternDoc.isEmpty() || ternDoc.equals(i18nKey))
        {
            ternDoc = this.properties.getProperty(definition + ".ternDoc");
        }
        if (ternDoc != null && !ternDoc.isEmpty())
        {
            definition.put("doc", ternDoc);
        }
    }

    protected String findTernName(final Class<?> cls)
    {
        return this.findTernName(cls, null);
    }

    protected String findTernName(final Class<?> cls, final String preferredTernConfigKey)
    {
        String ternName = cls.getName();
        final String prefix = "type." + ternName;
        ternName = ternName.substring(ternName.lastIndexOf('.') + 1);

        if (preferredTernConfigKey != null && !preferredTernConfigKey.isEmpty())
        {
            final String preferredTernName = this.properties.getProperty(prefix + "." + preferredTernConfigKey);
            if (preferredTernName != null && !preferredTernName.isEmpty())
            {
                ternName = preferredTernName;
            }
            else
            {
                final String typeTernName = this.properties.getProperty(prefix + ".ternName");
                if (typeTernName != null && !typeTernName.isEmpty())
                {
                    ternName = typeTernName;
                }
            }
        }
        else
        {
            final String typeTernName = this.properties.getProperty(prefix + ".ternName");
            if (typeTernName != null && !typeTernName.isEmpty())
            {
                ternName = typeTernName;
            }
        }

        return ternName;
    }

    protected boolean isMemberSkipped(final Class<?> cls, final String memberName, final boolean checkTypeSkipAndNameOnly)
    {
        final String prefix = "type." + cls.getName();
        String skip = this.properties.getProperty(prefix + "." + memberName + ".skip");
        if (checkTypeSkipAndNameOnly && (skip == null || skip.isEmpty()))
        {
            skip = this.properties.getProperty(prefix + ".skip");
        }
        if (checkTypeSkipAndNameOnly && (skip == null || skip.isEmpty()))
        {
            skip = this.properties.getProperty(prefix + ".nameOnly");
        }

        return Boolean.parseBoolean(skip);
    }

    protected boolean isMemberSkipped(final Class<?> cls, final Method method, final boolean checkTypeSkipAndNameOnly)
    {
        final Class<?> realReturnType = method.getReturnType();

        final Class<?>[] realParameterTypes = method.getParameterTypes();

        final String methodName = method.getName();
        final String commonPrefix = "type." + cls.getName();
        final String simpleMethodPrefix = commonPrefix + "." + methodName;
        final String methodPrefix = this.buildMethodPrefix(commonPrefix, realReturnType, realParameterTypes, methodName);

        String skip = this.properties.getProperty(methodPrefix + ".skip");
        if (skip == null || skip.isEmpty())
        {
            skip = this.properties.getProperty(simpleMethodPrefix + ".skip");
        }
        if (checkTypeSkipAndNameOnly && (skip == null || skip.isEmpty()))
        {
            skip = this.properties.getProperty(commonPrefix + ".skip");
        }
        if (checkTypeSkipAndNameOnly && (skip == null || skip.isEmpty()))
        {
            skip = this.properties.getProperty(commonPrefix + ".nameOnly");
        }

        return Boolean.parseBoolean(skip);
    }
}