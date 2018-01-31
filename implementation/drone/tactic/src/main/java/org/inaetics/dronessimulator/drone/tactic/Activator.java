package org.inaetics.dronessimulator.drone.tactic;

import org.apache.felix.dm.Component;
import org.apache.felix.dm.DependencyActivatorBase;
import org.apache.felix.dm.DependencyManager;
import org.apache.felix.dm.ServiceDependency;
import org.apache.log4j.Logger;
import org.inaetics.dronessimulator.architectureevents.ArchitectureEventController;
import org.inaetics.dronessimulator.discovery.api.Discoverer;
import org.inaetics.dronessimulator.drone.components.engine.Engine;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.gun.Gun;
import org.inaetics.dronessimulator.drone.components.radar.Radar;
import org.inaetics.dronessimulator.drone.components.radio.Radio;
import org.inaetics.dronessimulator.drone.droneinit.DroneInit;
import org.inaetics.dronessimulator.drone.tactic.example.SimpleTactic;
import org.inaetics.dronessimulator.pubsub.api.subscriber.Subscriber;
import org.osgi.framework.BundleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Activator extends DependencyActivatorBase {
    private static final Logger logger = Logger.getLogger(Activator.class);

    @Override
    public void init(BundleContext bundleContext, DependencyManager dependencyManager) throws Exception {
        Tactic tactic = createNewTactic();

        Component component = createComponent()
                .setInterface(Tactic.class.getName(), null)
                .setImplementation(tactic)
                .setCallbacks("init", "startTactic", "stopTactic", "destroy");

        for (ServiceDependency dep : getDroneComponents(dependencyManager)) {
            component.add(dep);
        }

        component.add(
                createServiceDependency()
                        .setService(ArchitectureEventController.class)
                        .setRequired(true)
        );

        component.add(
                createServiceDependency()
                        .setService(Subscriber.class)
                        .setRequired(true)
        );

        component.add(
                createServiceDependency()
                        .setService(Discoverer.class)
                        .setRequired(true)
        );

        component.add(
                createServiceDependency()
                        .setService(DroneInit.class)
                        .setRequired(true)
        );

        dependencyManager.add(component);
    }

    private Tactic createNewTactic() {
        try {
            Class<?> possibleTacticClass = Class.forName(System.getenv("DRONE_TACTIC"));
            Object possibleTactic = possibleTacticClass.newInstance();
            return (Tactic) possibleTactic;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException e) {
            logger.fatal(String.format("Could not find a tactic with name: %s. Please provide a fully classified class name that extends %s.java. The exception was: " +
                    "%s:%s", System.getenv("DRONE_TACTIC"), Tactic.class.getName(), e.getClass().getSimpleName(), e.getMessage()), e);
        }
        //By default return SimpleTactic
        return new SimpleTactic();
    }

    public List<ServiceDependency> getDroneComponents(DependencyManager dm) {
        String envComponents = System.getenv("DRONE_COMPONENTS");
        if (envComponents == null || "".equals(envComponents)) {
            envComponents = "radio,gps"; //Default components
        }
        envComponents += ",engine"; //A drone always needs an engine, so add that always to the componentlist
        logger.info("Create drone with the following components: " + envComponents);
        List<String> componentStrings = Arrays.stream(envComponents.split(",")).map(String::trim).filter(c -> !c.isEmpty()).collect(Collectors.toList());

        // Inject dependencies based on the defined components
        List<ServiceDependency> components = new ArrayList<>();
        if (componentStrings.contains("radar")) {
            components.add(dm.createServiceDependency()
                    .setService(Radar.class)
                    .setRequired(true)
            );
        }
        if (componentStrings.contains("gps")) {
            components.add(dm.createServiceDependency()
                    .setService(GPS.class)
                    .setRequired(true)
            );
        }
        if (componentStrings.contains("engine")) {
            components.add(dm.createServiceDependency()
                    .setService(Engine.class)
                    .setRequired(true)
            );
        }
        if (componentStrings.contains("gun")) {
            components.add(dm.createServiceDependency()
                    .setService(Gun.class)
                    .setRequired(true)
            );
        }
        if (componentStrings.contains("radio")) {
            components.add(dm.createServiceDependency()
                    .setService(Radio.class)
                    .setRequired(true)
            );
        }
        return components;
    }

}