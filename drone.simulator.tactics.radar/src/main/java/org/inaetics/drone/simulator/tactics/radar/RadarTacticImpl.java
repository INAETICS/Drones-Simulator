package org.inaetics.drone.simulator.tactics.radar;

import org.inaetics.drone.simulator.api.drone.DroneTactic;
import org.inaetics.drone.simulator.api.engine.Engine;
import org.inaetics.drone.simulator.api.gps.Gps;
import org.inaetics.drone.simulator.api.radar.Detection;
import org.inaetics.drone.simulator.api.radar.DetectionListener;
import org.inaetics.drone.simulator.api.radar.Radar;
import org.inaetics.drone.simulator.api.radio.Radio;
import org.inaetics.drone.simulator.api.radio.RadioMessageListener;
import org.osgi.service.log.LogService;

import java.util.List;
import java.util.UUID;

public class RadarTacticImpl implements DroneTactic, DetectionListener, RadioMessageListener {

    private final UUID droneId = UUID.randomUUID();

    //services
    //note the felix dm way injecting private fieldsprivate volatile Radar radar; //note the felix dm way injecting private fields
    private volatile LogService log;
    private volatile Radar radar;
    private volatile Engine engine;
    private volatile Gps gps;
    private volatile Radio radio;

    public RadarTacticImpl() {
    }

    @Override
    public UUID getDroneId() {
        return this.droneId;
    }

    @Override
    public void reset() {
        //TODO
    }

    @Override
    public void pause() {
        //TODO
    }

    @Override
    public void cont() {
        //TODO
    }

    @Override
    public void step(double time) {
        //TODO implement tactic, e.g. fly circle and send detection around
    }

    @Override
    public void handleRadioMessage(Object msg, double msgTimeValidity) {
        //TODO
    }

    @Override
    public void processDetections(UUID sensorSource, List<Detection> detections) {

    }
}
