FROM dockmob/java

# Install Felix
ENV FELIX_VERSION 5.6.0
ENV FELIX_PACKAGE=org.apache.felix.main.distribution-${FELIX_VERSION}.tar.gz
ENV FELIX_BASE http://repo1.maven.org/maven2/org/apache/felix

ADD ${FELIX_BASE}/org.apache.felix.main.distribution/${FELIX_VERSION}/${FELIX_PACKAGE} /tmp/
RUN mkdir -p /opt/felix && \
    cd /opt/felix && \
    tar xvzf /tmp/${FELIX_PACKAGE} && \
    ln -s /opt/felix/felix-framework-${FELIX_VERSION} /opt/felix/current

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
