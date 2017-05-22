package org.inaetics.dronessimulator.gameengine.identifiermapper;

import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(createComponent()
                                .setInterface(IdentifierMapper.class.getName(), null)
                                .setImplementation(IdentifierMapperService.class)
        );
    }
}
