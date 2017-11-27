package org.inaetics.dronessimulator.common;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TimeoutTimer {
    private final double timeout; //ms
    private long lastTime;


    /**
     * Check if a timeout has exceeded since the given starttime
     *
     * @param startTime a long which can be created using System.currentTimeMillis(), since this time the timeout is
     *                  counted.
     * @param timeout   a timeout in seconds
     * @return true if the timeout has been exceeded, false otherwise.
     */
    public static boolean isTimeExceeded(long startTime, double timeout) {

        return (startTime + timeout) < System.currentTimeMillis();
    }

    /**
     * Check if a timeout has exceeded since the given starttime
     *
     * @param startTime a LocalDateTime object with the time since when the timeout must be counted
     * @param timeout   a timeout in seconds
     * @return true if the timeout has been exceeded, false otherwise.
     */
    public static boolean isTimeExceeded(LocalDateTime startTime, long timeout) {
        return startTime.plusSeconds(timeout).isBefore(LocalDateTime.now());
    }

    public synchronized void reset() {
        lastTime = System.currentTimeMillis();
    }

    public synchronized boolean timeIsExceeded() {
        return isTimeExceeded(lastTime, timeout);
    }

}