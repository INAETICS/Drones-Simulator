package org.inaetics.dronessimulator.discovery.etcd;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.*;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Discoverer implementation which uses etcd.
 */
public class EtcdDiscoverer implements Discoverer {
    private static final Logger logger = Logger.getLogger(EtcdDiscoverer.class);

    /** Prefix for all etcd paths. */
    private static final String PATH_PREFIX = "/";

    /** Prefix/location for instance references. */
    private static final String INSTANCE_DIR = "instances";

    /** Location where discoverable configs can be found. */
    private static final String DISCOVERABLE_CONFIG_DIR = "configs";

    /** The instances registered through this discoverer. */
    private Map<Instance, String> myInstances;

    /** The etcd client instance. */
    private EtcdClient client;

    /** Index of the last received update. */
    private Long discoverableConfigModifiedIndex;

    /**
     * Instantiates a new etcd discoverer and connects to etcd using the given URI.
     * @param uri The URI to connect to etcd.
     */
    public EtcdDiscoverer(URI uri) {
        this.myInstances = new HashMap<>();
        this.client = new EtcdClient(uri);

        // Do not retry too many times or wait too long
        this.client.setRetryHandler(new RetryOnce(1));

        // Log server version
        EtcdVersionResponse versionResponse = this.client.version();

        if (versionResponse != null) {
            logger.info("Discoverer connected with etcd at {}, server version {}", uri.toString(), versionResponse.getServer());
        } else {
            logger.warn("Discoverer started, but could not connect to etcd");
        }

        // Initialize variables
        this.discoverableConfigModifiedIndex = null;

        // Create discoverable config directory
        this.client.putDir(buildPath(DISCOVERABLE_CONFIG_DIR));
    }

    @Override
    public void register(Instance instance) throws DuplicateName, IOException {
        String path = buildInstancePath(instance);
        logger.debug("Registering instance {} at path {}", instance, path);

        try {
            // Send request and wait for a response
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.putDir(path).prevExist(false).send();
            promise.get();
        } catch (EtcdException e) {
            if (e.isErrorCode(EtcdErrorCode.NodeExist)) {
                throw new DuplicateName(String.format("The name %s is already in use.", path));
            } else {
                throw new IOException(e);
            }
        } catch (EtcdAuthenticationException | TimeoutException e) {
            throw new IOException(e);
        }

        // Set properties
        this.registerProperties(instance);

        // Set discoverable config if needed
        String discoverablePath = null;

        if (instance.isConfigDiscoverable()) {
            discoverablePath = this.registerDiscoverableConfig(instance);
        }

        // Register instance
        this.myInstances.put(instance, discoverablePath);
    }

    /**
     * Registers the properties for an instance. Assumes the instance itself already exists.
     * @param instance The instance to register the properties of.
     * @throws IOException An error occurred.
     */
    public void registerProperties(Instance instance) throws IOException {
        String path = buildInstancePath(instance);

        EtcdResponsePromise promise;

        for (Map.Entry<String, String> entry : instance.getProperties().entrySet()) {
            try {
                logger.debug("Setting property for instance {}: {}", instance, String.format("%s = %s", entry.getKey(), entry.getValue()));
                promise = this.client.put(buildPath(path, entry.getKey()), entry.getValue()).send();
                promise.get();
            } catch (EtcdException | TimeoutException | EtcdAuthenticationException e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * Registers the instance as a discoverable config. Places a reference to the instance in a special etcd directory.
     * @param instance The instance to register.
     * @return The path to the key.
     * @throws IOException An error occurred.
     */
    private String registerDiscoverableConfig(Instance instance) throws IOException {
        String path;

        if (!this.myInstances.containsKey(instance)) {
            String instancePath = buildInstancePath(instance);
            String dirPath = buildPath(DISCOVERABLE_CONFIG_DIR);

            logger.debug("Registering discoverable configuration for instance {} at {}", instance, instancePath);

            try {
                EtcdResponsePromise<EtcdKeysResponse> promise = this.client.post(dirPath, instancePath).send();
                EtcdKeysResponse keys = promise.get();
                path = keys.node.key;
                logger.debug("Registered instance {} as discoverable at {}", instance, path);
            } catch (EtcdException | EtcdAuthenticationException | TimeoutException e) {
                throw new IOException(e);
            }
        } else {
            path = this.myInstances.get(instance);
        }

        return path;
    }

    @Override
    public void unregister(Instance instance) throws IOException {
        String path = buildInstancePath(instance);

        logger.debug("Unregistering instance {} from {}", instance, path);

        String discoverablePath = this.myInstances.getOrDefault(instance, null);

        // Unregister discoverable config
        if (instance.isConfigDiscoverable() && discoverablePath != null) {
            try {
                EtcdResponsePromise promise = this.client.delete(discoverablePath).send();
                promise.get();
                logger.debug("Unregistered instance {} as discoverable", instance);
            } catch (EtcdException | TimeoutException | EtcdAuthenticationException e) {
                throw new IOException(e);
            }
        }

        try {
            EtcdResponsePromise promise = this.client.deleteDir(path).recursive().send();
            promise.get();
        } catch (EtcdException | TimeoutException | EtcdAuthenticationException e) {
            throw new IOException(e);
        }

        this.myInstances.remove(instance);

        logger.debug("Unregistered instance {}", instance);
    }

    /**
     * (Re)registers all instances that were previously registered.
     */
    public void registerAll() throws IOException {
        logger.info("Reregistering all {} known instances", this.myInstances.size());

        for (Instance instance : this.myInstances.keySet()) {
            try {
                this.register(instance);
            } catch (DuplicateName ignored) {
                // Already exists, but update the properties
                this.registerProperties(instance);
            }
        }
    }

    /**
     * Unregisters all previously registered instances.
     */
    public void unregisterAll() throws IOException {
        logger.info("Unregistering all {} known instances", this.myInstances.size());
        for (Instance instance : this.myInstances.keySet()) {
                this.unregister(instance);
        }
    }

    @Override
    public Map<String, Collection<String>> find(String type) {
        Map<String, Collection<String>> forType = new HashMap<>();

        String path = buildInstancePath(type);

        try {
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.getDir(path).recursive().send();
            EtcdKeysResponse keys = promise.get();

            if (keys != null) {
                keys.node.nodes.forEach(groupNode -> {
                    Collection<String> forGroup = new HashSet<>();
                    forType.put(getDirName(groupNode.key), forGroup);
                    groupNode.nodes.forEach(node -> {
                        forGroup.add(getDirName(node.key));
                    });
                });
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty map
            logger.error("No data could be retrieved from etcd, returning an empty map for type {}", type);
        }

        return forType;
    }

    @Override
    public Collection<String> find(String type, String group) {
        Collection<String> forGroup = new HashSet<>();

        String path = buildInstancePath(type, group);

        try {
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.getDir(path).recursive().send();
            EtcdKeysResponse keys = promise.get();

            if (keys != null) {
                keys.node.nodes.forEach(node -> forGroup.add(getDirName(node.key)));
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty collection
            logger.error("No data could be retrieved from etcd, returning an empty collection for type {} and group {}", type, group);
        }

        return forGroup;
    }

    @Override
    public Map<String, String> getProperties(String type, String group, String name) {
        Map<String, String> properties = new HashMap<>();

        String path = buildInstancePath(type, group, name);

        try {
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.getDir(path).send();
            EtcdKeysResponse keys = promise.get();

            if (keys != null) {
                keys.node.nodes.forEach(node -> properties.put(getDirName(node.key), node.value));
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty map
            logger.error("No data could be retrieved from etcd, returning an empty map for instance with path {}", path);
        }

        return properties;
    }

    /**
     * Returns a collection of type, group, name triples of instances registered as discoverable configurations.
     * Waits for changes to be made before returning if the wait parameter is set to true.
     * @param wait Whether to wait for changes.
     * @return A collection of triples for the registered instances.
     */
    Collection<String> getDiscoverableConfigs(boolean wait) {
        Collection<String> instances = new HashSet<>();

        String path = buildPath(DISCOVERABLE_CONFIG_DIR);

        try {
            EtcdKeyGetRequest request = this.client.getDir(path);

            // Wait for change if needed
            if (wait && this.discoverableConfigModifiedIndex != null) {
                logger.debug("Waiting for changes in discoverable configs at {}", path);
                request = request.waitForChange(this.discoverableConfigModifiedIndex);
            }

            EtcdResponsePromise<EtcdKeysResponse> promise = request.send();
            EtcdKeysResponse keys = promise.get();

            if (keys != null) {
                keys.node.nodes.forEach(node -> instances.add(node.value));

                if (wait) {
                    this.discoverableConfigModifiedIndex = keys.node.modifiedIndex;
                    logger.info("Updated last seen change to {}", this.discoverableConfigModifiedIndex);
                }
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty set
            logger.error("No data could be retrieved from etcd, returning an empty collection of discoverble configurations");
        }

        return instances;
    }

    /**
     * Builds an etcd path from a number of strings.
     * @param segments The segments of the path.
     * @return The constructed path.
     */
    static String buildPath(String... segments) {
        return PATH_PREFIX + String.join("/", segments);
    }

    /**
     * Builds an etcd path for the given instance.
     * @param instance The instance to build the path for.
     * @return The path for the instance.
     */
    static String buildInstancePath(Instance instance) {
        return buildInstancePath(instance.getType(), instance.getGroup(), instance.getName());
    }

    /**
     * Builds an etcd path for the given parameters.
     * @param type The type of the instance.
     * @param group The group of the instance.
     * @param name The name of the instance.
     * @return The path for the instance.
     */
    static String buildInstancePath(String type, String group, String name) {
        return buildPath(INSTANCE_DIR, type, group, name);
    }

    /**
     * Builds an etcd path for the given parameters.
     * @param type The type of the instance.
     * @param group The group of the instance.
     * @return The path for the instance.
     */
    static String buildInstancePath(String type, String group) {
        return buildPath(INSTANCE_DIR, type, group);
    }

    /**
     * Builds an etcd path for the given parameters.
     * @param type The type of the instance.
     * @return The path for the instance.
     */
    static String buildInstancePath(String type) {
        return buildPath(INSTANCE_DIR, type);
    }

    /**
     * Returns the last segment of the given path. If the given string is not a path or is a path with a single-level,
     * the input string is returned.
     * @param path The full path.
     * @return The last segment in the path.
     */
    static String getDirName(String path) {
        return path.substring(Math.max(0, path.lastIndexOf("/") + 1));
    }

    /**
     * Splits the given path into three segments. Always returns an array of length 3 where the elements represent
     * (in-order) the type, group and name of the instance.
     *
     * Assumes a valid instance path is given as input.
     * @param path The instance path to split.
     * @return The type, group and name of the instance.
     */
    static String[] splitInstancePath(String path) {
        String[] segments = path.replaceFirst(PATH_PREFIX, "").replaceFirst(INSTANCE_DIR + "/", "").split("/");
        String[] triple = new String[]{"", "", ""};
        System.arraycopy(segments, 0, triple, 0, Math.min(segments.length, triple.length));
        return triple;
    }
}
