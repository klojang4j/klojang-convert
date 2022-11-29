package org.klojang.convert;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.atomic.LongAdder;

import static java.math.BigInteger.ONE;
import static org.junit.Assert.*;
import static org.klojang.convert.NumberMethods.MAX_DOUBLE_BD;
import static org.klojang.convert.ToFloatConversion.BIG_MIN_FLOAT;

public class ToLongConversionTest {

  @Test
  public void testDouble00() {
    assertFalse(ToLongConversion.isLossless(Double.MAX_VALUE));
    assertFalse(ToLongConversion.isLossless(Double.MIN_VALUE));
  }

  @Test
  public void testDouble01() {
    assertTrue(ToLongConversion.isLossless((double) Long.MAX_VALUE));
    assertTrue(ToLongConversion.isLossless((double) Long.MIN_VALUE));
  }

  @Test
  public void testBigInteger00() {
    assertTrue(ToLongConversion.isLossless(NumberMethods.MAX_LONG_BI));
  }

  @Test
  public void testBigInteger01() {
    assertTrue(ToLongConversion.isLossless(NumberMethods.MIN_LONG_BI));
  }

  @Test
  public void testBigDecimal00() {
    BigDecimal bd = new BigDecimal(NumberMethods.MAX_LONG_BI.add(ONE));
    assertFalse(ToLongConversion.isLossless(bd));
  }

  @Test
  public void testBigDecimal01() {
    BigDecimal bd = new BigDecimal(NumberMethods.MIN_LONG_BI.add(ONE));
    assertTrue(ToLongConversion.isLossless(bd));
  }

  @Test(expected = TypeConversionException.class)
  public void execDouble00() {
    ToLongConversion.exec(Double.MAX_VALUE);
  }

  @Test
  public void execDouble01() {
    assertEquals(Long.MIN_VALUE,
        (long) ToLongConversion.exec((double) (Long.MIN_VALUE + 1)));
  }

  @Test(expected = TypeConversionException.class)
  public void execBigInteger00() {
    ToLongConversion.exec(MAX_DOUBLE_BD.toBigInteger());
  }

  @Test
  public void execBigDecimal00() {
    BigDecimal two = BigDecimal.ONE.add(BigDecimal.ONE);
    BigDecimal overflow = two.multiply(BigDecimal.valueOf(Long.MAX_VALUE));
    BigDecimal bd = BIG_MIN_FLOAT.divide(two);
    assertFalse(ToLongConversion.isLossless(bd));
  }

  @Test
  public void execBigDecimal01() {
    assertEquals(100, (long) ToLongConversion.exec(BigDecimal.valueOf(100D)));
  }

  @Test(expected = TypeConversionException.class)
  public void execLongAdder00() {
    ToLongConversion.exec(new LongAdder());
  }

}
