FROM ${alfresco.repo.docker.image}:${alfresco.repo.docker.version}

${alfresco.repo.docker.build.preRun}

COPY support-tools-repo*.amp ${alfresco.repo.docker.amp.path}/

RUN java -jar ${alfresco.repo.docker.mmt.path}/alfresco-mmt*.jar install \
              ${alfresco.repo.docker.amp.path}/support-tools-repo*.amp \
              ${alfresco.repo.docker.tomcat.path}/webapps/alfresco -nobackup

COPY alfresco.xml ${alfresco.repo.docker.tomcat.path}/conf/Catalina/localhost/
COPY alfresco-global.addition.properties ${alfresco.repo.docker.tomcat.path}/shared/classes/alfresco/
COPY dev-log4j.properties dev-log4j2.properties ${alfresco.repo.docker.tomcat.path}/shared/classes/alfresco/extension/

# merge additions to alfresco-global.properties
RUN echo "" >> ${alfresco.repo.docker.tomcat.path}/shared/classes/alfresco-global.properties \
    && cat ${alfresco.repo.docker.tomcat.path}/shared/classes/alfresco/alfresco-global.addition.properties >> ${alfresco.repo.docker.tomcat.path}/shared/classes/alfresco-global.properties \
    && sed -i 's/<secure>true<\/secure>/<secure>false<\/secure>/' ${alfresco.repo.docker.tomcat.path}/conf/web.xml

RUN chown -R ${alfresco.repo.docker.chown.mask} \
             ${alfresco.repo.docker.tomcat.path}/webapps/alfresco \
             ${alfresco.repo.docker.tomcat.path}/shared/classes/*

${alfresco.repo.docker.build.postRun}
