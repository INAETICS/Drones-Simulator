package org.inaetics.drone.simulator.components.drone;

import org.inaetics.drone.simulator.api.drone.DroneTactic;
import org.inaetics.drone.simulator.api.mission.MissionInfo;
import org.inaetics.drone.simulator.spi.costs.ComponentCost;
import org.inaetics.drone.simulator.spi.events.DroneAnnouncement;
import org.osgi.service.log.LogService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for sending drone announcements to the game engine
 */
//TODO add publisher dependency to send drone announcment event
public class DroneManager {

    private final String teamName;

    private final long TIMEOUT = 1; //1 sec
    private final Thread updateThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(!Thread.interrupted()) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // reset the interrupt state
                }
            }
        }
    }, "drone.manager.update.thread"
    );

    //service deps
    private volatile LogService log;
    private volatile MissionInfo info;
    private volatile DroneTactic drone;
    private final List<ComponentCost> componentCosts = new CopyOnWriteArrayList<>();

    public DroneManager(String teamName) {
        this.teamName = teamName;
    }

    public void addComponentCost(ComponentCost cc) {
        componentCosts.add(cc);
    }

    public void removeComponentCost(ComponentCost cc) {
        componentCosts.remove(cc);
    }

    public void start() {
        this.updateThread.start();
    }

    public void stop() {
        this.updateThread.interrupt();
        try {
            this.updateThread.join(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        double now =  TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()); //TODO use info.getCurrentTime
        UUID id = drone.getDroneId();

        List<ComponentCost> costs = this.componentCosts;
        double cost = 0;
        List<String> components = new ArrayList<>(costs.size());
        for (ComponentCost cc : costs) {
            cost += cc.getCost();
            components.add(cc.getComponentName());
        }

        DroneAnnouncement ann = new DroneAnnouncement(now, id, this.teamName, cost, components);
        //TODO send component using pubsub
    }

    public String cost() {
        StringBuilder b = new StringBuilder();

        b.append("components for drone '" + drone.getDroneId().toString() + "' for team '" + this.teamName + "':\n");
        List<ComponentCost> costs = this.componentCosts;
        double cost = 0;
        for (ComponentCost cc: costs) {
            b.append("\t-" + cc.getComponentName() + "\n");
            cost += cc.getCost();
        }
        b.append("Total cost: " + cost + "\n");
        return b.toString();
    }
}
