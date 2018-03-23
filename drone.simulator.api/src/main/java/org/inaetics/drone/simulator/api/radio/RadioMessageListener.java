package org.inaetics.drone.simulator.api.radio;

public interface RadioMessageListener {

    public void handleRadioMessage(Object msg, double msgTimeValidity);
}
