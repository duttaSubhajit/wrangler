package io.cdap.wrangler.api.parser;

import org.junit.Test;
import static org.junit.Assert.*;

public class TimeDurationTest {
    @Test
    public void testTimeDurationParsing() {
        assertEquals(1000L, new TimeDuration("1s").getMilliseconds());
        assertEquals(1500L, new TimeDuration("1.5s").getMilliseconds());
        assertEquals(60000L, new TimeDuration("1m").getMilliseconds());
        assertEquals(100L, new TimeDuration("100ms").getMilliseconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeDuration() {
        new TimeDuration("invalid");
    }
}