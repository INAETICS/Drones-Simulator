package org.inaetics.drone.simulator.radio;

public interface RadioMessageListener {

    public void handleRadioMessage(Object msg, double  msgTimeValidity);
}
