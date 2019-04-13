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

## Usage

The plugin will report inspections in your opened editor file as warnings.
You can then quick-fix these with your quick-fix hotkey (usually Alt-Return or Opt-Return).

Or, you can use the "Run Inspection by Name..." action to run one inspection on a bigger scope (e.g. the whole project).

You can toggle the various inspections in the Settings/Editor/Inspections in the AssertJ group.

## Implemented inspections

- AssertThatObjectIsNullOrNotNull
  ```
  from: assertThat(object).isEqualTo(null);
    to: assertThat(object).isNull();

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
  ```
  
- AssertThatSize
  ```
  from: assertThat(array.length).isEqualTo(0);
  from: assertThat(array.length).isLessThanOrEqualTo(0);
  from: assertThat(array.length).isLessThan(1);
  from: assertThat(array.length).isZero();
    to: assertThat(array).isEmpty();

  from: assertThat(array.length).isGreaterThan(0);
  from: assertThat(array.length).isGreaterThanOrEqualTo(1);
  from: assertThat(array.length).isNotZero();
    to: assertThat(array).isNotEmpty();

  from: assertThat(array.length).isEqualTo(anotherArray.length);
    to: assertThat(array).hasSameSizeAs(anotherArray);
  ```

  with AssertJ 13.2.0 or higher

  ```
  from: assertThat(array.length).isLessThanOrEqualTo(expression);
    to: assertThat(array).hasSizeLessThanOrEqualTo(expression);
    
  from: assertThat(array.length).isLessThan(expression);
    to: assertThat(array).hasSizeLessThan(expression);

  from: assertThat(array.length).isGreaterThan(expression);
    to: assertThat(array).hasSizeGreaterThan(expression);

  from: assertThat(array.length).isGreaterThanOrEqualTo(expression);
    to: assertThat(array).hasSizeGreaterThanOrEqualTo(expression);
  ```

  and analogously for collections...

- AssertThatBinaryExpressionIsTrueOrFalse
  ```
  from: assertThat(primActual == primExpected).isTrue();
    to: assertThat(primActual).isEqualTo(primExpected);

  from: assertThat(10 < primActual).isNotEqualTo(false);
    to: assertThat(primActual).isGreaterThan(primExpected);

  from: assertThat(objActual != objExpected).isEqualTo(true);
    to: assertThat(objActual).isNotSameAs(objExpected);

  from: assertThat(null == objActual).isFalse();
    to: assertThat(objActual).isNotNull();
  ```
  and many, many more combinations (more than 150).

- AssertThatJava8Optional
  ```
  from: assertThat(opt.isPresent()).isEqualTo(true);
  from: assertThat(opt.isPresent()).isNotEqualTo(false);
  from: assertThat(opt.isPresent()).isTrue();
    to: assertThat(opt).isPresent();

  from: assertThat(opt.isPresent()).isEqualTo(false);
  from: assertThat(opt.isPresent()).isNotEqualTo(true);
  from: assertThat(opt.isPresent()).isFalse();
    to: assertThat(opt).isNotPresent();
    
  from: assertThat(opt.get()).isEqualTo("foo");
    to: assertThat(opt).contains("foo");
     
  from: assertThat(opt.get()).isSameAs("foo");
    to: assertThat(opt).containsSame("foo"); 

  from: assertThat(opt).isEqualTo(Optional.of("foo"));
  from: assertThat(opt).isEqualTo(Optional.ofNullable("foo"));
    to: assertThat(opt).contains("foo"); 

  from: assertThat(opt).isEqualTo(Optional.empty());
    to: assertThat(opt).isNotPresent();

  from: assertThat(opt).isNotEqualTo(Optional.empty());
    to: assertThat(opt).isPresent();
  ```

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
  
- AssertThatGuavaOptional
  ```
  from: assertThat(opt.isPresent()).isEqualTo(true);
  from: assertThat(opt.isPresent()).isNotEqualTo(false);
  from: assertThat(opt.isPresent()).isTrue();
    to: assertThat(opt).isPresent();

  from: assertThat(opt.isPresent()).isEqualTo(false);
  from: assertThat(opt.isPresent()).isNotEqualTo(true);
  from: assertThat(opt.isPresent()).isFalse();
    to: assertThat(opt).isAbsent();
    
  from: assertThat(opt.get()).isEqualTo("foo");
    to: assertThat(opt).contains("foo");
     
  from: assertThat(opt).isEqualTo(Optional.of("foo"));
  from: assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
    to: assertThat(opt).contains("foo"); 

  from: assertThat(opt).isEqualTo(Optional.absent());
    to: assertThat(opt).isAbsent();

  from: assertThat(opt).isNotEqualTo(Optional.absent());
    to: assertThat(opt).isPresent();
  ```

  AssertJ for Guava needs to be available in the classpath.

## Development notice

Cajon is written in Kotlin 1.3.

Cajon is probably the only plugin that uses JUnit 5 Jupiter for unit testing so far (or at least the only one that I'm aware of ;) ).
The IntelliJ framework actually uses the JUnit 3 TestCase for plugin testing and I took me quite a while to make it work with JUnit 5.
Feel free to use the code (in package de.platon42.intellij.jupiter) for your projects (with attribution).

## TODO
- AssertThatNegatedBooleanExpression
- AssertThatInstanceOf
- Referencing string properties inside extracting()
- Extraction with property names to lambda with Java 8
  ```
  from: assertThat(object).extracting("propOne", "propNoGetter", "propTwo.innerProp")...
    to: assertThat(object).extracting(type::getPropOne, it -> it.propNoGetter, it -> it.getPropTwo().getInnerProp())...
  ```
- Kotlin support

## Changelog

#### V0.5 (13-Apr-19)
- Fixed incompatibility with IDEA versions < 2018.2 (affected AssertThatSizeInspection).
- Fixed missing Guava imports (if not already present) for AssertThatGuavaInspection. This was a major PITA to get right.

#### V0.4 (11-Apr-19)
- Reduced minimal supported IDEA version from 2018.2 to 2017.2.
- New inspection AssertThatJava8Optional that operates on Java 8 Optional objects and tries to use contains(), containsSame(), isPresent(), and isNotPresent() instead.
- New inspection AssertThatGuavaOptional that operates on Guava Optional objects and tries to use contains(), isPresent(), and isAbsent() instead.
- Added support in AssertThatBinaryExpressionIsTrueOrFalse for is(Not)EqualTo(Boolean.TRUE/FALSE).

#### V0.3 (07-Apr-19)
- New inspection AssertThatBinaryExpressionIsTrueOrFalse that will find and fix common binary expressions and equals() statements (more than 150 combinations) inside assertThat().
- Merged AssertThatObjectIsNull and AssertThatObjectIsNotNull to AssertThatObjectIsNullOrNotNull.
- Support for hasSizeLessThan(), hasSizeLessThanOrEqualTo(), hasSizeGreaterThanOrEqualTo(), and hasSizeGreaterThan() for AssertThatSizeInspection (with AssertJ >=13.2.0).
- Really fixed highlighting for JUnit conversion. Sorry.

#### V0.2 (01-Apr-19)
- Fixed descriptions and quick fix texts.
- Fixed highlighting of found problems and also 'Run inspection by Name' returning nothing.

#### V0.1 (31-Mar-19)
- Initial release.