[![Build Status](https://travis-ci.org/OrderOfTheBee/ootbee-support-tools.svg?branch=master)](https://travis-ci.org/OrderOfTheBee/ootbee-support-tools)

# OOTBee Support Tools

This addon aims to enhance the scope and functionality available to Alfresco administrators via the Repository-tier Admin Console or Share-tier Admin Tools. It contains most of the tools provided with the [Alfresco Support Tools](https://github.com/Alfresco/alfresco-support-tools) addon (by Antonio Soler) without requiring to be run on any specific Alfresco edition as well as about half a dozen custom tools.

The project started as a project at the [2016 Global Virtual Hack-a-thon](https://community.alfresco.com/docs/DOC-6364-projects-and-teams-global-virtual-hackathon-2016) and has since been transferred to the Order of the Bee in order to make it a fully community-owned and -maintained project. 

# Compatibility

This project has been built to be compatible with Alfresco Community 5.0.d+ and Alfresco Enterprise 5.1+.

Though it can technically be installed in Alfresco Enterprise 5.0 it will not work properly in that version as the Enterprise Administration Console cannot handle Community Edition tools. The tools will be listed in the navigation but cannot be accessed (result in HTTP 404 errors due to hardcoded URL patterns).

# Maven usage

This addon is being build using Alfresco SDK 4. This means we primarily produce a JAR artifact that can be added to an Alfresco installation, though care must be taken to also include all of the third party dependencies required by the OOTBee Support Tools. For users who want a hassle free installation using Alfresco Module Packages (AMPs) we also still produce an AMP for both Repository and Share as we have done in version 1.0.0.0.

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

Including the AMP artifact into an All-in-One project created from the archetype provided by Alfresco SDK 4, the following dependency must be added to the ``*-platform-docker`` sub-module of the generated project:

```xml
<dependency>
    <groupId>org.orderofthebee.support-tools</groupId>
    <artifactId>support-tools-repo</artifactId>
    <version>1.1.0.0</version>
    <type>amp</type>
    <classifier>amp</classifier>
    <exclusions>
        <exclusion>
            <groupId>*</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Share-tier

The Admin Tools added to the Share user interface are built on Aikau. We recommend that one of the most recent releases of Aikau is used to run the tools for optimal performance, but technically we are compatible and have verified releases as far back as 1.0.67.

Including the AMP artifact into an All-in-One project created from the archetype provided by Alfresco SDK 4, the following dependency must be added to the ``*-share-docker`` sub-module of the generated project:

```xml
<dependency>
    <groupId>org.orderofthebee.support-tools</groupId>
    <artifactId>support-tools-share</artifactId>
    <version>1.1.0.0</version>
    <type>amp</type>
    <classifier>amp</classifier>
    <exclusions>
        <exclusion>
            <groupId>*</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Artifact Repository and Building

Releases of this addon are [published to Maven Central](https://search.maven.org/search?q=g:org.orderofthebee.support-tools) so you can use these artifacts in your Maven build without any extra configuration. If you want to use a SNAPSHOT build, clone this project and build it locally using:

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
- [Bindu Wavell](https://github.com/binduwavell), Zia / Order of the Bee
- [Younes Regaieg](https://github.com/yregaieg), Order of the Bee
