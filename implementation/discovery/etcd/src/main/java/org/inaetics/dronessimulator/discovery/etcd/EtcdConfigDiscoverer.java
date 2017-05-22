package org.inaetics.dronessimulator.discovery.etcd;

import org.apache.log4j.Logger;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.io.IOException;
import java.util.*;

/**
 * Discovers configurations and publishes these as services.
 */
public class EtcdConfigDiscoverer implements Runnable {
    private static final Logger logger = Logger.getLogger(EtcdConfigDiscoverer.class);

    /** The discoverer used to find configs. */
    private EtcdDiscoverer discoverer;

    /** The configuration admin used to register config services. */
    private ConfigurationAdmin configurationAdmin;

    /** Map of the already discovered configurations. */
    private HashMap<String, String> configs = new HashMap<>();

    /**
     * Instantiates a new config discoverer.
     * @param discoverer The etcd discoverer to use to find configs.
     */
    public EtcdConfigDiscoverer(EtcdDiscoverer discoverer, ConfigurationAdmin configurationAdmin) {
        this.discoverer = discoverer;
        this.configurationAdmin = configurationAdmin;

        // Initialize some fields
        this.configs = new HashMap<>();
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

            logger.debug("Found {} configs in etcd", collectedPaths.size());

            // Process paths
            for (String path : collectedPaths) {
                if (this.configs.containsKey(path)) {
                    logger.debug("Config path {} is still there", path);
                    stalePaths.remove(path);
                } else {
                    logger.debug("Config path {} is new", path);
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
        List<String> pathSegmentCollection = Arrays.asList(pathSegments);

        if (pathSegments.length == 3 && !pathSegmentCollection.contains("")) {
            String type = pathSegments[0];
            String group = pathSegments[1];
            String name = pathSegments[2];

            // Build and register pid
            String pid = String.join(".", pathSegments);
            this.configs.put(instancePath, pid);

            // Get properties
            Map<String, String> properties = this.discoverer.getProperties(type, group, name);
            Dictionary<String, String> configurationProperties = new Hashtable<>(properties);

            // Register properties
            try {
                Configuration configuration = this.configurationAdmin.getConfiguration(pid);
                configuration.update(configurationProperties);
            } catch (IOException e) {
                logger.error("Error when accessing the configuration admin: {}", e.getMessage());
            }

            logger.info("Discovered config {} has become available", pid);
        } else {
            logger.warn("Invalid config {} detected", instancePath);
        }
    }

    /**
     * Unregisters an existing discovered config with properties located on the given path.
     * @param instancePath The instance path for the config.
     */
    private void unregisterInstance(String instancePath) {
        logger.debug("Unregistering {} as a discovered config", instancePath);

        String pid = this.configs.get(instancePath);

        // Remove component from configuration admin
        try {
            this.configurationAdmin.getConfiguration(pid).delete();
        } catch (IOException e) {
            logger.error("Error when accessing the configuration admin: {}", e.getMessage());
        }
        logger.info("Discovered config {} has been removed", pid);

        // Clean up in case the instance comes back
        this.configs.remove(instancePath);
    }
}
