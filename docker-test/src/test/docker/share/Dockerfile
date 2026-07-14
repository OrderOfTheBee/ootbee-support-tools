FROM ${alfresco.share.docker.image}:${alfresco.share.docker.version}

COPY support-tools-share*.amp ${alfresco.share.docker.amp.path}/

RUN java -jar ${alfresco.share.docker.mmt.path}/alfresco-mmt*.jar install \
              ${alfresco.share.docker.amp.path}/support-tools-share*.amp \
              ${alfresco.share.docker.tomcat.path}/webapps/share -nobackup

RUN sed -i 's/<secure>true<\/secure>/<secure>false<\/secure>/' ${alfresco.share.docker.tomcat.path}/conf/web.xml

COPY share.xml ${alfresco.share.docker.tomcat.path}/conf/Catalina/localhost/share.xml
