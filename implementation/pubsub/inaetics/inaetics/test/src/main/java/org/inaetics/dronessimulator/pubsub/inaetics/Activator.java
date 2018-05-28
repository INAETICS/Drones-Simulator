package org.inaetics.dronessimulator.pubsub.inaetics;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Dictionary;
import java.util.Hashtable;

public class Activator extends DependencyActivatorBase {


    @Override
    public void init(BundleContext bundleContext, DependencyManager dm) throws Exception {
        String[] objectClass = new String[] {Object.class.getName()};
        Dictionary<String, Object> properties = new Hashtable<>();

        dm.add(
                dm.createComponent()
                        .setInterface(objectClass, properties)
                        .setImplementation(Demo.class)
                        .add(createServiceDependency()
                                .setService(LogService.class)
                                .setRequired(false))
        );
    }
}
