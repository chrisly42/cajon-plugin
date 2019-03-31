# Cajon - Concise AssertJ Optimizing Nitpicker

Cajon is an IntelliJ IDEA Plugin for shortening and optimizing AssertJ assertions.

## Why?

First, code is easier to read, when it is concise and reflects the intention clearly.
AssertJ has plenty of different convenience methods that describing various intentions precisely.
Why write longer, more complex code that can be expressed in brevity?

Second, AssertJ is able to output more meaningful descriptions when an assertion fails.
This makes finding bugs and fixing failed tests more efficient.

For example:

```
assertThat(collection.size()).isEqualTo(5);
```

If the collection has more or less than 5 elements, the assertion will fail, but will not
tell you about the contents, making it hard to guess what went wrong.

Instead, if you wrote the same assertion the following way:

```
assertThat(collection).hasSize(5);
```

Then AssertJ would tell you the contents of the collection on failure.

## Conversion of JUnit assertions to AssertJ

The plugin also supports the conversion of the most common JUnit 4 assertions to AssertJ.

## Implemented

- AssertThatObjectIsNull
  ```
  from: assertThat(object).isEqualTo(null);
    to: assertThat(object).isNull();
  ```
- AssertThatObjectIsNotNull
  ```
  from: assertThat(object).isNotEqualTo(null);  
    to: assertThat(object).isNotNull();
  ```
- AssertThatBooleanIsTrueOrFalse
  ```
  from: assertThat(booleanValue).isEqualTo(true/false/Boolean.TRUE/Boolean.FALSE);  
    to: assertThat(booleanValue).isTrue()/isFalse();
  ```
- AssertThatStringIsEmpty
  ```
  from: assertThat(charSequence/string).isEqualTo("");
  from: assertThat(charSequence/string).hasSize(0);
    to: assertThat(charSequence/string).isEmpty();
  ```
- AssertThatEnumerableIsEmpty
  ```
  from: assertThat(enumerable).hasSize(0);
    to: assertThat(enumerable).isEmpty();
- JUnitAssertToAssertJ
  ```
  assertTrue(condition);
  assertTrue(message, condition);
  assertFalse(condition);
  assertFalse(message, condition);
  assertNull(object);
  assertNull(message, object);
  assertNonNull(object);
  assertNonNull(message, object);
  assertEquals(expected, actual);
  assertEquals(message, expected, actual);
  assertEquals(expectedDoubleOrFloat, actualDoubleOrFloat, delta);
  assertEquals(message, expectedDoubleOrFloat, actualDoubleOrFloat, delta);
  assertNotEquals(unexpected, actual);
  assertNotEquals(message, unexpected, actual);
  assertNotEquals(unexpectedDoubleOrFloat, actualDoubleOrFloat, delta);
  assertNotEquals(message, unexpectedDoubleOrFloat, actualDoubleOrFloat, delta);
  assertSame(expected, actual);
  assertSame(message, expected, actual);
  assertNotSame(unexpected, actual);
  assertNotSame(message, unexpected, actual);
  assertArrayEquals(expected, actual);
  assertArrayEquals(message, expectedArray, actualArray);
  assertArrayEquals(expectedDoubleOrFloatArray, actualDoubleOrFloatArray, delta);
  assertArrayEquals(message, expectedDoubleOrFloatArray, actualDoubleOrFloatArray, delta);
  ```

## TODO
- AssertThatArrayHasLiteralSize
  ```
  from: assertThat(array.length).isEqualTo(literal);  literal > 0
    to: assertThat(array).hasSize(literal);
  ```
- AssertThatArrayHasEqualSize
  ```
  from: assertThat(array.length).isEqualTo(anotherArray.length);
    to: assertThat(array).hasSameSizeAs(anotherArray);
  from: assertThat(array.length).isEqualTo(iterable.size());
    to: assertThat(array).hasSameSizeAs(iterable);
  ```
- AssertThatArrayIsEmpty
  ```
  from: assertThat(array.length).isEqualTo(0);
  from: assertThat(array.length).isLessThanOrEqualTo(0);
  from: assertThat(array.length).isLessThan(1);
  from: assertThat(array).hasSize(0);
    to: assertThat(array).isEmpty();
  ```
- AssertThatArrayIsNotEmpty
  ```
  from: assertThat(array.length).isGreaterThan(0);
    to: assertThat(array).isNotEmpty();
  ```
- AssertThatCollectionHasLiteralSize
  ```
  from: assertThat(collection.size()).isEqualTo(literal);  literal > 0
    to: assertThat(collection).hasSize(literal);
  ```
- AssertThatCollectionHasEqualSize
  ```
  from: assertThat(collection.size()).isEqualTo(anotherArray.length);
    to: assertThat(collection).hasSameSizeAs(anotherArray);
  from: assertThat(collection.size()).isEqualTo(anotherCollection.size());
    to: assertThat(collection).hasSameSizeAs(anotherCollection);
  ```
- AssertThatCollectionIsNotEmpty
  ```
  from: assertThat(collection.size()).isGreaterThan(0);
  from: assertThat(collection.size()).isGreaterThanOrEqualTo(1);
    to: assertThat(collection).isNotEmpty();
  ```
- AssertThatCollectionIsEmpty
  ```
  from: assertThat(collection.size()).isEqualTo(0);
  from: assertThat(collection.size()).isLessThanOrEqualTo(0);
  from: assertThat(collection.size()).isLessThan(1);
  from: assertThat(collection).hasSize(0);
    to: assertThat(collection).isEmpty();
  ```
- AssertThatGuavaOptionalContains