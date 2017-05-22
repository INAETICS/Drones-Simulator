package org.inaetics.dronessimulator.discovery.api.discoveryevent;


public interface DiscoveryHandler<T> {
    void handle(T t);

}
