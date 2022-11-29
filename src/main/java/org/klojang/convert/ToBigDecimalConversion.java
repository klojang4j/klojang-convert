package org.klojang.convert;

import static org.klojang.convert.NumberMethods.toBigDecimal;

import java.math.BigDecimal;

final class ToBigDecimalConversion {

  private ToBigDecimalConversion() {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unused")
  static boolean isLossless(Number n) {
    return true;
  }

  static BigDecimal exec(Number n) {
    return toBigDecimal(n);
  }

}
