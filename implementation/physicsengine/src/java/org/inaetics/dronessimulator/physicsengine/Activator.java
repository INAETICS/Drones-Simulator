package org.inaetics.dronessimulator.physicsengine;


import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.publisher.Publisher;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
            .setImplementation(PhysicsEngineBundle.class)
            .add(createServiceDependency()
                .setService(Publisher.class)
                .setRequired(true)
            )
        );
    }

    @Override
    public void destroy(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Component component = dependencyManager.getComponents().get(0);

        if(component != null) {
            PhysicsEngineBundle bundle = component.getInstance();

            if(bundle != null) {
                bundle.stop();
            }
        }
    }
}
