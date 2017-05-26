package org.inaetics.dronessimulator.discovery.etcd;

import mousio.etcd4j.responses.EtcdKeysResponse;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryNode;
import org.inaetics.dronessimulator.discovery.api.DiscoveryPath;
import org.inaetics.dronessimulator.discovery.api.discoverynode.DiscoveryStoredNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.NodeEventHandler;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.AddedNode;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.ChangedValue;
import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.RemovedNode;

import java.util.List;

/**
 * Handler for etcd changes. Monitors etcd for changes and updates the tree accordingly.
 */
public class EtcdChangeHandler extends Thread {
    /** The etcd discoverer to use. */
    private final EtcdDiscoverer discoverer;

    /** The root node of the tree. */
    private final DiscoveryNode cachedRoot;

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

        Logger.getLogger(EtcdChangeHandler.class).info("Starting EtcdChangeHandler...");

        EtcdKeysResponse.EtcdNode etcdNode = discoverer.getFromRoot(false);
        DiscoveryStoredNode storedRoot = new DiscoveryStoredEtcdNode(etcdNode);

        if(etcdNode != null) {
            synchronized (this) {
                cachedRoot.updateTree(storedRoot);
            }
        }

        Logger.getLogger(EtcdChangeHandler.class).info("Started EtcdChangeHandler!");

        while(!this.isInterrupted()) {
            etcdNode = discoverer.getFromRoot(true);
            storedRoot = new DiscoveryStoredEtcdNode(etcdNode);

            if(etcdNode != null) {
                synchronized (this) {
                    cachedRoot.updateTree(storedRoot);
                }
            }
        }

        Logger.getLogger(EtcdChangeHandler.class).info("Stopped EtcdChangeHandler!");
    }

    /**
     * Adds handlers for node events.
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
