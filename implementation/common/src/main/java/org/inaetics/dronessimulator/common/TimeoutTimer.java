package org.inaetics.dronessimulator.common;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TimeoutTimer {
    private final double timeout; //ms
    private long lastTime;

    public static boolean isTimeExceeded(long startTime, double timeout) {
        return (startTime + timeout) < System.currentTimeMillis();
    }
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