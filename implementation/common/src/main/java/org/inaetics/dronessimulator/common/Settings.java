package org.inaetics.dronessimulator.common;

public class Settings {
    public static final String ETCD_HOST = v("ETCD_HOST", "localhost");
    public static final String ETCD_PORT = v("ETCD_PORT", "4001");
    public static final double ARENA_HEIGHT = Float.parseFloat(v("ARENA_HEIGHT", "100"));
    public static final double ARENA_DEPTH = Float.parseFloat(v("ARENA_DEPTH", "800"));
    public static final double ARENA_WIDTH = Float.parseFloat(v("ARENA_WIDTH", "800"));
    public static final GameMode GAME_MODE = GameMode.valueOf(v("GAME_MODE", "DEATHMATCH"));
    public static final double TICK_TIME = 1000.0;//ms


    private static String v(String variableName, String defaultValue) {
        String value = System.getenv(variableName);

        return value != null ? value : defaultValue;
    }
}
