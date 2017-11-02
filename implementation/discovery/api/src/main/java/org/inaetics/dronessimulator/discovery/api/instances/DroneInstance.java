package org.inaetics.dronessimulator.discovery.api.instances;

import lombok.EqualsAndHashCode;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
public class DroneInstance extends Instance {
    public DroneInstance(String id) {
        super(Type.DRONE, Group.DRONE, id);
    }

    public DroneInstance(String id, Map<String, String> properties) {
        super(Type.DRONE, Group.DRONE, id, properties);
    }

    public static String getTeamname(Discoverer discoverer, String id){
        return discoverer.getNode(new DroneInstance(id)).getValues().getOrDefault("team", null);
    }
}
