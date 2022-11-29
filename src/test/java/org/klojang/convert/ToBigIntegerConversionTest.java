package org.klojang.convert;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class ToBigIntegerConversionTest {

  @Test
  public void isLossless00() {
    assertFalse(ToBigIntegerConversion.isLossless(0.89));
    assertFalse(ToBigIntegerConversion.isLossless(-.3F));
    assertTrue(ToBigIntegerConversion.isLossless(-.0F));
    assertTrue(ToBigIntegerConversion.isLossless(Double.valueOf("0.000")));
    assertTrue(ToBigIntegerConversion.isLossless(333));
  }

  @Test
  public void exec00() {
    assertEquals(BigInteger.valueOf(33L), ToBigIntegerConversion.exec((short) 33));
    assertEquals(BigInteger.valueOf(0), ToBigIntegerConversion.exec(Double.valueOf("0.000")));
  }

  @Test(expected = TypeConversionException.class)
  public void exec01() {
    ToBigIntegerConversion.exec(Double.valueOf("0.0002"));
  }

}