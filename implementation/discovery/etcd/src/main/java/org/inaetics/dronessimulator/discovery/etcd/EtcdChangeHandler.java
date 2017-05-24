package org.inaetics.dronessimulator.discovery.etcd;


import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;

import java.util.List;

public class EtcdChangeHandler extends Thread {
    private final EtcdDiscoverer discoverer;
    private final DiscoveryNode cachedRoot;

    public EtcdChangeHandler(EtcdDiscoverer discoverer) {
        this.discoverer = discoverer;
        this.cachedRoot = new DiscoveryNode(DiscoveryPath.ROOT, null, DiscoveryPath.ROOT_PATH);
    }

    @Override
    public void run() {
        // Run once to get currentstate
        EtcdKeysResponse.EtcdNode etcdNode = discoverer.getFromRoot(false);
        DiscoveryStoredNode storedRoot = new DiscoveryStoredEtcdNode(etcdNode);
        synchronized (this) {
            cachedRoot.updateTree(storedRoot);
        }

        while(!this.isInterrupted()) {
            etcdNode = discoverer.getFromRoot(true);
            storedRoot = new DiscoveryStoredEtcdNode(etcdNode);

            synchronized (this) {
                cachedRoot.updateTree(storedRoot);
            }
        }
    }

    public void addHandlers(boolean replay, List<NodeEventHandler<AddedNode>> addHandlers
                                          , List<NodeEventHandler<ChangedValue>> changedValueHandlers
                                          , List<NodeEventHandler<RemovedNode>> removedHandlers) {
        synchronized (this) {
            for(NodeEventHandler<AddedNode> handler : addHandlers) {
                if(replay) {
                    this.cachedRoot.initializeAddHandler(handler);
                }
                this.cachedRoot.addAddNodeHandler(handler);
            }

            for(NodeEventHandler<ChangedValue> handler : changedValueHandlers) {
                if(replay) {
                    this.cachedRoot.initializeChangeHandler(handler);
                }
                this.cachedRoot.addChangeValueHandler(handler);
            }

            for(NodeEventHandler<RemovedNode> handler : removedHandlers) {
                this.cachedRoot.addRemoveNodeHandler(handler);
            }
        }

   }

    public void quit() {
        this.interrupt();
    }
}
