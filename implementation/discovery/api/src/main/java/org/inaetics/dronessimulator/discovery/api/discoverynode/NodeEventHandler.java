package org.inaetics.dronessimulator.discovery.api.discoverynode;

import org.inaetics.dronessimulator.discovery.api.discoverynode.discoveryevent.NodeEvent;

public interface NodeEventHandler<T extends NodeEvent> {
    void handle(T t);
}
