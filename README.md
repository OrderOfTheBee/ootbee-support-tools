[![Build Status](https://travis-ci.org/AFaust/ootbee-support-tools.svg?branch=master)](https://travis-ci.org/AFaust/ootbee-support-tools)

# "Liberated" Alfresco Support Tools
This addon aims to bring the functionality provided by the [Alfresco Support Tools](https://github.com/Alfresco/alfresco-support-tools) addon by Antonio Soler, which is only supported on the Alfresco Enterprise Edition, to the free and open Community Edition of Alfresco.

# Compatibility

This project has been built to be compatible with Alfresco Community 5.0.d+ and Alfresco Enterprise 5.1+.

Though it can technically be installed in Alfresco Enterprise 5.0 it will not work properly in that version as the Enterprise Administration Console cannot handle Community Edition tools. The tools will be listed in the navigation but cannot be accessed (result in HTTP 404 errors due to hardcoded URL patterns).

In order to avoid conflicts with tools provided by the official [Alfresco Support Tools](https://github.com/Alfresco/alfresco-support-tools) for Alfresco Enterprise Edition most of the tools this module provides have been configured to be "Community-only", that is they will not show up in an Enterprise Edition system. This behaviour can be changed by overriding the web script descriptor XML files via the *&lt;configRoot&gt;/alfresco/extension/templates/webscripts/* method. Specifically all the web script descriptors of [Repository-tier web scripts in this module](https://github.com/AFaust/ootbee-support-tools/tree/master/repository/src/main/amp/config/alfresco/templates/webscripts/org/orderofthebee/ootbee-support-tools/admin) that contain the following fragment need to be overridden and the snippet removed.

```xml
    <!-- would conflict with Alfresco provided Support Tools project -->
    <family>AdminConsole:Edition:Community</family>    
```

# Maven usage

This addon is being build using Alfresco SDK 2.2.0. This means we produce an AMP artifact that can be added to an Alfresco all-in-one / WAR build with the as a dependency:

```xml
<dependency>
    <groupId>org.orderofthebee.addons</groupId>
    <artifactId>ootbee-support-tools-repo</artifactId>
    <version>0.0.1.0-SNAPSHOT</version>
    <type>amp</type>
</dependency>
```

In an all-in-one project you also need to add the AMP as an <overlay> to the maven-war-plugin configuration (usage for custom WAR builds may differ):

```xml
<plugin>
    <artifactId>maven-war-plugin</artifactId>
    <configuration>
        <overlays>
            <overlay />
            <overlay>
                <groupId>${alfresco.groupId}</groupId>
                <artifactId>${alfresco.repo.artifactId}</artifactId>
                <type>war</type>
                <excludes />
            </overlay>
            <!-- other AMPs -->
            <overlay>
                <groupId>org.orderofthebee.addons</groupId>
                <artifactId>ootbee-support-tools-repo</artifactId>
                <type>amp</type>
            </overlay>
        </overlays>
    </configuration>
</plugin>
``` 

For Alfresco SDK 3 beta users:

```xml
<platformModules>
    <moduleDependency>
        <groupId>org.orderofthebee.addons</groupId>
        <artifactId>ootbee-support-tools-repo</artifactId>
        <version>0.0.1.0-SNAPSHOT</version>
        <type>amp</type>
    </moduleDependency>
</platformModules>
```

Currently this addon is not yet published to an artifact repository, so before you can use it you need to clone and build it locally using:

```
mvn install
```
# Contributing
We hope to have lots of collaborators on this project. As such, we have outlined our contribution policies and proceedures in the [CONTRIBUTING.md](./CONTRIBUTING.md) document.

# License
This addon is licensed under the GNU Lesser General Public License (LGPL) similarily to the original work by Antonio Soler. See [LICENSE.md](./LICENSE.md) for the full LGPL license.

Authors:

- [Axel Faust](mailto:axel.faust@acosix.org), Order of the Bee
- Markus Joos, AdNovum

Alfresco (base software) - Copyright &copy; Alfresco Software Ltd.
