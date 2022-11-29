package org.klojang.convert;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.LongAdder;

import static java.math.BigDecimal.ONE;
import static org.junit.Assert.*;
import static org.klojang.convert.NumberMethods.MAX_DOUBLE_BD;
import static org.klojang.convert.ToFloatConversion.BIG_MAX_FLOAT;
import static org.klojang.convert.ToFloatConversion.BIG_MIN_FLOAT;

public class ToFloatConversionTest {

  @Test
  public void testDouble00() {
    assertFalse(ToFloatConversion.isLossless(Double.MAX_VALUE));
    assertFalse(ToFloatConversion.isLossless(Double.MIN_VALUE));
  }

  @Test
  public void testDouble01() {
    assertTrue(ToFloatConversion.isLossless((double) Float.MAX_VALUE));
    assertTrue(ToFloatConversion.isLossless((double) Float.MIN_VALUE));
  }

  @Test
  public void testBigInteger00() {
    BigInteger bi = MAX_DOUBLE_BD.toBigInteger();
    assertFalse(ToFloatConversion.isLossless(bi));
  }

  @Test
  public void testBigInteger01() {
    BigInteger bi = BIG_MAX_FLOAT.toBigInteger().subtract(BigInteger.ONE);
    assertTrue(ToFloatConversion.isLossless(bi));
  }

  @Test
  public void testBigDecimal00() {
    BigDecimal bd = BIG_MAX_FLOAT.add(ONE);
    assertFalse(ToFloatConversion.isLossless(bd));
  }

  @Test
  public void testBigDecimal01() {
    BigDecimal bd = BIG_MAX_FLOAT.subtract(ONE);
    assertTrue(ToFloatConversion.isLossless(bd));
  }

  @Test
  public void testBigDecimal02() {
    BigDecimal two = ONE.add(ONE);
    BigDecimal bd = BIG_MIN_FLOAT.divide(two);
    assertFalse(ToFloatConversion.isLossless(bd));
  }

  @Test
  public void testBigDecimal03() {
    BigDecimal two = ONE.add(ONE);
    BigDecimal bd = BIG_MIN_FLOAT.multiply(two);
    assertTrue(ToFloatConversion.isLossless(bd));
  }

  @Test(expected = TypeConversionException.class)
  public void execDouble00() {
    ToFloatConversion.exec(Double.MAX_VALUE);
  }

  @Test
  public void execDouble01() {
    assertEquals(Float.MIN_VALUE,
        ToFloatConversion.exec((double) Float.MIN_VALUE),
        0F);
  }

  @Test(expected = TypeConversionException.class)
  public void execBigInteger00() {
    ToFloatConversion.exec(MAX_DOUBLE_BD.toBigInteger());
  }

  @Test
  public void execBigInteger01() {
    assertEquals(Float.MAX_VALUE - 1F,
        ToFloatConversion.exec(BIG_MAX_FLOAT.toBigInteger()
            .subtract(BigInteger.ONE)), 0F);
  }

  @Test
  public void execBigDecimal00() {
    BigDecimal two = ONE.add(ONE);
    BigDecimal bd = BIG_MIN_FLOAT.divide(two);
    assertFalse(ToFloatConversion.isLossless(bd));
  }

  @Test
  public void execBigDecimal01() {
    BigDecimal two = ONE.add(ONE);
    BigDecimal bd = BIG_MIN_FLOAT.multiply(two);
    assertEquals(2F * Float.MIN_VALUE, ToFloatConversion.exec(bd), 0F);
  }

  @Test(expected = TypeConversionException.class)
  public void execLongAdder00() {
    ToFloatConversion.exec(new LongAdder());
  }

}
