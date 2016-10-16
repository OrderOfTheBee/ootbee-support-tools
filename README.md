# "Liberated" Alfresco Support Tools
This addon aims to bring the functionality provided by the [Alfresco Support Tools](https://github.com/Alfresco/alfresco-support-tools) addon by Antonio Soler, which is only supported on the Alfresco Enterprise Edition, to the free and open Community Edition of Alfresco.

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

# License
This addon is licensed under the GNU Lesser General Public License (LGPL) similarily to the original work by Antonio Soler. See [LICENSE.md](./LICENSE.md) for the full LGPL license.

Authors:

- [Axel Faust](mailto:axel.faust@acosix.org), Order of the Bee
- Markus Joos, AdNovum

Alfresco (base software) - Copyright &copy; Alfresco Software Ltd.