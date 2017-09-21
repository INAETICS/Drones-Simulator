package org.inaetics.dronessimulator.drone.droneinit;


import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.discovery.api.DuplicateName;
import org.inaetics.dronessimulator.discovery.api.Instance;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Group;
import org.inaetics.dronessimulator.discovery.api.discoverynode.Type;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The initial bundle in the drone dependency tree
 * Generates the id of the drone
 */
public class DroneInit {
    /**
     * The logger
     */
    private static final Logger logger = Logger.getLogger(DroneInit.class);

    /**
     * The identifier of this drone
     */
    private String identifier;

    /**
     * Reference to the Discoverer bundle
     */
    private volatile Discoverer m_discoverer;

    /**
     * The instance registered in discovery noting the drone service
     */
    private Instance registered_instance;

    public DroneInit(){
        this.initIdentifier();
    }

    /**
     * FELIX CALLBACKS
     */
    /**
     * On startup register Drone in Discovery.
     */
    public void start() throws IOException{
        logger.info("Starting the drone now!");
        this.registerDroneService();
    }

    /**
     * On startup unregister Drone in Discovery.
     */
    public void stop() throws IOException {
        logger.info("Stopping the drone now!");
        this.unregisterDroneService();
    }

    /**
     * Register the drone service in Discovery
     */
    private void registerDroneService(){
        Map<String, String> properties = new HashMap<>();

        properties.put("team", "team1");
        Instance instance = new Instance(Type.SERVICE, Group.SERVICES, this.getIdentifier(), properties);
        try{
            m_discoverer.register(instance);
            this.registered_instance = instance;
        } catch (IOException e) {
            logger.fatal(e);
        }catch(DuplicateName e){
            this.setIdentifier(this.getIdentifier() + "-" + UUID.randomUUID().toString());
            this.registerDroneService();
        }

    }

    /**
     * Unregister the drone service in Discovery
     */
    private void unregisterDroneService(){
        try{
            this.m_discoverer.unregister(registered_instance);
        } catch (IOException e) {
            logger.fatal(e);
        }
    }

    /**
     * Returns the indentifier from the drone
     * @return the indentifier
     */
    public String getIdentifier(){
        return this.identifier;
    }

    /**
     * Changes the indentifier to a new value
     * @param new_identifier the new indentifier
     */
    public void setIdentifier(String new_identifier){
        this.identifier = new_identifier;
    }

    /**
     * Initializes the identifier for this drone
     * It checks the following environment variables
     * in order: DRONENAME, COMPUTERNAME, HOSTNAME
     * If none are found, it generates a random UUID
     */
    public void initIdentifier(){
        Map<String, String> env = System.getenv();
        if(env.containsKey("DRONENAME"))
            this.setIdentifier(env.get("DRONENAME"));
        else if (env.containsKey("COMPUTERNAME"))
            this.setIdentifier(env.get("COMPUTERNAME"));
        else if (env.containsKey("HOSTNAME"))
            this.setIdentifier(env.get("HOSTNAME"));
        else
            this.setIdentifier(UUID.randomUUID().toString());
    }
}
