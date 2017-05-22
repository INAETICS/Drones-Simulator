package org.inaetics.dronessimulator.drone.tactic;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.osgi.framework.BundleContext;

import javax.xml.ws.Service;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Tactic tactic = new SimpleTactic();


        Component component = createComponent()
                               .setInterface(Tactic.class.getName(), null)
                               .setImplementation(tactic);

        for(ServiceDependency dep : tactic.getComponents(dependencyManager)) {
            component.add(dep);
        }

        //component.add(createServiceDependency()
        //        .setService(DroneInit.class)
        //        .setRequired(true)
        //);
        dependencyManager.add(component);
    }
}