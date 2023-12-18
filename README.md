# Klojang Invoke

_Klojang Convert_ contains classes aimed at converting values of one type into values of
another type. The
[Morph](https://klojang4j.github.io/klojang-convert/api/org.klojang.convert/org/klojang/convert/Morph.html)
class is capable of converting a wide variety of input types to a wide variety of
output types.

_Klojang Invoke_ is mainly intended as a supporting library for
[Klojang Invoke](https://github.com/klojang4j/klojang-invoke) where it is used to enable
loosely typed bean writing.

## Getting Started

To use _Klojang Convert_, add the following dependency to your Maven POM file:

```xml

<dependency>
    <groupId>org.klojang</groupId>
    <artifactId>klojang-convert</artifactId>
    <version>1.0.6</version>
</dependency>
```

or Gradle build script:

```
implementation group: 'org.klojang', name: 'klojang-convert', version: '1.0.6'
```

## Documentation

The **Javadocs** for _Klojang Convert_ can be
found [here](https://klojang4j.github.io/klojang-convert/api).

The latest **coverage reports** can be
found [here](https://klojang4j.github.io/klojang-convert/coverage).

The latest **vulnerabilities report** can be found
[here](https://klojang4j.github.io/klojang-convert/vulnerabilities/dependency-check-report.html).

## Conversion Table

| Input Value (val) | Target Type | Output Value / Transformation                              | 
|-------------------|-------------|------------------------------------------------------------|
| `null`            | primitive   | primitive default (0, 0L '\0', `false`, etc.)              |
| `null`            | object      | `null`                                                     |
| instance of `T`   | `T`         | `(T) val`                                                  |
| `Integer`         | `int`       | `(int) val`  (and so on for other primitive wrappers)      |
| `byte[]`          | `String`    | `new String(val, StandardCharsets.UTF_8)`                  |
| `char[]`          | `String`    | `new String(val)`                                          |
| ?                 | `String`    | `val.toString()`                                           |
| `T[]`             | `U[]`       | for-each `t`: `Morph.convert(t, U.class)` (recursive call) |
| `Collection<T>`   | `U[]`       | for-each `t`: `Morph.convert(t, U.class)` (recursive call) |
| `IntList` *)      | `U[]`       | for-each `i`: `Morph.convert(i, U.class)` (recursive call) |
| `String`          | `byte[]`    | `val.getBytes(StandardCharsets.UTF_8)`                     |
| `String`          | `char[]`    | `val.toCharArray()`                                        |
| ?                 | `U[]`       | `new U[] { val }`                                          |


*) [org.klojang.util.collection.IntList](https://klojang4j.github.io/klojang-util/api/org.klojang.util/org/klojang/util/collection/IntList.html)


