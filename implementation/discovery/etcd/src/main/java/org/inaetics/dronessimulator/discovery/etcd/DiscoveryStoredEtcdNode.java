package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscoveryStoredEtcdNode extends DiscoveryStoredNode {
    private final EtcdKeysResponse.EtcdNode etcdNode;

    public DiscoveryStoredEtcdNode(EtcdKeysResponse.EtcdNode etcdNode) {
        this.etcdNode = etcdNode;
    }

    @Override
    public String getId() {
        return getNodeName(etcdNode.getKey());
    }

    @Override
    public Map<String, String> getValues() {
        Map<String, String> values = new HashMap<>();

        etcdNode.getNodes()
                .stream()
                .filter((n) -> !n.isDir())
                .forEach((n) -> values.put(getNodeName(n.getKey()), n.getValue()));


        return values;
    }

    @Override
    public List<DiscoveryStoredNode> getChildren() {
        return etcdNode.getNodes()
                       .stream()
                       .filter(EtcdKeysResponse.EtcdNode::isDir)
                       .map(DiscoveryStoredEtcdNode::new)
                       .collect(Collectors.toList());
    }

    static String getNodeName(String key) {
        if(key != null) {
            return key.substring(Math.max(0, key.lastIndexOf("/") + 1));
        } else {
            return DiscoveryPath.ROOT_PATH.toString();
        }
    }
}
