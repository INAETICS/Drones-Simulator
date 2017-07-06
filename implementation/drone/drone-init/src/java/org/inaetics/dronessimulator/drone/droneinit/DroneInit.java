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

public class DroneInit {
    private static final Logger logger = Logger.getLogger(DroneInit.class);

    private final BundleContext bundleContext;
    private String identifier;
    private volatile Discoverer m_discoverer;
    private Instance registered_instance;

    public DroneInit(BundleContext bundleContext){
        this.bundleContext = bundleContext;
        this.initIdentifier();
    }

    /**
     * FELIX CALLBACKS
     */
    /**
     * On startup register Drone in Discovery.
     */
    public void start() throws IOException{
        this.registerDroneService();
    }

    /**
     * On startup unregister Drone in Discovery.
     */
    public void stop() throws IOException {
        this.unregisterDroneService();
    }

    /**
     * Registers the Drone in Discovery
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
     * Unregisters the Drone in Discovery
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
     * @return
     */
    public String getIdentifier(){
        return this.identifier;
    }

    /**
     * Changes the indentifier to a new value
     * @param new_identifier
     */
    public void setIdentifier(String new_identifier){
        this.identifier = new_identifier;
    }

    /**
     * Generates a new indentifier based on enviroment variables
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
