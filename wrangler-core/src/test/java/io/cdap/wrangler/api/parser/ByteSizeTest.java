package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class ByteSizeTest {

  @Test
  public void testByteUnitParsing() {
    ByteSize kb = new ByteSize("10kb");
    Assert.assertEquals(10 * 1024L, kb.getBytes());
    
    ByteSize mb = new ByteSize("1.5MB");
    Assert.assertEquals((long)(1.5 * 1024 * 1024), mb.getBytes());
    
    ByteSize gb = new ByteSize("2GB");
    Assert.assertEquals(2 * 1024 * 1024 * 1024L, gb.getBytes());
    
    ByteSize tb = new ByteSize("0.5TB");
    Assert.assertEquals((long)(0.5 * 1024 * 1024 * 1024 * 1024), tb.getBytes());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new ByteSize("100XB"); // Invalid unit
  }
  
  @Test(expected = NumberFormatException.class)
  public void testInvalidNumber() {
    new ByteSize("abc KB"); // Invalid number
  }
}