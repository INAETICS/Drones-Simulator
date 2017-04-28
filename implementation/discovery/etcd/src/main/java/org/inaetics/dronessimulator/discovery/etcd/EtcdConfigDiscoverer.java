package org.inaetics.dronessimulator.discovery.etcd;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyManager;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.DiscoveredConfig;

import java.util.*;

/**
 * Discovers configurations and publishes these as services.
 */
public class EtcdConfigDiscoverer implements Runnable {
    private static final Logger logger = Logger.getLogger(EtcdConfigDiscoverer.class);

    /** The discoverer used to find configs. */
    private EtcdDiscoverer discoverer;

    /** The dependency manager used to register config services. */
    private DependencyManager dependencyManager;

    /** Cache of registered configs. */
    private Map<String, DiscoveredConfig> configs;

    /** Mapping of Felix component per config. */
    private Map<DiscoveredConfig, Component> components;

    /**
     * Instantiates a new config discoverer.
     * @param discoverer The etcd discoverer to use to find configs.
     * @param dependencyManager The dependency manager to use to register config services.
     */
    public EtcdConfigDiscoverer(EtcdDiscoverer discoverer, DependencyManager dependencyManager) {
        this.discoverer = discoverer;
        this.dependencyManager = dependencyManager;

        // Initialize some fields
        this.configs = new HashMap<>();
        this.components = new HashMap<>();
    }

    @Override
    public void run() {
        logger.info("Started etcd config discoverer");

        Collection<String> currentPaths = this.configs.keySet();

        while(!Thread.interrupted()) {
            // Wait for update in directory
            Collection<String> collectedPaths = this.discoverer.getDiscoverableConfigs(true);
            Collection<String> newPaths = new HashSet<>();
            Collection<String> stalePaths = new HashSet<>(currentPaths);

            // Process paths
            for (String path : collectedPaths) {
                if (this.configs.containsKey(path)) {
                    stalePaths.remove(path);
                } else {
                    newPaths.add(path);
                }
            }

            newPaths.forEach(this::registerInstance);
            stalePaths.forEach(this::unregisterInstance);

            // Update paths
            currentPaths = this.configs.keySet();
        }

        logger.info("Stopping etcd config discoverer");
    }

    /**
     * Registers a new discovered config with properties located on the given path.
     * @param instancePath The instance path for the config.
     */
    private void registerInstance(String instancePath) {
        logger.debug("Registering {} as a discovered config", instancePath);

        // Split path
        String[] pathSegments = EtcdDiscoverer.splitInstancePath(instancePath);
        String type = pathSegments[0];
        String group = pathSegments[1];
        String name = pathSegments[2];

        // Get properties
        Map<String, String> properties = this.discoverer.getProperties(type, group, name);

        // Build discovered config object
        DiscoveredConfig config = new EtcdDiscoveredConfig(type, group, name, properties);
        this.configs.put(instancePath, config);

        // Build service component
        Dictionary<String, String> componentProperties = new Hashtable<>();
        componentProperties.put("type", type);
        componentProperties.put("group", group);

        Component component = this.dependencyManager.createComponent()
                .setInterface(DiscoveredConfig.class.getName(), componentProperties)
                .setImplementation(config);
        this.components.put(config, component);
        this.dependencyManager.add(component);
        logger.info("Discovered config {} {} has become available", name, String.format("(type=%s group=%s)", type, group));
    }

    /**
     * Unregisters an existing discovered config with properties located on the given path.
     * @param instancePath The instance path for the config.
     */
    private void unregisterInstance(String instancePath) {
        logger.debug("Unregistering {} as a discovered config", instancePath);

        DiscoveredConfig config = this.configs.get(instancePath);
        Component component = this.components.get(config);

        // Remove component from dependency manager
        this.dependencyManager.remove(component);
        logger.info("Discovered config for {} has been removed", instancePath);

        // Clean up in case the instance comes back
        this.components.remove(config);
        this.configs.remove(instancePath);
    }
}
