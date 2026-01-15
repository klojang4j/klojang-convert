package org.klojang.convert;

import org.klojang.check.Check;

import java.util.*;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import static org.klojang.check.CommonChecks.*;
import static org.klojang.convert.EnumParser.ParseInput.*;
import static org.klojang.convert.NumberMethods.isIntegral;

/**
 * Parses or converts values of various types into enum constants.
 *
 * @param <T>
 *     The type of the {@code enum}
 *
 * @author Ayco Holleman
 */
public final class EnumParser<T extends Enum<T>> {

  /**
   * Symbolic constants for what the value to be parsed or converted represents: the name of the constant, its
   * {@code toString()} value, the ordinal value of the constant, or the {@code enum} constant itself. The
   * latter may be useful in dynamic contexts where it is not known beforehand whether the incoming value
   * perhaps already is (or has been converted to) an enum constant. By default an {@code EnumParser} will try
   * to interpret the value as the constant's ordinal if it is an integral value, else as its name or
   * {@code toString()} value.
   */
  public enum ParseInput {
    /**
     * Indicates that the value to be converted is supposed to be the {@linkplain Enum#name() name} of an enum
     * constant.
     */
    NAME,
    /**
     * Indicates that the value to be converted is supposed to be the
     * {@linkplain Enum#ordinal() ordinal value} of an enum constant.
     */
    ORDINAL,
    /**
     * Indicates that the value to be converted is supposed to be the string representation of an enum
     * constant.
     */
    TO_STRING,
    /**
     * Indicates that the value to be converted is supposed to be already an enum constant, and it must be
     * returned <i>as-is</i> by the parser. This may be useful in dynamic contexts where it is not known
     * beforehand whether the incoming value perhaps already is (or has been converted to) an enum constant.
     */
    IDENTITY
  }

  /**
   * The default normalization function. Removes spaces, hyphens and underscores and returns an all-lowercase
   * string.
   */
  public static final UnaryOperator<String> DEFAULT_NORMALIZER =
      s -> Check.notNull(s).ok().strip().replaceAll("[-_ ]", "").toLowerCase();

  private final Class<T> enumClass;
  private final UnaryOperator<String> normalizer;
  private final Set<ParseInput> inputs;
  private final Map<String, T> lookups;

  /**
   * Creates an {@code EnumParser} for the specified enum class, using the {@link #DEFAULT_NORMALIZER}.
   *
   * @param enumClass
   *     The enum class
   */
  public EnumParser(Class<T> enumClass) {
    this(enumClass, DEFAULT_NORMALIZER);
  }

  /**
   * Creates an {@code EnumParser} for the specified enum class, using the specified {@code normalizer} to
   * normalize the strings to be parsed.
   *
   * @param enumClass
   *     the enum class managed by this {@code EnumParser}
   * @param normalizer
   *     the normalization function
   */
  public EnumParser(Class<T> enumClass, UnaryOperator<String> normalizer) {
    this(enumClass, normalizer, EnumSet.of(NAME, ORDINAL, TO_STRING));
  }

  /**
   * Creates an {@code EnumParser} for the specified enum class, using the specified {@code normalizer} to
   * normalize the strings to be parsed.
   *
   * @param enumClass
   *     the enum class managed by this {@code EnumParser}
   * @param normalizer
   *     the normalization function
   * @param inputs
   *     the aspects of an enum constant that the values to be converted may represent (the constant's name,
   *     ordinal value, string representation, or the constant itself).
   */
  public EnumParser(
      Class<T> enumClass, UnaryOperator<String> normalizer,
      Set<ParseInput> inputs) {
    this.enumClass = Check.notNull(enumClass, "enumClass").ok();
    this.normalizer = Check.notNull(normalizer, "normalizer").ok();
    this.inputs = Check.that(inputs, "parseTargets").is(deepNotEmpty()).ok();
    HashMap<String, T> tmp = new HashMap<>();
    if (inputs.contains(NAME)) {
      for (T e : enumClass.getEnumConstants()) {
        tmp.put(normalize(e.name()), e);
      }
    }
    if (inputs.contains(TO_STRING)) {
      Set<String> duplicates = HashSet.newHashSet(enumClass.getEnumConstants().length);
      for (T e : enumClass.getEnumConstants()) {
        Check.that(e.toString()).isNot(in(), duplicates, "Duplicate toString() value: \"${arg}\"")
            .then(str -> tmp.put(normalize(str), e));
      }
    }
    this.lookups = tmp;
  }

  /**
   * Parses or converts the specified value into an enum constant.
   *
   * @param value
   *     The value to be parsed or converted
   *
   * @return The enum constant
   *
   * @throws TypeConversionException
   *     If the value was {@code null} or could not be mapped to one of the enum's constants.
   */
  public T parse(Object value) throws TypeConversionException {
    if (value != null) {
      if (inputs.contains(ORDINAL) && isIntegral(value.getClass())) {
        int ordinal = NumberMethods.convert((Number) value, Integer.class);
        return Check.that(ordinal)
            .is(indexOf(), enumClass.getEnumConstants(), noSuchConstant(value))
            .mapToObj(x -> enumClass.getEnumConstants()[x]);
      } else if (inputs.contains(IDENTITY) && enumClass.isInstance(value)) {
        return enumClass.cast(value);
      }
    }
    String key = normalize(Objects.toString(value));
    return Check.that(lookups.get(key)).is(notNull(), noSuchConstant(value)).ok();
  }

  private String normalize(String s) {
    try {
      return normalizer.apply(s);
    } catch (TypeConversionException tce) {
      throw tce;
    } catch (Throwable t) {
      throw new TypeConversionException(s, enumClass, t.getMessage());
    }
  }

  private Supplier<TypeConversionException> noSuchConstant(Object value) {
    return () -> new TypeConversionException(value, enumClass);
  }


}
