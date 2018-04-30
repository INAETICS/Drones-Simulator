package org.inaetics.drone.simulator.gameengine;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import java.util.Properties;

/**
 * Created by michiel on 20-4-18.
 */
public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext ctx, DependencyManager dm) throws Exception {
        dm.add(createComponent()
            .setInterface(IGameEngine.class.getName(), new Properties())
            .setImplementation(GameEngine.class)
            .add(dm.createServiceDependency()
                .setService(LogService.class)));
    }
}
