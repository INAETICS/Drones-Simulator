package org.inaetics.dronessimulator.drone.droneinit;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.instances.DroneInstance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The initial bundle in the drone dependency tree
 * Generates the id of the drone
 */
@Log4j
public class DroneInit {
    /**
     * The identifier of this drone
     */
    @Getter
    @Setter
    private String identifier;

    /**
     * Reference to the Discoverer bundle
     */
    private volatile Discoverer m_discoverer;

    /**
     * The instance registered in discovery noting the drone service
     */
    private Instance registered_instance;

    public DroneInit() {
        this.initIdentifier();
    }

    /**
     * Constructor for test purposes
     *
     * @param discoverer An implementation for the Discoverer
     */
    public DroneInit(Discoverer discoverer) {
        this();
        m_discoverer = discoverer;
    }

    /**
     * FELIX CALLBACKS
     */
    /**
     * On startup register Drone in Discovery.
     */
    public void start() throws IOException {
        log.info("Starting the drone now!");
        this.registerDroneService();
    }

    /**
     * On startup unregister Drone in Discovery.
     */
    public void stop() throws IOException {
        log.info("Stopping the drone now!");
        this.unregisterDroneService();
    }

    /**
     * Register the drone service in Discovery
     */
    private void registerDroneService() throws IOException {
        Map<String, String> properties = new HashMap<>();
        properties.put("team", getTeamname());
        Instance instance = new DroneInstance(this.getIdentifier(), properties);
        try {
            m_discoverer.register(instance);
            this.registered_instance = instance;
        } catch (DuplicateName e) {
            this.setIdentifier(this.getIdentifier() + "-" + UUID.randomUUID().toString());
            this.registerDroneService();
        }
    }

    public String getTeamname() {
        String teamname = "unknown_team"; //Default fallback

        if (System.getenv("DRONE_TEAM") != null) {
            teamname = System.getenv("DRONE_TEAM");
        }
        return teamname;
    }


    /**
     * Unregister the drone service in Discovery
     */
    private void unregisterDroneService() throws IOException {
        this.m_discoverer.unregister(registered_instance);
    }

    /**
     * Initializes the identifier for this drone
     * It checks the following environment variables
     * in order: DRONENAME, COMPUTERNAME, HOSTNAME
     * If none are found, it generates a random UUID
     */
    public void initIdentifier() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("DRONENAME"))
            this.setIdentifier(env.get("DRONENAME"));
        else if (env.containsKey("COMPUTERNAME"))
            this.setIdentifier(env.get("COMPUTERNAME"));
        else if (env.containsKey("HOSTNAME"))
            this.setIdentifier(env.get("HOSTNAME"));
        else
            this.setIdentifier(UUID.randomUUID().toString());
    }
}
