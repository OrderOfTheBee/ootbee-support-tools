[![Build Status](https://travis-ci.org/OrderOfTheBee/ootbee-support-tools.svg?branch=master)](https://travis-ci.org/OrderOfTheBee/ootbee-support-tools)

# OOTBee Support Tools
This addon aims to enhance the scope and functionality available to Alfresco administrators via the Repository-tier Admin Console or Share-tier Admin Tools. It contains most of the tools provided with the [Alfresco Support Tools](https://github.com/Alfresco/alfresco-support-tools) addon (by Antonio Soler) without requiring to be run on any specific Alfresco edition as well as about half a dozen custom tools.

The project started as a project at the [2016 Global Virtual Hack-a-thon](https://community.alfresco.com/docs/DOC-6364-projects-and-teams-global-virtual-hackathon-2016) and has since been transferred to the Order of the Bee in order to make it a fully community-owned and -maintained project. 

# Compatibility

This project has been built to be compatible with Alfresco Community 5.0.d+ and Alfresco Enterprise 5.1+.

Though it can technically be installed in Alfresco Enterprise 5.0 it will not work properly in that version as the Enterprise Administration Console cannot handle Community Edition tools. The tools will be listed in the navigation but cannot be accessed (result in HTTP 404 errors due to hardcoded URL patterns).

# Maven usage

This addon is being build using Alfresco SDK 2.2.0. This means we produce an AMP artifact that can be added to an Alfresco all-in-one / WAR build with the as a dependency:

## Using SNAPSHOT builds

In order to use a pre-built SNAPSHOT artifact published to the Open Source Sonatype Repository Hosting site, the artifact repository may need to be added to the POM, global settings.xml or an artifact repository proxy server. The following is the XML snippet for inclusion in a POM file.

```xml
<repositories>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

## Repository-tier

```xml
<dependency>
    <groupId>org.orderofthebee.support-tools</groupId>
    <artifactId>support-tools-repo</artifactId>
    <version>1.0.0.0</version>
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
                <groupId>org.orderofthebee.support-tools</groupId>
                <artifactId>support-tools-repo</artifactId>
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
        <groupId>org.orderofthebee.support-tools</groupId>
        <artifactId>support-tools-repo</artifactId>
        <version>1.0.0.0</version>
        <type>amp</type>
    </moduleDependency>
</platformModules>
```

## Share-tier

The Admin Tools added to the Share user interface are built on Aikau. We recommend that one of the most recent releases of Aikau is used to run the tools for optimal performance, but technically we are compatible and have verified releases as far back as 1.0.67.

```xml
<dependency>
    <groupId>org.orderofthebee.support-tools</groupId>
    <artifactId>support-tools-share</artifactId>
    <version>1.0.0.0</version>
    <type>amp</type>
</dependency>
<dependency>
    <groupId>org.alfresco</groupId>
    <artifactId>aikau</artifactId>
    <version>1.0.101</version>
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
                <artifactId>${alfresco.share.artifactId}</artifactId>
                <type>war</type>
                <excludes />
            </overlay>
            <!-- other AMPs -->
            <overlay>
                <groupId>org.orderofthebee.support-tools</groupId>
                <artifactId>support-tools-share</artifactId>
                <type>amp</type>
            </overlay>
        </overlays>
    </configuration>
</plugin>
``` 

For Alfresco SDK 3 beta users:

```xml
<shareModules>
    <moduleDependency>
        <groupId>org.orderofthebee.support-tools</groupId>
        <artifactId>support-tools-share</artifactId>
        <version>1.0.0.0</version>
        <type>amp</type>
    </moduleDependency>
</shareModules>
```

## Artifact Repository and Building

Currently this addon is not yet published to an artifact repository, so before you can use it you need to clone and build it locally using:

```
mvn install
```
# Contributing
We hope to have lots of collaborators on this project. As such, we have outlined our contribution policies and proceedures in the [CONTRIBUTING.md](./CONTRIBUTING.md) document.

# Code of conduct
It is a key goal of our project to foster active participation and collaboration with the greater Alfresco community. To that end we expect folks to conform to our [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md).

# License
This addon is licensed under the GNU Lesser General Public License (LGPL) similarily to the original work by Antonio Soler. See [LICENSE.md](./LICENSE.md) for the full LGPL license.

Alfresco (base software) - Copyright &copy; Alfresco Software Ltd.

The Contributor Covenant is released under [Creative Commons Attribution 4.0 International Public License](https://github.com/ContributorCovenant/contributor_covenant/blob/master/LICENSE.md).

Original authors:

- [Axel Faust](https://github.com/AFaust), Acosix / Order of the Bee
- [Markus Joos](https://github.com/mrksjs), AdNovum

Maintainers:

- [Axel Faust](https://github.com/AFaust), Acosix / Order of the Bee
- [Ahmed Owian](https://github.com/ahmedowian)
- [Mittal Patolyia](https://github.com/mits2013)
- [Bindu Wavell](https://github.com/binduwavell), Zia / Order of the Bee
