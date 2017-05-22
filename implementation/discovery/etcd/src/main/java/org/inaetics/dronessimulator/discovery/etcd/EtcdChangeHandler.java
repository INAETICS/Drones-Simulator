package org.inaetics.dronessimulator.discovery.etcd;


import mousio.etcd4j.responses.EtcdKeysResponse;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.DiscoveryHandler;
import org.inaetics.dronessimulator.discovery.api.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryDirNode;
import org.inaetics.dronessimulator.discovery.api.tree.DiscoveryStoredNode;

import java.util.List;

public class EtcdChangeHandler extends Thread {
    private final EtcdDiscoverer discoverer;
    private final DiscoveryDirNode cachedRoot;

    public EtcdChangeHandler(EtcdDiscoverer discoverer) {
        this.discoverer = discoverer;
        this.cachedRoot = new DiscoveryDirNode("/");
    }

    @Override
    public void run() {
        // Run once to get currentstate
        EtcdKeysResponse.EtcdNode etcdNode = discoverer.getFromRoot(false);
        DiscoveryStoredNode storedRoot = new DiscoveryStoredEtcdNode(etcdNode);
        cachedRoot.updateTree(storedRoot);

        while(!this.isInterrupted()) {
            System.out.println(" BEFORE");
            etcdNode = discoverer.getFromRoot(true);
            storedRoot = new DiscoveryStoredEtcdNode(etcdNode);

            EtcdKeysResponse.EtcdNode currentTop = etcdNode;
            System.out.println("Found: ");
            while(currentTop != null) {
                System.out.println("  " + currentTop.getKey());

                if(currentTop.getNodes().size() > 0) {
                currentTop = currentTop.getNodes().get(0);
                } else {
                    currentTop = null;
                }
            }

            synchronized (this) {
                cachedRoot.updateTree(storedRoot);
            }
        }
    }

    public void addHandlers(boolean replay, List<DiscoveryHandler<AddedNode>> addHandlers
                                          , List<DiscoveryHandler<ChangedValue<String>>> changedValueHandlers
                                          , List<DiscoveryHandler<RemovedNode<String>>> removedHandlers) {
        synchronized (this) {
            for(DiscoveryHandler<AddedNode> handler : addHandlers) {
                if(replay) {
                    this.cachedRoot.initializeAddHandler(handler);
                }
                this.cachedRoot.addAddNodeHandler(handler);
            }

            for(DiscoveryHandler<ChangedValue<String>> handler : changedValueHandlers) {
                if(replay) {
                    this.cachedRoot.initializeChangeHandler(handler);
                }
                this.cachedRoot.addChangeValueHandler(handler);
            }

            for(DiscoveryHandler<RemovedNode<String>> handler : removedHandlers) {
                this.cachedRoot.addRemoveNodeHandler(handler);
            }
        }

   }

    public void quit() {
        this.interrupt();
    }
}
