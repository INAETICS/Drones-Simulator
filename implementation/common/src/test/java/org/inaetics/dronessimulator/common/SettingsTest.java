package org.inaetics.dronessimulator.common;

import org.junit.Assert;
import org.junit.Test;

import java.time.temporal.ChronoUnit;

public class SettingsTest {
    @Test
    public void getTickTime() throws Exception {
        Assert.assertEquals(Settings.TICK_TIME, Settings.getTickTime(ChronoUnit.MILLIS), 0.1);
        Assert.assertEquals(Settings.TICK_TIME / 1000, Settings.getTickTime(ChronoUnit.SECONDS), 0.1);
        Assert.assertEquals(Settings.TICK_TIME * 1000, Settings.getTickTime(ChronoUnit.MICROS), 0.1);
    }
}