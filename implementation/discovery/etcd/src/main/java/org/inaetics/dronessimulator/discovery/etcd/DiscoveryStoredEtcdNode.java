package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryStoredNode;

import java.util.List;
import java.util.stream.Collectors;

public class DiscoveryStoredEtcdNode implements DiscoveryStoredNode {
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
}
