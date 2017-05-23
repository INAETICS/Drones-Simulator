package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryStoredNode;

import java.util.List;
import java.util.stream.Collectors;

public class DiscoveryStoredEtcdNode extends DiscoveryStoredNode {
    private final EtcdKeysResponse.EtcdNode etcdNode;

    public DiscoveryStoredEtcdNode(EtcdKeysResponse.EtcdNode etcdNode) {
        this.etcdNode = etcdNode;
    }

    @Override
    public String getKey() {
        return etcdNode.getKey() == null ? "/" : etcdNode.getKey();
    }

    @Override
    public String getValue() {
        return etcdNode.getValue();
    }

    @Override
    public DiscoveryPath getPath() {
        return new DiscoveryPath(etcdNode.getKey().split(DiscoveryPath.PATH_DELIMITER));
    }

    @Override
    public List<DiscoveryStoredNode> getChildren() {
        return etcdNode.getNodes()
                       .stream()
                       .map(DiscoveryStoredEtcdNode::new)
                       .collect(Collectors.toList());
    }

    @Override
    public boolean isDir() {
        return etcdNode.isDir();
    }

        /**
     * Splits the given path into three segments. Always returns an array of length 3 where the elements represent
     * (in-order) the type, group and name of the instance.
     *
     * Assumes a valid instance path is given as input.
     * @param path The instance path to split.
     * @return The type, group and name of the instance.
     */
    private static String[] splitInstancePath(String path) {
        String[] segments = path.split("/");
        String[] triple = new String[]{"", "", ""};
        System.arraycopy(segments, 0, triple, 0, Math.min(segments.length, triple.length));
        return triple;
    }
}
