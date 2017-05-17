package org.inaetics.dronessimulator.drone.tactic;

import org.inaetics.dronessimulator.common.D3Vector;
import org.inaetics.dronessimulator.drone.components.gps.GPS;
import org.inaetics.dronessimulator.drone.components.radar.Radar;

/**
 * Created by mart on 17-5-17.
 */
public class SimpleTactic extends Tactic {
    protected volatile Radar m_radar;
    protected volatile GPS m_gps;

    private static final int MAX_DEVIATION_POSTION = 400;
    private static final int MAX_VELOCITY = 20;
    private static final int MAX_ACCELERATION = 10;



    /**
     *  -- FUNCTIONS
     */
    D3Vector limit_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() > MAX_ACCELERATION){
            double correctionFactor = MAX_ACCELERATION / input.length();
            output = input.scale(correctionFactor);
        }
        return output;
    }

    D3Vector maximize_acceleration(D3Vector input){
        D3Vector output = input;
        // Prevent that the acceleration exteeds te maximum acceleration
        if(input.length() < MAX_ACCELERATION){
            double correctionFactor =  input.length() / MAX_ACCELERATION;
            output = input.scale(correctionFactor);
        }
        return output;
    }




}
