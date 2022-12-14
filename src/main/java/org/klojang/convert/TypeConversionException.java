package org.klojang.convert;

import org.klojang.check.Check;

import java.io.File;
import java.time.temporal.Temporal;

import static java.lang.String.format;
import static org.klojang.util.ClassMethods.className;
import static org.klojang.util.ClassMethods.simpleClassName;
import static org.klojang.util.StringMethods.ellipsis;

/**
 * Indicates that a value could not be converted to the desired type.
 *
 * @author Ayco Holleman
 */
public final class TypeConversionException extends RuntimeException {

  /**
   * Returns a {@code TypeConversionException} informing the user that a type
   * conversion failed because the conversion function does not support the type of
   * the input value.
   *
   * @param inputValue the input value (may be {@code null}, indicating that
   *     {@code null} could not be converted to the target type)
   * @param targetType the target type
   * @return a {@code TypeConversionException} informing the user that a type
   *     conversion failed because the conversion function does not support the type
   *     of the input value
   */
  static TypeConversionException inputTypeNotSupported(Object inputValue,
      Class<?> targetType) {
    return new TypeConversionException(inputValue,
        targetType, "input type not supported");
  }

  /**
   * Returns a {@code TypeConversionException} informing the user that a type
   * conversion failed because the input value did not "fit into" the target type
   *
   * @param inputValue the input value (may be {@code null}, indicating that
   *     {@code null} could not be converted to the target type)
   * @param targetType the target type
   * @return a {@code TypeConversionException} informing the user that a type
   *     conversion failed because the input value did not "fit into" the target
   *     type
   */
  static final TypeConversionException targetTypeTooNarrow(Object inputValue,
      Class<?> targetType) {
    return new TypeConversionException(inputValue,
        targetType,
        "target type too narrow");
  }

  private final Object inputValue;
  private final Class<?> targetType;

  /**
   * Creates a new {@code TypeConversionException} for the specified input value and
   * target type. A standard message is generated from the two arguments
   *
   * @param inputValue the input value (may be {@code null}, indicating that
   *     {@code null} could not be converted to the target type)
   * @param targetType the target type
   */
  TypeConversionException(Object inputValue, Class<?> targetType) {
    super(defaultMessage(inputValue, targetType));
    this.inputValue = inputValue;
    this.targetType = targetType;
  }

  /**
   * Creates a new {@code TypeConversionException} for the specified input value and
   * target type.
   *
   * @param inputValue the input value (may be {@code null}, indicating that
   *     {@code null} could not be converted to the target type)
   * @param targetType the target type
   * @param msg a custom message or {@code String.format} message pattern
   * @param msgArgs zero or more message arguments
   */
  public TypeConversionException(Object inputValue,
      Class<?> targetType,
      String msg,
      Object... msgArgs) {
    super(defaultMessage(inputValue, targetType) + " *** " + format(msg, msgArgs));
    this.inputValue = inputValue;
    this.targetType = targetType;
  }

  /**
   * Returns the value for which the type conversion failed.
   *
   * @return the value for which the type conversion failed
   */
  public Object getInputValue() {
    return inputValue;
  }

  /**
   * Returns the target type of the type conversion.
   *
   * @return the target type of the type conversion
   */
  public Class<?> getTargetType() {
    return targetType;
  }

  private static String defaultMessage(Object obj, Class<?> type) {
    Check.notNull(type, "target type");
    if (obj == null) {
      return format("cannot convert null to %s", className(type));
    } else if (obj instanceof String s) {
      return format("cannot convert \"%s\" to %s",
          ellipsis(obj, 30),
          className(type));
    } else if (hasDecentToString(obj)) {
      return format("cannot convert (%s) %s to %s",
          simpleClassName(obj),
          ellipsis(obj, 30), className(type));
    }
    return format("cannot convert (%s) to %s", className(obj), className(type));
  }

  private static boolean hasDecentToString(Object obj) {
    return obj instanceof CharSequence
        || obj instanceof Number
        || obj instanceof Enum<?>
        || obj instanceof Temporal
        || obj instanceof File
        // add to taste ...
        ;
  }

}
