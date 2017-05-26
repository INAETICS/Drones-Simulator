package org.inaetics.dronessimulator.discovery.etcd;

import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * Service for the etcd discoverer. Combines the normal discoverer and the threads to allow waiting for changes.
 */
public class EtcdDiscovererService implements Discoverer {
    private static final Logger logger = Logger.getLogger(EtcdDiscovererService.class);

    /** Actual discoverer implementation. */
    private EtcdDiscoverer discoverer;

    /** Configuration admin to use. */
    private volatile ConfigurationAdmin m_configurationAdmin;

    /** The etcd change handler to use. */
    private EtcdChangeHandler changeHandler;

    /**
     * Starts the service by starting the threads that allow for waiting on changes.
     */
    public void start() {
        // TODO: Make this URI dynamic
        this.discoverer = new EtcdDiscoverer(URI.create("http://localhost:4001/"));

        this.changeHandler = new EtcdChangeHandler(discoverer);
        this.changeHandler.start();

        this.addConfigAdminHandlers();
    }

    /**
     * Stops the service and any related threads.
     */
    public void stop() {
        this.changeHandler.quit();
        this.discoverer.closeConnection();

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
    public void addHandlers(boolean replay, List<NodeEventHandler<AddedNode>> addHandlers
                                          , List<NodeEventHandler<ChangedValue>> changedValueHandlers
                                          , List<NodeEventHandler<RemovedNode>> removedHandlers) {
        this.changeHandler.addHandlers(replay, addHandlers, changedValueHandlers, removedHandlers);
    }

    /**
     * Adds handlers for populating the config admin.
     */
    public void addConfigAdminHandlers() {
        List<NodeEventHandler<AddedNode>> addHandlers = new ArrayList<>();
        List<NodeEventHandler<ChangedValue>> changedValueHandlers = new ArrayList<>();
        List<NodeEventHandler<RemovedNode>> removedHandlers = new ArrayList<>();

        // Register when added
        addHandlers.add((AddedNode event) -> {
            DiscoveryNode node = event.getNode();
            DiscoveryPath path = node.getPath();

            // Only do something with config paths
            if (path.isConfigPath()) {
                try {
                    Configuration config = this.getConfig(path);
                    logger.debug("Registering configuration {}", config.getPid());
                    config.update(new Hashtable<>(node.getValues()));
                } catch (IOException e) {
                    logger.error("Error while registering configuration {}", path.toString());
                }
            }
        });

        // Register when changed
        changedValueHandlers.add((ChangedValue event) -> {
            DiscoveryNode node = event.getNode();
            DiscoveryPath path = node.getPath();

            // Only do something with config paths
            if (path.isConfigPath()) {
                try {
                    Configuration config = this.getConfig(path);
                    logger.debug("Updating configuration {}", config.getPid());
                    config.getProperties().put(event.getKey(), event.getNewValue());
                } catch (IOException e) {
                    logger.error("Error while updating configuration {}", path.toString());
                }
            }
        });

        // Register when removed
        removedHandlers.add((RemovedNode event) -> {
            DiscoveryNode node = event.getNode();
            DiscoveryPath path = node.getPath();

            // Only do something with config paths
            if (path.isConfigPath()) {
                try {
                    Configuration config = this.getConfig(path);
                    logger.debug("Removing configuration {}", config.getPid());
                    config.delete();
                } catch (IOException e) {
                    logger.error("Error while removing configuration {}", path.toString());
                }
            }
        });

        // Actually add handlers, replay to populate config admin
        this.addHandlers(true, addHandlers, changedValueHandlers, removedHandlers);
    }

    /**
     * Gets the config admin configuration for the given path.
     * @param path The path.
     * @return The configuration.
     */
    protected Configuration getConfig(DiscoveryPath path) throws IOException {
        int pathLength = path.getSegments().length;
        String[] pidSegments = Arrays.copyOfRange(path.getSegments(), pathLength - 3, pathLength);
        String pid = String.join(".", pidSegments);
        return this.m_configurationAdmin.getConfiguration(pid, "?");
    }
}
