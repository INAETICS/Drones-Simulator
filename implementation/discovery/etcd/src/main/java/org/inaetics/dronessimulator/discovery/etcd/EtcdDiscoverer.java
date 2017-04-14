package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdErrorCode;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Discoverer implementation which uses etcd.
 */
public class EtcdDiscoverer implements Discoverer {
    private static final String PATH_PREFIX = "/";

    /** The instances registered through this discoverer. */
    Collection<Instance> myInstances;

    /** The etcd client instance. */
    EtcdClient client;

    public EtcdDiscoverer(URI uri) {
        this.myInstances = new HashSet<>();
        this.client = new EtcdClient(uri);
    }

    @Override
    public void register(Instance instance) throws DuplicateName, IOException {
        String path = buildInstancePath(instance);

        EtcdResponsePromise<EtcdKeysResponse> promise = this.client.putDir(path).prevExist(false).send();
        Throwable exception = promise.getException();

        // Check if this instance already exists
        if (exception instanceof EtcdException && ((EtcdException) exception).isErrorCode(EtcdErrorCode.NodeExist)) {
            throw new DuplicateName(exception);
        } else if (exception != null) {
            throw new IOException(exception);
        } else {
            // Set properties
            instance.getProperties().forEach((key, value) -> this.client.put(buildPath(path, key), value));
            this.myInstances.add(instance);
        }
    }

    @Override
    public void unregister(Instance instance) throws IOException {
        String path = buildInstancePath(instance);

        this.client.deleteDir(path).recursive().send();
        this.myInstances.remove(instance);
    }

    @Override
    public Map<String, Collection<String>> find(String type) {
        Map<String, Collection<String>> forType = new HashMap<>();

        String path = buildPath(type);

        try {
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.getDir(type).recursive().send();
            EtcdKeysResponse keys = promise.getNow();

            if (keys != null) {
                keys.node.nodes.forEach(groupNode -> {
                    Collection<String> forGroup = new HashSet<>();
                    forType.put(getDirName(groupNode.key), forGroup);
                    groupNode.nodes.forEach(node -> {
                        forGroup.add(getDirName(node.key));
                    });
                });
            }
        } catch (IOException ignored) {
            // Just return an empty map
        }

        return forType;
    }

    @Override
    public Collection<String> find(String type, String group) {
        Collection<String> forGroup = new HashSet<>();

        String path = buildPath(type, group);

        try {
            EtcdResponsePromise<EtcdKeysResponse> promise = this.client.getDir(path).recursive().send();
            EtcdKeysResponse keys = promise.getNow();

            if (keys != null) {
                keys.node.nodes.forEach(node -> forGroup.add(getDirName(node.key)));
            }
        } catch (IOException ignored) {
            // Just return an empty map
        }

        return forGroup;
    }

    /**
     * Builds an etcd path from a number of strings.
     * @param segments The segments of the path.
     * @return The constructed path.
     */
    private static String buildPath(String ... segments) {
        return PATH_PREFIX + String.join("/", segments);
    }

    /**
     * Builds an etcd path for the given instance.
     * @param instance The instance to build the path for.
     * @return The path for the instance.
     */
    private static String buildInstancePath(Instance instance) {
        return buildPath(instance.getType(), instance.getGroup(), instance.getName());
    }

    /**
     * Returns the last segment of the given path. If the given string is not a path or is a path with a single-level,
     * the input string is returned.
     * @param path The full path.
     * @return The last segment in the path.
     */
    private static String getDirName(String path) {
        return path.substring(Math.max(0, path.lastIndexOf("/")));
    }
}
