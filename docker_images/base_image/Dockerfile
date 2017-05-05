FROM dockmob/java

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