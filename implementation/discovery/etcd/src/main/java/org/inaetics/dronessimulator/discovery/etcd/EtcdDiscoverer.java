package org.inaetics.dronessimulator.discovery.etcd;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.*;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Discoverer implementation which uses etcd.
 */
public class EtcdDiscoverer {
    private static final Logger logger = Logger.getLogger(EtcdDiscoverer.class);

    /** Prefix for all etcd paths. */
    private static final String PATH_PREFIX = "/";

    /** Prefix/location for instance references. */
    private static final String INSTANCE_DIR = "instances";

    /** Location where discoverable configs can be found. */
    private static final String DISCOVERABLE_CONFIG_DIR = "configs";

    /** The instances registered through this discoverer. */
    private Set<Instance> myInstances;

    /** The etcd client instance. */
    private EtcdClient client;

    /** Index of the last received update for a given path. */
    private Map<String, Long> pathModifiedIndex;

    /**
     * Instantiates a new etcd discoverer and connects to etcd using the given URI.
     * @param uri The URI to connect to etcd.
     */
    public EtcdDiscoverer(URI uri) {
        this.myInstances = new HashSet<>();
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
        this.pathModifiedIndex = new ConcurrentHashMap<>();

        // Create discoverable config directory
        this.client.putDir(buildPath(DISCOVERABLE_CONFIG_DIR));
    }

    /**
     * Closes any open connection to etcd. This method can be called to stop any processes waiting on changes from etcd.
     */
    public void closeConnection() {
        try {
            this.client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the given instance in etcd.
     * @param instance The instance to register.
     * @throws DuplicateName There is already an instance registered with the same type, group and name.
     * @throws IOException An error occured in the connection with etcd.
     */
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

        // Register instance
        this.myInstances.add(instance);
    }

    /**
     * Registers the properties for an instance. Assumes the instance itself already exists.
     * @param instance The instance to register the properties of.
     * @throws IOException An error occurred in the connection with etcd.
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
     * Unregisters the given instance from etcd.
     * @param instance The instance to unregister.
     * @throws IOException An error occurred in the connection with etcd.
     */
    public void unregister(Instance instance) throws IOException {
        String path = buildInstancePath(instance);

        logger.debug("Unregistering instance {} from {}", instance, path);

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

        for (Instance instance : this.myInstances) {
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
        for (Instance instance : this.myInstances) {
                this.unregister(instance);
        }
    }

    /**
     * Builds and returns an etcd node tree from the root. Optionally waits for changes.
     * @param wait Whether to wait for changes.
     * @return The root node of the tree, or null if the tree is empty or when an error occurred.
     */
    EtcdKeysResponse.EtcdNode getFromRoot(boolean wait) {
        EtcdKeysResponse.EtcdNode root = null;
        String path = buildPath();

        try {
            EtcdKeyGetRequest request = this.client.getDir(path).recursive();

            if (wait) {
                if (this.pathModifiedIndex.containsKey(path)) {
                    Long modifiedIndex = this.pathModifiedIndex.get(path) + 1;
                    request = request.waitForChange(modifiedIndex);
                } else {
                    request = request.waitForChange();
                }

                EtcdResponsePromise<EtcdKeysResponse> waitPromise = request.send();
                waitPromise.get();

                // If waited for changes, we have to get the actual data due to etcd quirks
                EtcdKeysResponse getResponse = this.client.getDir(path).recursive().send().get();
                root = getResponse.getNode();

                this.pathModifiedIndex.put(path, getResponse.etcdIndex);
            } else {
                EtcdKeysResponse getResponse = request.send().get();

                root = getResponse.getNode();
                this.pathModifiedIndex.put(path, getResponse.etcdIndex);
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return null
            logger.error("No data could be retrieved from etcd, returning null");
        }

        return root;
    }

    /**
     * Gets the groups and names for the given type.
     * @param type The type.
     * @return A map containing the groups and lists of names per group.
     */
    public Map<String, Collection<String>> find(String type) {
        return this.find(type, false);
    }

    /**
     * Gets the groups and names for the given type. Optionally waits for changes.
     * @param type The type.
     * @param wait Whether to wait for changes.
     * @return A map containing the groups and lists of names per group.
     */
    private Map<String, Collection<String>> find(String type, boolean wait) {
        Map<String, Collection<String>> forType = new HashMap<>();

        String path = buildInstancePath(type);

        try {
            EtcdKeyGetRequest request = this.client.getDir(path).recursive();

            if (wait) {
                if (this.pathModifiedIndex.containsKey(path)) {
                    request = request.waitForChange(this.pathModifiedIndex.get(path) + 1);
                } else {
                    request = request.waitForChange();
                }
            }

            EtcdResponsePromise<EtcdKeysResponse> promise = request.send();
            EtcdKeysResponse keys = promise.get();

            // If waited for changes, we have to get the actual data due to etcd quirks
            if (wait) {
                keys = this.client.getDir(path).recursive().send().get();
            }

            if (keys != null) {
                keys.node.nodes.forEach(groupNode -> {
                    Collection<String> forGroup = new HashSet<>();
                    forType.put(getDirName(groupNode.key), forGroup);
                    groupNode.nodes.forEach(node -> {
                        forGroup.add(getDirName(node.key));
                    });
                });

                if (wait) {
                    this.setModifiedIndex(path, keys);
                }
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty map
            logger.error("No data could be retrieved from etcd, returning an empty map for type {}", type);
        }

        return forType;
    }

    /**
     * Waits for a change in the given type and returns the groups and names for the given type.
     * @param type The type.
     * @return A map containing the groups and lists of names per group.
     */
    public Map<String, Collection<String>> waitFor(String type) {
        return this.find(type, true);
    }

    /**
     * Gets the names for the given type and group.
     * @param type The type.
     * @param group The group.
     * @return A list of names.
     */
    public Collection<String> find(String type, String group) {
        return this.find(type, group, false);
    }

    /**
     * Gets the names for the given type and group. Optionally waits for changes.
     * @param type The type.
     * @param group The group.
     * @param wait Whether to wait for changes.
     * @return A list of names.
     */
    private Collection<String> find(String type, String group, boolean wait) {
        Collection<String> forGroup = new HashSet<>();

        String path = buildInstancePath(type, group);

        try {
            EtcdKeyGetRequest request = this.client.getDir(path).recursive();

            if (wait) {
                if (this.pathModifiedIndex.containsKey(path)) {
                    request = request.waitForChange(this.pathModifiedIndex.get(path) + 1);
                } else {
                    request = request.waitForChange();
                }
            }

            EtcdResponsePromise<EtcdKeysResponse> promise = request.send();
            EtcdKeysResponse keys = promise.get();

            // If waited for changes, we have to get the actual data due to etcd quirks
            if (wait) {
                keys = this.client.getDir(path).recursive().send().get();
            }

            if (keys != null) {
                keys.node.nodes.forEach(node -> forGroup.add(getDirName(node.key)));

                if (wait) {
                    this.setModifiedIndex(path, keys);
                }
            }
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return an empty collection
            logger.error("No data could be retrieved from etcd, returning an empty collection for type {} and group {}", type, group);
        }

        return forGroup;
    }

    /**
     * Waits for changes in the given group and returns the names for the given type and group.
     * @param type The type.
     * @param group The group.
     * @return A list of names.
     */
    public Collection<String> waitFor(String type, String group) {
        return this.find(type, group, true);
    }

    /**
     * Sets the modified index for the given path.
     * @param path The path.
     * @param response The etcd response containing the modified index.
     */
    private void setModifiedIndex(String path, EtcdKeysResponse response) {
        if (response.node != null) {
            // Calculate and set largest modified index
            long modifiedIndex = response.node.modifiedIndex;

            for (EtcdKeysResponse.EtcdNode node : response.node.nodes) {
                if (node.modifiedIndex > modifiedIndex) {
                    modifiedIndex = node.modifiedIndex;
                }
            }

            this.pathModifiedIndex.put(path, modifiedIndex);
        }
    }

    /**
     * Returns the properties for the instance identified by the given parameters.
     * @param type The type of the instance.
     * @param group The group of the instance.
     * @param name The name of the instance.
     * @return The properties of the instance.
     */
    public Map<String, String> getProperties(Type type, Group group, String name) {
        return this.getProperties(type.getStr(), group.getStr(), name);
    }

    /**
     * Returns the properties for the instance identified by the given parameters.
     * @param type The type of the instance.
     * @param group The group of the instance.
     * @param name The name of the instance.
     * @return The properties of the instance.
     */
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
    static String buildInstancePath(Type type, Group group, String name) {
        return buildInstancePath(type.getStr(), group.getStr(), name);
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
    static String buildInstancePath(Type type, Group group) {
        return buildInstancePath(type.getStr(), group.getStr());
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
    static String buildInstancePath(Type type) {
        return buildInstancePath(type.getStr());
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
