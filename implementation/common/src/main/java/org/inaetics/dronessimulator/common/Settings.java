package org.inaetics.dronessimulator.common;

import org.inaetics.dronessimulator.common.vector.D3Vector;

import java.time.temporal.ChronoUnit;

public class Settings {
    public static final String ETCD_HOST = v("ETCD_HOST", "localhost");
    public static final String ETCD_PORT = v("ETCD_PORT", "4001");
    public static final double ARENA_HEIGHT = Float.parseFloat(v("ARENA_HEIGHT", "100"));
    public static final double ARENA_DEPTH = Float.parseFloat(v("ARENA_DEPTH", "800"));
    public static final double ARENA_WIDTH = Float.parseFloat(v("ARENA_WIDTH", "800"));
    public static final D3Vector ARENA = new D3Vector(ARENA_WIDTH, ARENA_DEPTH, ARENA_HEIGHT);
    public static final GameMode GAME_MODE = GameMode.valueOf(v("GAME_MODE", "DEATHMATCH"));
    public static final long TICK_TIME = 33;//ms
    /**
     * The max acceleration of this engine in m/s^2
     */
    public static final double MAX_DRONE_ACCELERATION = Double.parseDouble(v("MAX_DRONE_ACCELERATION", "10"));
    /**
     * The max velocity of this engine in m/s
     */
    public static final double MAX_DRONE_VELOCITY = Double.parseDouble(v("MAX_DRONE_VELOCITY", "20"));


    private static String v(String variableName, String defaultValue) {
        String value = System.getenv(variableName);

        return value != null ? value : defaultValue;
    }

    private Settings() {
        throw new IllegalStateException("Utility class");
    }

    public static double getTickTime(ChronoUnit temporalUnit) {
        switch (temporalUnit) {
            case NANOS:
                return getTickTime(ChronoUnit.MICROS) * 1000d;
            case MICROS:
                return getTickTime(ChronoUnit.MILLIS) * 1000d;
            case MILLIS:
                return TICK_TIME;
            case SECONDS:
                return getTickTime(ChronoUnit.MILLIS) / 1000d;
            case MINUTES:
                return getTickTime(ChronoUnit.SECONDS) / 60d;
            case HOURS:
                return getTickTime(ChronoUnit.MINUTES) / 60d;
            default:
                throw new IllegalArgumentException(temporalUnit.name() + " is not (yet) supported");
        }
    }
}
