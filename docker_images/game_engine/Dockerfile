FROM openjdk:8-alpine
MAINTAINER Shawn Dempsay <sdempsay@pavlovmedia.com>

# Install Felix
ENV felix_version 5.6.0
ENV felix_package=org.apache.felix.main.distribution-${felix_version}.tar.gz
ENV felix_base http://repo1.maven.org/maven2/org/apache/felix

ADD ${felix_base}/org.apache.felix.main.distribution/${felix_version}/${felix_package} /tmp/
RUN mkdir -p /opt/felix && \
    cd /opt/felix && \
    tar xvzf /tmp/${felix_package} && \
    ln -s /opt/felix/felix-framework-${felix_version} /opt/felix/current

# We set up configuration here so that our obr installs go nicely
ADD files/config.properties /opt/felix/current/conf/

#
# Now expose where config manager dumps thigs so we can persist
# across starts
#
RUN mkdir -p /opt/felix/current/configs

#
# Install bundles with OBR
#
WORKDIR /opt/felix/current

#
# Finally expose our webports so you can use this with something like
# https://github.com/jwilder/nginx-proxy
#
EXPOSE 8080 8000
VOLUME ["/opt/felix/current/configs", "/opt/felix/current/load" ]

ADD files/dependencies/*.jar /opt/felix/current/bundle/
ADD files/bundles/*.jar /opt/felix/current/bundle/

# You can override these at runtime, and you are encouraged to turn off debugger support in production
ENV JVM_OPTIONS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

CMD exec java $JVM_OPTIONS -jar bin/felix.jar