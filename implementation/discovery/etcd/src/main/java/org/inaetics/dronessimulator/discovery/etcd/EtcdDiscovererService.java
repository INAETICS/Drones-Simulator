package org.inaetics.dronessimulator.discovery.etcd;

import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service for the etcd discoverer. Combines the normal discoverer and the threads to allow waiting for changes.
 */
public class EtcdDiscovererService implements Discoverer {
    /** Config discoverer thread. */
    private Thread configDiscoverer;

    /** Actual discoverer implementation. */
    private EtcdDiscoverer discoverer;

    /** Configuration admin to use. */
    private volatile ConfigurationAdmin m_configurationAdmin;

    private EtcdChangeHandler changeHandler;

    /**
     * Starts the service by starting the threads that allow for waiting on changes.
     */
    public void start() {
        // TODO: Make this URI dynamic
        this.discoverer = new EtcdDiscoverer(URI.create("http://localhost:4001/"));
        // Run config discoverer
        EtcdConfigDiscoverer configDiscoverer = new EtcdConfigDiscoverer(discoverer, m_configurationAdmin);
        this.configDiscoverer = new Thread(configDiscoverer);
        this.configDiscoverer.start();

        this.changeHandler = new EtcdChangeHandler(discoverer);
        this.changeHandler.start();
    }

    /**
     * Stops the service and any related threads.
     */
    public void stop() {
        this.configDiscoverer.interrupt();
        try {
            this.configDiscoverer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.changeHandler.quit();
        try {
            this.changeHandler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(Instance instance) throws DuplicateName, IOException {
        this.discoverer.register(instance);
    }

    @Override
    public void unregister(Instance instance) throws IOException {
        this.discoverer.unregister(instance);
    }

    @Override
    public Map<String, Collection<String>> find(String type) {
        return this.discoverer.find(type);
    }

    @Override
    public Map<String, Collection<String>> waitFor(String type) {
        return this.discoverer.waitFor(type);
    }

    @Override
    public Collection<String> find(String type, String group) {
        return this.discoverer.find(type, group);
    }

    @Override
    public Collection<String> waitFor(String type, String group) {
        return this.discoverer.waitFor(type, group);
    }

    @Override
    public Map<String, String> getProperties(String type, String group, String name) {
        return this.discoverer.getProperties(type, group, name);
    }

    @Override
    public void addHandlers(boolean replay, List<NodeEventHandler<AddedNode>> addHandlers
                                          , List<NodeEventHandler<ChangedValue>> changedValueHandlers
                                          , List<NodeEventHandler<RemovedNode>> removedHandlers) {
        this.changeHandler.addHandlers(replay, addHandlers, changedValueHandlers, removedHandlers);
    }
}
