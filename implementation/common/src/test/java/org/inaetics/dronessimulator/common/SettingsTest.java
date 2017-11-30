package org.inaetics.dronessimulator.common;

import org.junit.Assert;
import org.junit.Test;

import java.time.temporal.ChronoUnit;

public class SettingsTest {
    @Test
    public void getTickTime() throws Exception {
        Assert.assertEquals(Settings.getTickTime(ChronoUnit.MILLIS), Settings.TICK_TIME, 0.1);
        Assert.assertEquals(Settings.getTickTime(ChronoUnit.SECONDS), Settings.TICK_TIME / 1000, 0.1);
        Assert.assertEquals(Settings.getTickTime(ChronoUnit.MICROS), Settings.TICK_TIME * 1000, 0.1);
    }
}