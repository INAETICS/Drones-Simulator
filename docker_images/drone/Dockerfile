FROM dronesim/base

ADD files/dependencies/*.jar /opt/felix/current/bundle/
ADD files/bundles/*.jar /opt/felix/current/bundle/

# You can override these at runtime, and you are encouraged to turn off debugger support in production
ENV JVM_OPTIONS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

CMD exec java $JVM_OPTIONS -jar bin/felix.jar