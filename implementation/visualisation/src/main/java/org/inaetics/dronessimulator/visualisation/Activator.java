package org.inaetics.dronessimulator.visualisation;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

/**
 * Created by langstra on 7-4-17.
 */
public class Activator extends DependencyActivatorBase {

    @Override
    public void init(BundleContext context, DependencyManager manager) throws Exception {
        manager.add(createComponent()
                .setImplementation(Game.class)
                .add(createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
                )
        );
    }

}
