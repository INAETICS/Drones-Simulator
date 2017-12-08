package org.inaetics.dronessimulator.common;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class TimeoutTimerTest {
    @Test
    public void isTimeExceeded() throws Exception {
        long testDelta = 1000;
        long oldTime = System.currentTimeMillis() - testDelta;

        Assert.assertTrue(TimeoutTimer.isTimeExceeded(oldTime, testDelta * 0.5));
        Assert.assertFalse(TimeoutTimer.isTimeExceeded(oldTime, testDelta * 2));
    }

    @Test
    public void isTimeExceeded1() throws Exception {
        long testDelta = 1000;
        LocalDateTime oldTime = LocalDateTime.now().minusSeconds(testDelta);

        //Use long as timeout
        Assert.assertTrue(TimeoutTimer.isTimeExceeded(oldTime, (long) (testDelta * 0.5)));
        Assert.assertFalse(TimeoutTimer.isTimeExceeded(oldTime, testDelta * 2));

        //Use double as timeout
        Assert.assertTrue(TimeoutTimer.isTimeExceeded(oldTime, (testDelta * 0.5)));
        Assert.assertFalse(TimeoutTimer.isTimeExceeded(oldTime, testDelta * 2.0));

        //A very small number
        double testDeltaDouble = 0.005;
        oldTime = LocalDateTime.now().minusNanos((long) (testDeltaDouble * 1e9));
        Assert.assertTrue(TimeoutTimer.isTimeExceeded(oldTime, (testDeltaDouble * 0.5)));
        Assert.assertFalse(TimeoutTimer.isTimeExceeded(oldTime, testDeltaDouble * 2.0));
    }

}