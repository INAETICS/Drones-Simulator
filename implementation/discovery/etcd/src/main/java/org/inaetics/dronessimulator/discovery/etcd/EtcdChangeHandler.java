package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;
import org.inaetics.dronessimulator.discovery.api.tree.Tuple;

import java.util.List;

/**
 * Handler for etcd changes. Monitors etcd for changes and updates the tree accordingly.
 */
public class EtcdChangeHandler extends Thread {
    /** The etcd discoverer to use. */
    private final EtcdDiscoverer discoverer;

    /** The root node of the tree. */
    private final DiscoveryNode cachedRoot; //TODO implement TTL here as well

    /** The last seen etcd change index. */
    private Long nextModifiedIndex;

    /**
     * Instantiates a new change handler with the given discoverer. Also builds a new root node.
     * @param discoverer The etcd discoverer to use as connection to etcd.
     */
    public EtcdChangeHandler(EtcdDiscoverer discoverer) {
        this.discoverer = discoverer;
        this.cachedRoot = new DiscoveryNode(DiscoveryPath.ROOT, null, DiscoveryPath.ROOT_PATH);
    }

    /**
     * Gets the etcd state and then waits for changes in etcd before updating the tree.
     */
    @Override
    public void run() {
        // Run once to get currentstate
        DiscoveryStoredNode storedRoot;
        EtcdKeysResponse.EtcdNode etcdNode;
        Tuple<EtcdKeysResponse.EtcdNode, Long> discovererData;

        Logger.getLogger(EtcdChangeHandler.class).info("Starting EtcdChangeHandler...");

        discovererData = discoverer.getFromRoot(null, false);
        if(discovererData != null) {
            etcdNode = discovererData.getT1();

            if(etcdNode != null) {
                nextModifiedIndex = discovererData.getT2();
                storedRoot = new DiscoveryStoredEtcdNode(etcdNode);

                synchronized (this) {
                    cachedRoot.updateTree(storedRoot);
                }
            }
        }

        Logger.getLogger(EtcdChangeHandler.class).info("Started EtcdChangeHandler!");

        while(!this.isInterrupted()) {
            discovererData = discoverer.getFromRoot(nextModifiedIndex, true);
            if(discovererData != null) {
                etcdNode = discovererData.getT1();

                if(etcdNode != null) {
                    storedRoot = new DiscoveryStoredEtcdNode(etcdNode);
                    nextModifiedIndex = discovererData.getT2();

                    synchronized (this) {
                        cachedRoot.updateTree(storedRoot);
                    }
                }
            }
        }

        Logger.getLogger(EtcdChangeHandler.class).info("Stopped EtcdChangeHandler!");
    }

    /**
     * Adds handlers for node events. The handlers are added to the root of the tree so they will receive all events of
     * the respective type.
     * @param replay Whether to replay the current state of the tree by sending added node events for the existing
     *               nodes.
     * @param addHandlers The added node event handlers to add.
     * @param changedValueHandlers The changed value event handlers to add.
     * @param removedHandlers The removed node event handlers to add.
     */
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

    /**
     * Stops monitoring etcd and updating the tree by interrupting this thread.
     */
    public void quit() {
        this.interrupt();
    }
}
