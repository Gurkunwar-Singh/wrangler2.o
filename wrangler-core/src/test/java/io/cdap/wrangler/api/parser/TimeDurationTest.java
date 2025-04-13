package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class TimeDurationTest {

  @Test
  public void testTimeUnitParsing() {
    TimeDuration ms = new TimeDuration("100ms");
    Assert.assertEquals(100 * 1_000_000L, ms.getNanoseconds());
    
    TimeDuration s = new TimeDuration("1.5s");
    Assert.assertEquals((long)(1.5 * 1_000_000_000), s.getNanoseconds());
    
    TimeDuration m = new TimeDuration("2m");
    Assert.assertEquals(2 * 60 * 1_000_000_000L, m.getNanoseconds());
    
    TimeDuration h = new TimeDuration("0.5h");
    Assert.assertEquals((long)(0.5 * 60 * 60 * 1_000_000_000), h.getNanoseconds());
    
    TimeDuration d = new TimeDuration("1d");
    Assert.assertEquals(24 * 60 * 60 * 1_000_000_000L, d.getNanoseconds());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new TimeDuration("100ns"); // Invalid unit (ns is not supported)
  }
  
  @Test(expected = NumberFormatException.class)
  public void testInvalidNumber() {
    new TimeDuration("abc ms"); // Invalid number
  }
}