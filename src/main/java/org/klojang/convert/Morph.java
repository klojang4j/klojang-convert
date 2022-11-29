package org.klojang.convert;

import org.klojang.check.Check;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.klojang.check.CommonChecks.notNull;
import static org.klojang.util.ClassMethods.*;

/**
 * Performs a wide variety of type conversions.
 *
 * @param <T> The type to which incoming values will be converted
 * @author Ayco Holleman
 * @see NumberMethods#convert(Number, Class)
 * @see NumberMethods#parse(String, Class)
 * @see Bool
 * @see EnumParser
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Morph<T> {

  /**
   * Converts the specified object to the specified type.
   *
   * @param <U> The target type
   * @param obj The value to convert
   * @param targetType The {@code Class} object corresponding to the target type
   * @return The converted value
   * @throws TypeConversionException If the conversion did not succeed
   */
  public static <U> U convert(Object obj, Class<U> targetType) {
    return new Morph<>(targetType).convert(obj);
  }

  private final Class<T> targetType;

  /**
   * Creates a new {@code Morph} instance that will convert values to the specified
   * type.
   *
   * @param targetType The type to which to convert values
   */
  public Morph(Class<T> targetType) {
    this.targetType = Check.notNull(targetType).ok();
  }

  /**
   * Converts the specified object into an instance of the type specified through the
   * constructor.
   *
   * @param obj The value to convert
   * @return An instance of the target type
   * @throws TypeConversionException If the conversion did not succeed
   */
  public T convert(Object obj) throws TypeConversionException {
    Class<T> toType = this.targetType;
    if (obj == null) {
      return getTypeDefault(toType);
    } else if (toType.isInstance(obj)) {
      return (T) obj;
    } else if (isAutoUnboxedAs(obj.getClass(), toType)) {
      return (T) obj;
    } else if (toType == String.class) {
      if (obj instanceof byte[] bytes) {
        return (T) new String(bytes, StandardCharsets.UTF_8);
      } else if (obj instanceof char[] chars) {
        return (T) new String(chars);
      }
      return (T) obj.toString();
    } else if (toType.isArray()) {
      return MorphToArray.morph(obj, toType);
    } else if (isSubtype(toType, Collection.class)) {
      return MorphToCollection.morph(obj, toType);
    }
    Class myType = obj.getClass();
    if (myType.isArray()) {
      return Array.getLength(obj) == 0
          ? getTypeDefault(toType)
          : convert(Array.get(obj, 0), toType);
    } else if (isSubtype(myType, Collection.class)) {
      Collection coll = (Collection) obj;
      return coll.isEmpty()
          ? getTypeDefault(toType)
          : convert(coll.iterator().next(), toType);
    }
    Object out = MorphToNumber.morph(obj, toType);
    if (out != null) {
      return (T) out;
    } else if (toType.isEnum()) {
      return (T) MorphToEnum.morph(obj, toType);
    }
    throw new TypeConversionException(obj, toType);
  }

  static String stringify(Object obj) {
    return Check.that(obj.toString())
        .is(notNull(), "obj.toString() must not return null")
        .ok();
  }

}
