package org.inaetics.dronessimulator.discovery.etcd;

import mousio.client.retry.RetryOnce;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.responses.*;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.inaetics.dronessimulator.discovery.api.tree.Tuple;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
            logger.fatal(e);
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
     * (Re)registers all instances. Also ones that were previously registered.
     */
    public void registerAll() throws IOException {
        logger.info("Reregistering all {} known instances", this.myInstances.size());

        for (Instance instance : this.myInstances) {
            try {
                this.register(instance);
            } catch (DuplicateName e) {
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
     * Builds and returns an etcd node tree from the root. Optionally waits for changes since the given modified index.
     * @param modifiedIndex The last seen modified index. Used to wait for new changes. May be null to not take changes
     *                      into account.
     * @param wait Whether to wait for changes.
     * @return Tuple containing the root node of the tree and the next modified index, or null if the tree is empty or when
     *         an error occurred.
     */
    Tuple<EtcdKeysResponse.EtcdNode, Long> getFromRoot(Long modifiedIndex, boolean wait) {
        Tuple<EtcdKeysResponse.EtcdNode, Long> returnValue = null;

        String path = buildPath();

        try {
            EtcdKeyGetRequest request = this.client.getDir(path).recursive();
            EtcdKeysResponse getResponse;

            if (wait) {
                if (modifiedIndex != null) {
                    request = request.waitForChange(modifiedIndex);
                } else {
                    request = request.waitForChange();
                }

                EtcdResponsePromise<EtcdKeysResponse> waitPromise = request.send();
                waitPromise.get();

                // If waited for changes, we have to get the actual data due to etcd quirks
                getResponse = this.client.getDir(path).recursive().send().get();
            } else {
                getResponse = request.send().get();
            }

            EtcdKeysResponse.EtcdNode root = getResponse.getNode();
            Long index = getResponse.etcdIndex + 1;

            returnValue = new Tuple<>(root, index);
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException ignored) {
            // Just return null
            logger.error("No data could be retrieved from etcd, returning null", ignored);
        }

        return returnValue;
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

    public Instance updateProperties(Instance instance, Map<String, String> properties) throws IOException {
        Instance newInstance = new Instance(instance.getType(), instance.getGroup(), instance.getName(), properties);

        registerProperties(newInstance);

        return newInstance;
    }

    public DiscoveryStoredNode getNode(Instance instance) {
        String path = buildInstancePath(instance);
        EtcdKeysResponse.EtcdNode etcdEntry = null;
        try {
            etcdEntry = this.client.get(path).send().get().getNode();
        } catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
            // Just return null
            logger.error("No data could be retrieved from etcd, returning null", e);
            return null;
        }
        return new DiscoveryStoredEtcdNode(etcdEntry);
    }
}
