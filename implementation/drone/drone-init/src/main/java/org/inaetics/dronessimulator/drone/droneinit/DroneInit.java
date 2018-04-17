package org.inaetics.dronessimulator.drone.droneinit;


import org.apache.log4j.Logger;
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
public class DroneInit {
    private String identifier;

    public DroneInit(String identifier, Discoverer discoverer, Instance registeredInstance) {  //Constructor for testing purposes
        this.identifier = identifier;
        this.discoverer = discoverer;
        this.registeredInstance = registeredInstance;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static Logger getLog() {
        return log;
    }

    public Discoverer getDiscoverer() {
        return discoverer;
    }

    public void setDiscoverer(Discoverer discoverer) {
        this.discoverer = discoverer;
    }

    public Instance getRegisteredInstance() {
        return registeredInstance;
    }

    public void setRegisteredInstance(Instance registeredInstance) {
        this.registeredInstance = registeredInstance;
    }

    /**
     * Create the logger
     */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DroneInit.class);
    /**
     * The client for the discoverer where the
     */
    @SuppressWarnings("unused")//This is initialized by Apache Felix
    private volatile Discoverer discoverer;

    /**
     * The instance registered in discovery noting the drone service
     */
    private Instance registeredInstance;

    public DroneInit() {
        this.initIdentifier();
    }

    /**
     * On startup register Drone in Discovery.
     */
    public void start() throws IOException {
        log.info("Starting the drone now!");
        this.registerDroneService();
    }

    /**
     * On stop unregister Drone in Discovery.
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
            discoverer.register(instance);
            this.registeredInstance = instance;
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

    private void unregisterDroneService() throws IOException {
        this.discoverer.unregister(registeredInstance);
    }

    /**
     * Initializes the identifier for this drone. It checks the following environment variables in order:
     * - DRONENAME
     * - COMPUTERNAME
     * - HOSTNAME
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
