import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.osgi.framework.BundleContext;

public class Activator extends DependencyActivatorBase {
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        dependencyManager.add(
            createComponent()
            .setInterface(ArchitectureEventListener.class.getName(), null)
            .setImplementation(ArchitectureEventListenerService.class)
            .add(
                createServiceDependency()
                .setService(Discoverer.class)
                .setRequired(true)
            )
        );
    }
}
