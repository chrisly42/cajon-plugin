# Cajon - Concise AssertJ Optimizing Nitpicker [![Build Status](https://travis-ci.org/chrisly42/cajon-plugin.svg?branch=master)](https://travis-ci.org/chrisly42/cajon-plugin) [![Coverage Status](https://coveralls.io/repos/github/chrisly42/cajon-plugin/badge.svg?branch=master)](https://coveralls.io/github/chrisly42/cajon-plugin?branch=master)

Cajon is an IntelliJ IDEA Plugin for shortening and optimizing [AssertJ](https://assertj.github.io/doc/) assertions.

## Purpose

First, code is easier to read, when it is concise and reflects the intention clearly.
AssertJ has plenty of different convenience methods that describing various intentions precisely.
Why write longer, more complex code that can be expressed in brevity?

Second, when using the available special assertion methods of AssertJ, a failure of a condition
can be expressed in better detail and with more meaningful descriptions.
This makes finding bugs and fixing failed tests more efficient.
Nobody likes to read failures of the kind "failed because true is not false".

For example:

```
assertThat(collection.size()).isEqualTo(5);
```

If the collection has more or less than five elements, the assertion will fail, but will not
tell you about the contents, making it hard to guess what went wrong.

Instead, if you wrote the same assertion the following way:

```
assertThat(collection).hasSize(5);
```

Then AssertJ would tell you the _actual contents_ of the collection on failure.

## Conversion of JUnit assertions to AssertJ

The plugin also supports the conversion of the most common JUnit 4 assertions to AssertJ.

## Wrong use of AssertJ

Cajon also warns about bogus or incorrect uses of AssertJ.

## Lookup and refactoring of string-based extracting()

AssertJ allows [extracting POJO fields/properties on iterables/arrays](http://joel-costigliola.github.io/assertj/assertj-core-features-highlight.html#extracted-properties-assertion).

Using strings is not safe for refactoring (and before Java 8 Lambdas were available,
creating extractor functions just for testing purpose was a bit too tedious).

This plugin adds support for referencing these fields (so you can ctrl(/cmd)-click on the 
string to go to the definition) and also allows safe refactoring on the 
fields (refactoring a getter method without a corresponding field will not work 
correctly right now).

## Usage

The plugin will report inspections in your opened editor file as warnings.
You can then quick-fix these with your quick-fix hotkey (usually Alt-Return or Opt-Return).

Or, you can use the "Run Inspection by Name..." action to run one inspection on a bigger scope (e.g. the whole project).
Applying a quick fix might result in further optimization possibilities, so 
you might need to perform a couple of fixes before you get to the final result.

Check out this example where every line represents the result after a Cajon quickfix:
```
assertFalse(!(array.length == collection.size()));

assertThat(!(array.length == collection.size())).isFalse();

assertThat(array.length == collection.size()).isTrue();

assertThat(array.length).isEqualTo(collection.size());

assertThat(array).hasSameSizeAs(collection);
```

You can toggle the various inspections in the Settings/Editor/Inspections in the AssertJ group.

## Implemented inspections and quickfixes

- JoinAssertThatStatements
  
  Joins multiple ```assertThat()``` statements with same actual expression together.

  ```
  from: assertThat(expected).someCondition();
        assertThat(expected).anotherCondition();
    to: assertThat(expected).someCondition()
                            .anotherCondition();
  ```
  Joining will work on actual expressions inside ```assertThat()``` that are equivalent expressions,
  except for method calls with known side-effect methods such as ```Iterator.next()``` and
  pre/post-increment/decrement operations -- please notify me about others.

  The comments of the statements will be preserved. When using ```extracting()``` or similar,
  the statements will not be merged.
  
  The behavior regarding the insertion of line breaks between the expressions can be configured in the
  inspection settings.

- JoinVarArgsContains

  Looks for ```.contains()```, ```.doesNotContain()```, and .```containsOnlyOnce()``` calls for iterables 
  within the same statement. The available quickfix can join the arguments to variadic version of the call
  and remove the surplus one.
  
  ```
  from: assertThat(expected).contains("foo").doesNotContain("bar").contains("etc").doesNotContain("huh");
    to: assertThat(expected).contains("foo", "etc").doesNotContain("bar", "huh");
  ```
  Will not be performed on more complex statements with ```.extracting()``` or ```.as()``` to avoid
  changing semantics or losing descriptions.

  Note that the quickfix does not handle comments very well and might remove them during the operation.
  
  You may need to perform some manual reformatting, if the line gets too long after applying the fix.

- AssertThatObjectIsNullOrNotNull

  Uses ```isNull()``` and ```isNotNull()``` instead.

  ```
  from: assertThat(object).isEqualTo(null);
    to: assertThat(object).isNull();

  from: assertThat(object).isNotEqualTo(null);  
    to: assertThat(object).isNotNull();
  ```

- AssertThatBooleanCondition

  Uses ```isTrue()``` and ```isFalse()``` instead.

  ```
  from: assertThat(booleanValue).isEqualTo(true/false/Boolean.TRUE/Boolean.FALSE);  
    to: assertThat(booleanValue).isTrue()/isFalse();
  ```

- AssertThatInvertedBooleanCondition

  Inverts the boolean condition to make it more readable.
  
  ```
  from: assertThat(!booleanValue).isEqualTo(true/false/Boolean.TRUE/Boolean.FALSE);  
  from: assertThat(!booleanValue).isTrue()/isFalse();  
    to: assertThat(booleanValue).isFalse()/isTrue();
  ```

- AssertThatInstanceOf

  Moves ```instanceof``` expressions out of ```assertThat()```.
  
  ```
  from: assertThat(object instanceof classname).isEqualTo(true);
  from: assertThat(object instanceof classname).isTrue();
    to: assertThat(object).isInstanceOf(classname.class);

  from: assertThat(object instanceof classname).isEqualTo(false);
  from: assertThat(object instanceof classname).isFalse();
    to: assertThat(object).isNotInstanceOf(classname.class);
  ```

- AssertThatStringIsEmpty

  Uses ```isEmpty()``` for empty string assertions.

  ```
  from: assertThat(charSequence/string).isEqualTo("");
  from: assertThat(charSequence/string).hasSize(0);
    to: assertThat(charSequence/string).isEmpty();
  ```

  The ```assertThat(string.length()).isEqualTo(0);``` case is handled in the AssertThatSize inspection.

- AssertThatStringExpression
  
  Moves string operations inside ```assertThat()``` out.
  
  ```
  from: assertThat(stringActual.isEmpty()).isTrue();
    to: assertThat(stringActual).isEmpty();

  from: assertThat(stringActual.equals(stringExpected)).isTrue();
  from: assertThat(stringActual.contentEquals(charSeqExpected)).isTrue();
    to: assertThat(stringActual).isEqualTo(stringExpected);

  from: assertThat(stringActual.equalsIgnoreCase(stringExpected)).isTrue();
    to: assertThat(stringActual).isEqualToIgnoringCase(stringExpected);

  from: assertThat(stringActual.contains(stringExpected)).isTrue();
    to: assertThat(stringActual).contains(stringExpected);

  from: assertThat(stringActual.startsWith(stringExpected)).isTrue();
    to: assertThat(stringActual).startsWith(stringExpected);

  from: assertThat(stringActual.endsWith(stringExpected)).isTrue();
    to: assertThat(stringActual).endsWith(stringExpected);

  from: assertThat(stringActual.matches(stringExpected)).isTrue();
    to: assertThat(stringActual).matches(stringExpected);
  ```
  Analogously with ```isFalse()```.
  
  More funny stuff (excerpt):

  ```
  from: assertThat(stringActual.compareToIgnoreCase(stringExpected)).isEqualTo(0);
    to: assertThat(stringActual).isEqualToIgnoringCase(stringExpected);

  from: assertThat(stringActual.indexOf(stringExpected)).isEqualTo(0);
  from: assertThat(stringActual.indexOf(stringExpected)).isZero();
    to: assertThat(stringActual).startsWith(stringExpected);

  from: assertThat(stringActual.indexOf(stringExpected)).isNotZero();
    to: assertThat(stringActual).doesNotStartWith(stringExpected);

  from: assertThat(stringActual.indexOf(stringExpected)).isEqualTo(-1);
  from: assertThat(stringActual.indexOf(stringExpected)).isNegative();
    to: assertThat(stringActual).doesNotContain(stringExpected);

  from: assertThat(stringActual.indexOf(stringExpected)).isGreaterThanOrEqualTo(0);
    to: assertThat(stringActual).contains(stringExpected);

  from: assertThat(stringActual.trim()).isNotEmpty();
    to: assertThat(stringActual).isNotBlank();
  ```

- AssertThatObjectExpression

  Handles ```equals()```, ```toString()``` and ```hashCode()``` inside an expected expression.
  
  ```
  from: assertThat(objActual.equals(objExpected)).isTrue();
    to: assertThat(objActual).isEqualTo(objExpected);

  from: assertThat(objActual.toString()).isEqualTo(stringExpected);
    to: assertThat(objActual).hasToString(stringExpected);

  from: assertThat(objActual.hashCode()).isEqualTo(objExpected.hashCode());
    to: assertThat(objActual).hasSameHashCodeAs(objExpected);
  ```

- AssertThatComparableExpression

  Handles ```compareTo()``` inside an expected expression.
  
  ```
  from: assertThat(obj1.compareTo(obj2)).isEqualTo(0);
    to: assertThat(obj1).isEqualByComparingTo(obj2);

  from: assertThat(obj1.compareTo(obj2)).isNotZero();
    to: assertThat(obj1).isNotEqualByComparingTo(obj2);

  from: assertThat(obj1.compareTo(obj2)).isNotEqualTo(-1);
  from: assertThat(obj1.compareTo(obj2)).isGreaterThanOrEqualTo(0);
  from: assertThat(obj1.compareTo(obj2)).isGreaterThan(-1);
  from: assertThat(obj1.compareTo(obj2)).isNotNegative();
    to: assertThat(obj1).isGreaterThanOrEqualTo(obj2);

  from: assertThat(obj1.compareTo(obj2)).isOne();
    to: assertThat(obj1).isGreaterThan(obj2);

  from: assertThat(obj1.compareTo(obj2)).isNotPositive();
    to: assertThat(obj1).isLessThanOrEqualTo(obj2);
  
  from: assertThat(obj1.compareTo(obj2)).isLessThan(0);
    to: assertThat(obj1).isLessThan(obj2);
  ```

  Several more combinations omitted...

- AssertThatCollectionOrMapExpression

  Moves ```Collection``` and ```Map``` operations inside ```assertThat()``` out.

  ```
  from: assertThat(collection.isEmpty()).isTrue();
    to: assertThat(collection).isEmpty();

  from: assertThat(collection.contains("foobar")).isTrue();
    to: assertThat(collection).contains("foobar");
    
  from: assertThat(collection.containsAll(otherCollection)).isTrue();
    to: assertThat(collection).containsAll(otherCollection);

  from: assertThat(map.isEmpty()).isTrue();
    to: assertThat(map).isEmpty();

  from: assertThat(map.containsKey(key)).isTrue();
    to: assertThat(map).containsKey(key);
    
  from: assertThat(map.containsValue(value)).isTrue();
    to: assertThat(map).containsValue(value);
  ```
  Analogously with ```isFalse()``` (except for ```containsAll()```).

  Additional transformations for ```Map``` instances:

  ```
  from: assertThat(map.get(key)).isEqualTo(value);
    to: assertThat(map).containsEntry(key, value);

  from: assertThat(map.get(key)).isNotEqualTo(value);
    to: assertThat(map).doesNotContainEntry(key, value);

  from: assertThat(map.get(key)).isNotNull();
    to: assertThat(map).containsKey(key);
    
  from: assertThat(map.get(key)).isNull();
    to: assertThat(map).doesNotContainKey(key);
  ```
  
  The last transformation is the default, but may not be 100% equivalent depending whether the map
  is a degenerated case with ```null``` values, where ```map.get(key)``` returns ```null```,
  but ```containsKey(key)``` is ```true```.
  For that special case (which usually is the result of a bad design decision!)
  the quickfix should rather generate ```assertThat(map).containsEntry(key, null)```.
  Therefore, the behavior can be configured in the settings for this inspection to either
  create the default case (```doesNotContainKey```), the degenerated case (```containsEntry```),
  choosing between both fixes (does not work well for batch processing), or ignore this edge case
  altogether (just to be sure to not break any code).

- AssertThatFileExpression

  Moves ```File``` method calls inside ```assertThat()``` out.

  ```
  from: assertThat(file.canRead()).isTrue();
    to: assertThat(file).canRead();

  from: assertThat(file.canWrite()).isTrue();
    to: assertThat(file).canWrite();

  from: assertThat(file.exists()).isTrue();
    to: assertThat(file).exists();

  from: assertThat(file.exists()).isFalse();
    to: assertThat(file).doesNotExist();

  from: assertThat(file.isAbsolute()).isTrue();
    to: assertThat(file).isAbsolute();

  from: assertThat(file.isAbsolute()).isFalse();
    to: assertThat(file).isRelative();

  from: assertThat(file.isDirectory()).isTrue();
    to: assertThat(file).isDirectory();

  from: assertThat(file.isFile()).isTrue();
    to: assertThat(file).isFile();

  from: assertThat(file.getName()).isEqualTo(filename);
    to: assertThat(file).hasName(filename);

  from: assertThat(file.getParent()).isEqualTo(pathname);
    to: assertThat(file).hasParent(pathname);

  from: assertThat(file.getParent()).isNull();
  from: assertThat(file.getParentFile()).isNull();
    to: assertThat(file).hasNoParent();

  from: assertThat(file.list()).isEmpty();
  from: assertThat(file.listFiles()).isEmpty();
    to: assertThat(file).isEmptyDirectory();

  from: assertThat(file.list()).isNotEmpty();
  from: assertThat(file.listFiles()).isNotEmpty();
    to: assertThat(file).isNotEmptyDirectory();
  ```

  and additionally with AssertJ 3.14.0 or later

  ```
  from: assertThat(file.length()).isEqualTo(0);
  from: assertThat(file.length()).isZero();
    to: assertThat(file).isEmpty();

  from: assertThat(file.length()).isNotEqualTo(0);
  from: assertThat(file.length()).isNotZero();
    to: assertThat(file).isNotEmpty();

  from: assertThat(file.length()).isEqualTo(len);
    to: assertThat(file).hasSize(len);
  ```

- AssertThatPathExpression

  Moves ```Path``` method calls inside ```assertThat()``` out.
  Note: Uses hasParentRaw() instead of hasParent() for quickfixes, because it is semantically
  equivalent. For most cases though, hasParent() will show identical behavior.

  ```
  from: assertThat(path.isAbsolute()).isTrue();
    to: assertThat(path).isAbsolute();

  from: assertThat(path.isAbsolute()).isFalse();
    to: assertThat(path).isRelative();

  from: assertThat(path.getParent()).isEqualTo(pathname);
    to: assertThat(path).hasParentRaw(pathname);

  from: assertThat(path.getParent()).isNull();
    to: assertThat(path).hasNoParentRaw();

  from: assertThat(path.startsWith(otherPath)).isTrue();
    to: assertThat(path).startsWithRaw(otherPath);

  from: assertThat(path.endsWith(otherPath)).isTrue();
    to: assertThat(path).endsWithRaw(otherPath);
  ```

- AssertThatEnumerableIsEmpty

  Uses ```isEmpty()``` for ```hasSize(0)``` iterable assertions instead.
  
  ```
  from: assertThat(enumerable).hasSize(0);
    to: assertThat(enumerable).isEmpty();
  ```

- AssertThatSize

  Makes assertions on sizes on ```Array```, ```Collection```,
  ```Map```, ```String```, or ```CharSequence``` instances more concise.

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
    
  from: assertThat(array).hasSize(anotherArray.length);
    to: assertThat(array).hasSameSizeAs(anotherArray);
  ```

  and additionally with AssertJ 3.12.0 or later

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
  and analogously for ```Collection```, ```Map```, ```String``` and
  ```CharSequence``` objects, e.g:

  ```
  from: assertThat("string".length()).isLessThan(1);
    to: assertThat("string").isEmpty();

  from: assertThat(map.size()).isEqualTo(anotherMap.size())
    to: assertThat(map).hasSameSizeAs(anotherMap);
    
  from: assertThat("string".length()).hasSize("strong".length())
    to: assertThat("string").hasSameSizeAs("strong");
  ```

- AssertThatBinaryExpression

  Splits a boolean condition represented by binary expression out of ```assertThat()```.

  ```
  from: assertThat(primActual == primExpected).isTrue();
    to: assertThat(primActual).isEqualTo(primExpected);

  from: assertThat(10 < primActual).isNotEqualTo(false);
    to: assertThat(primActual).isGreaterThan(10);

  from: assertThat(objActual != objExpected).isEqualTo(true);
    to: assertThat(objActual).isNotSameAs(objExpected);

  from: assertThat(null == objActual).isFalse();
    to: assertThat(objActual).isNotNull();
  ```
  ...and many, many more combinations (more than 150).

- TwistedAssertion

  Examines the actual expression for common mistakes such as mixing expected and actual expression.
  For simple cases, a quick fix is offered to swap them. Otherwise, only a warning is issued.
  
  ```
  from: assertThat(5).isEqualTo(variable);
    to: assertThat(variable).isEqualTo(5);

  from: assertThat(8.0).isGreaterThan(variable);
    to: assertThat(variable).isLessOrEqualTo(8.0);
  ```

  There are, of course, more variations of the theme.
  
  If both sides of an assertion are constant expressions, the problem will only appear as
  a weak warning without a quick fix.
  
  Constants used on the actual side of ```.matches()``` and ```doesNotMatch()``` will not be
  reported for regular expression testing.
  
  Neither will a ```Class``` type be considered a constant in the classic sense, so
  ```assertThat(SomeClass.class).isAssignableFrom(SomeOtherClass.class)``` will not be reported.

- BogusAssertion

  Sometimes programmers make copy and paste or logical errors writing down assertions
  that will never fail due to the same actual and expected assertions.
  This inspection will warn about obvious cases such as the following ones.

  ```
  assertThat(object).isEqualTo(object);
  assertThat(object).isSameAs(object);
  assertThat(object).hasSameClassAs(object);
  assertThat(object).hasSameHashCodeAs(object);

  assertThat(array).hasSameSizeAs(array);
  assertThat(array).contains(array);
  assertThat(array).containsAnyOf(array);
  assertThat(array).containsExactly(array);
  assertThat(array).containsExactlyInAnyOrder(array);
  assertThat(array).containsExactlyInAnyOrder(array);
  assertThat(array).containsOnly(array);
  assertThat(array).containsSequence(array);
  assertThat(array).containsSubsequence(array);
  assertThat(array).startsWith(array);
  assertThat(array).endsWith(array);
  
  assertThat(enumerable).hasSameSizeAs(enumerable);

  assertThat(iterable).hasSameElementsAs(iterable);
  assertThat(iterable).containsAll(iterable);
  assertThat(iterable).containsAnyElementOf(iterable);
  assertThat(iterable).containsOnlyElementsOf(iterable);
  assertThat(iterable).containsExactlyElementsOf(iterable);
  assertThat(iterable).containsSequence(iterable);
  assertThat(iterable).containsSubsequence(iterable);

  assertThat(charSeq).isEqualToIgnoringCase(charSeq);
  assertThat(charSeq).startsWith(charSeq);
  assertThat(charSeq).endsWith(charSeq);
  assertThat(charSeq).containsSequence(charSeq);
  assertThat(charSeq).containsSubsequence(charSeq);

  assertThat(map).containsAllEntriesOf(map);
  assertThat(map).containsExactlyEntriesOf(map);
  assertThat(map).containsExactlyInAnyOrderEntriesOf(map);
  assertThat(map).hasSameSizeAs(map);
  ```

  Note that expressions with method calls will not cause a warning as the method call might have side effects
  that result in the assertion not being bogus at all.

  If the assertions is either ```isEqualTo()``` or ```hasSameHashCodeAs()``` it may be checking custom
  ```equals()``` or ```hashCode()``` behavior. If the test method name containing the statement has a
  name that contains 'equal' or 'hashcode' (case insensitive), the warning will be weakened to information
  level.

- ImplicitAssertion

  Detects and removes implicit use of ```isNotNull()```, ```isNotEmpty()``` and
  ```isPresent()``` when followed by an assertion that will implicitly cover this
  check.

  ```
  from: assertThat(string).isNotNull().startsWith("foo");
    to: assertThat(string).startsWith("foo");

  from: assertThat(list).isNotEmpty().hasSize(10);
    to: assertThat(list).hasSize(10);

  from: assertThat(optional).isPresent().contains("foo");
    to: assertThat(optional).contains("foo");
  ```
  ...and many more combinations (more than 100).

- AssertThatJava8Optional

  Examines the statement for Java 8 ```Optional``` type and whether the statement
  effectively tries to assert the presence, absence or content and then 
  replaces the statement by better assertions.
  
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

  from: assertThat(opt.orElse(null)).isEqualTo(null);
    to: assertThat(opt).isNotPresent();
     
  from: assertThat(opt.orElse(null)).isNotEqualTo(null);
    to: assertThat(opt).isPresent();
     
  from: assertThat(opt).isEqualTo(Optional.of("foo"));
  from: assertThat(opt).isEqualTo(Optional.ofNullable("foo")); // only for constant "foo"
    to: assertThat(opt).contains("foo"); 

  from: assertThat(opt).isEqualTo(Optional.empty());
    to: assertThat(opt).isNotPresent();

  from: assertThat(opt).isNotEqualTo(Optional.empty());
    to: assertThat(opt).isPresent();
  ```

- AssertThatGuavaOptional

  Examines the statement for Google Guava ```Optional``` type and whether the statement
  effectively tries to assert the presence, absence or content and then 
  replaces the statement by better assertions.

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
     
  from: assertThat(opt.orNull()).isEqualTo(null);
    to: assertThat(opt).isAbsent();
     
  from: assertThat(opt.orNull()).isNotEqualTo(null);
    to: assertThat(opt).isPresent();
     
  from: assertThat(opt).isEqualTo(Optional.of("foo"));
  from: assertThat(opt).isEqualTo(Optional.fromNullable("foo")); // only for constant "foo"
    to: assertThat(opt).contains("foo"); 

  from: assertThat(opt).isEqualTo(Optional.absent());
    to: assertThat(opt).isAbsent();

  from: assertThat(opt).isNotEqualTo(Optional.absent());
    to: assertThat(opt).isPresent();
  ```

  AssertJ for Guava needs to be available in the classpath for this inspection to work.

- AssumeThatInsteadOfReturn

  Tries to detect bogus uses of return statements in test methods and replaces them by ```assumeThat()``` calls.
  
  Novices will use these to skip test execution by bailing out early on some preconditions not met.
  However, this suggests that the test has actually been run and passed instead of showing the test
  as being skipped.

  Return statements in ```if``` statements in main test methods (must be annotated with JUnit 4 or 
  Jupiter ```@Test``` annotations) will be verified to have at least one ```assertThat()``` statement in the code flow.
  Method calls within the same class will be examined for ```assertThat()``` statements, too.
  However, at most 50 statements and down to five recursions will be tolerated before giving up.
  
  Currently, the quickfix may lose some comments during operation. The other branch of the ```if``` statement
  will be inlined (blocks with declarations will remain a code block due to variable scope).
  The quickfix will only work with AssertJ >= 2.9.0 (for 2.x releases) or >= 3.9.0 (for 3.x releases).
  
  The generated ```assumeThat()``` statement could be optimized further (similar to ```assertThat()```), but
  there is currently no support in Cajon for this (you could rename the method to ```assertThat()``` optimize it
  and turn it back into ```assumeThat()``` in the end).

  Example:
  
  ```
      @Test
      public void check_fuel_emission() {
          if (System.getProperty("manufacturer").equals("Volkswagen")) {
              return;
          }
          double nitroxppm = doWltpDrivingCycle();
          assertThat(nitroxppm).isLessThan(500.0);
      }
  ```
  will be transformed to
  ```
      @Test
      public void check_fuel_emission() {
          assumeThat(System.getProperty("manufacturer").equals("Volkswagen")).isFalse();
          double nitroxppm = doWltpDrivingCycle();
          assertThat(nitroxppm).isLessThan(500.0);
      }
  ```

- JUnitAssertToAssertJ

  Tries to convert most of the JUnit 4 assertions and assumptions to AssertJ format.
  Sometimes the expected and actual expressions are specified in wrong order -- 
  Cajon tries to swap these when it detects the supposed actual expression to be a
  constant while the expected one is not.

  Does not support Hamcrest-Matchers.
  If you need that kind of conversion, you might want to check out the
  [Assertions2AssertJ plugin](https://plugins.jetbrains.com/plugin/10345-assertions2assertj) by Ric Emery.
  
  ```
  assertTrue(condition);
  assertTrue(message, condition);
  assertFalse(condition);
  assertFalse(message, condition);
  assertNull(object);
  assertNull(message, object);
  assertNotNull(object);
  assertNotNull(message, object);
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
  
  assumeTrue(condition);
  assumeTrue(message, condition);
  assumeFalse(condition);
  assumeFalse(message, condition);
  assumeNotNull(object); // single argument only!
  assumeNoException(throwable);
  assumeNoException(message, throwable);
  ```

### Implemented referencing

  You can ctrl-click on references inside .extracting() method parameters to go the
  referencing method definition.
  ```
  .extracting("field")
  .extracting("outerField.fieldInsideObjectTypeOfOuterField.andSoOn")
  .extracting("property") // where the class has a getProperty() (or isProperty() for boolean) method
  .extracting("bareMethod") // supported with AssertJ 13.12.0
  .extracting(Extractors.byName("fieldOrPropertyOrBareMethod")
  .extracting(Extractors.byName("fieldOrPropertyOrBareMethod.orAPathLikeAbove")
  .extracting(Extractors.resultOf("bareMethod")
  .extractingResultOf("bareMethod")
  .flatExtracting("fieldOrPropertyOrBareMethod.orAPathLikeAbove")
  .flatExtracting(Extractors.byName("fieldOrPropertyOrBareMethod.orAPathLikeAbove")
  .flatExtracting(Extractors.resultOf("bareMethod")
  ```
  This works on both POJOs and ```Iterable```s/```Array```s. 
  Implementation is very basic though and does not work with fancy cascaded ```.extracting()``` sequences.
  If there's demand, I could add it.

## Development notice

Cajon is written in Kotlin 1.3.

Cajon is probably the only plugin that uses JUnit 5 Jupiter for unit testing so far (or at least the only one that I'm aware of ;) ).
The IntelliJ framework actually uses the JUnit 3 TestCase for plugin testing and it took me quite a while to make it work with JUnit 5.
Feel free to use the code (in package ```de.platon42.intellij.jupiter```) for your projects (with attribution).

## Planned features
- More Optional fixes such as ```opt1.get() == opt2.get()``` etc.
- More moving out of methods for LocalDate/Time etc.
- Extraction with property names to lambda/method reference with Java 8

  ```
  from: assertThat(object).extracting("propOne", "propNoGetter", "propTwo.innerProp")...
    to: assertThat(object).extracting(type::getPropOne, it -> it.propNoGetter, it -> it.getPropTwo().getInnerProp())...
  ```

## Changelog

#### V1.10 (unreleased)
- Updated libraries to the latest versions (including AssertJ 3.16.1 and Kotlin 1.40-rc).

#### V1.9 (25-Feb-20) Mardi Gras Edition
- TwistedAssertion inspection will no longer warn for ```.matches()``` and ```doesNotMatch()``` for regular expressions.
  Apparently, ```assertThat("somestring").matches(regex)``` is a valid test if the regex is what needs to be tested.
  If the actual expression is of ```Class``` type, this will no longer be reported.
- If the expected expression in TwistedAssertion is also a constant, the warning will be weakened and
  no quick fix will be available.
- BogusAssertion inspection will no longer warn if the expression contains method calls.
  Moreover, for assertions of ```isEqualTo()``` and ```hasSameHashCodeAs()```, AND if the containing method name contains 'equal' or 'hashcode',
  the warning will be reduced to information level as the assertion may be testing ```equals()``` or ```hashCode()``` for validity.

#### V1.8 (14-Feb-20) Valentine Edition
- Maintenance. Removed experimental API use. Updated dependencies. Fixed testing problems introduced with IntelliJ IDEA 2019.3
- Added new TwistedAssertion inspection that will warn about assertions with the actual expression being a constant indicating
  swapped use of actual and expected expressions.
- Added new BogusAssertion inspection that showing typical copy and paste errors where actual and expected expressions are the same.

#### V1.7 (19-Nov-19)
- Fixed a lapsuus in AssertThatFileExpression also transforming ```.listFiles()``` with a filter argument.
- Added first version of AssertThatPathExpression for a limited number transformations (more stuff is possible,
  but requires detection and transformation of static ```Files```-methods).
- Added AssertThatComparableExpression for funny ```compareTo()``` uses.
- Added ```hasSize(), isEmpty()``` and ```isNotEmpty()``` for AssertThatFileExpression when using AssertJ >= 3.14.0.

#### V1.6 (30-Sep-19)
- Really fixed AssertThatGuavaOptional inspections to avoid conversions from ```.get()``` to ```.contains()```
  for array types. Sigh. Shouldn't be working >12h a day and then do some more stuff at home.
- Fixed a bug in AssertThatBinaryExpression inspection for ```assertThat(null != expression)``` and related
  that would not correctly invert the condition on transformation.
- Added new AssertThatFileExpression to move out many common methods from inside the
  ```assertThat()``` expression (```exists(), getName(), getParent()```, and many more).
- Added several transformations to AssertThatStringExpression inspection.
  Specifically, uses of ```matches()```, ```compareToIgnoreCase()```, ```indexOf()```, and ```trim()```.

#### V1.5 (24-Sep-19)
- Fix for AssertThatCollectionOrMap inspection sometimes causing an index out of bounds exception.
- AssertThatGuavaOptional inspections will now avoid conversions from ```.get()``` to ```.contains()```
  for array types (currently not correctly supported by ```contains()``` in AssertJ-Guava).
- Added an settings option for AssertThatCollectionOrMap inspection respecting the degenerated case of maps with ```null``` values.
  It is now possible to change the behavior for ```map.get(key) == null```, so it can offer either ```.doesNotContainKey()``` (default)
  or ```.containsEntry(key, null)```, or even both.
- Fixes to AssertThatSize inspection after extending it for Maps in previous release as not all
  combinations for ```.hasSameSizeAs()``` are supported.

#### V1.4 (25-Aug-19)
- Minor fix for highlighting of JoinVarArgsContains inspection.
- Extended AssertThatSize inspection to Maps, too.
- Extended AssertThatCollectionOrMap inspection for several ```assertThat(map.get())``` cases as suggested by Georgij G.

#### V1.3 (03-Aug-19)
- New JoinVarArgsContains inspection that will detect multiple ```.contains()```, ```.doesNotContain()```,
  and ```.containsOnlyOnce()``` calls within the same statement that could be joined together using variadic arguments.
- AssertJ 3.13.0 broke some inspections due to new ```AbstractStringAssert::isEqualTo()``` method.
- AssertThatJava8Optional and AssertThatGuavaOptional inspections do not longer try to fix
  ```assertThat(optional).isEqualTo(Optional.fromNullable(expression))``` to ```contains()```
  when ```expression``` is not a non-null constant expression.
  
#### V1.2 (23-Jun-19)
- Due to popular demand the JoinAssertThatStatements inspection will now add line breaks on joining statements.
  The amount of statements joined without causing line breaks can be configured but defaults to 1 (always).
          
#### V1.1 (09-Jun-19)
- Improved JoinAssertThatStatements detection of expressions with side-effects and added pre/post-increment/decrement detection.
- Added Guava Optional ```opt.orNull() == null``` case. You know, I'm not making this stuff up, people actually write this kind of code.
- Added Java 8 Optional ```opt.orElse(null) == null``` case, too.
- Extended JUnitAssertToAssertJ inspection to convert JUnit ```assume``` statements, too.
- Improved JUnitAssertToAssertJ quick fix to swap expected and actual expressions if the actual one is a constant.
- New ImplicitAssertion inspection for implicit ```isNotNull()```, ```isNotEmpty()``` and ```isPresent()``` assertions that will be covered by chained assertions.
- Fix for multiple JUnit Conversions in batch mode with and without delta creating an exception.
- Added new AssertThatObjectExpression inspection for ```toString()``` and ```hashCode()``` and moved ```equals()``` from AssertThatBinaryExpression there.

#### V1.0 (06-May-19)
- First release to be considered stable enough for production use.
- Fixed a NPE in AssumeThatInsteadOfReturn inspection quickfix for empty else branches.
- Fixed missing description for AssumeThatInsteadOfReturn inspection.
- Added new AssertThatCollectionOrMapExpression inspection that tries to pull out methods such as ```isEmpty()``` or ```contains()``` out of an actual ```assertThat()``` expression.

#### V0.8 (05-May-19)
- Fixed missing description for JoinAssertThatStatements and detection of equivalent expressions (sorry, released it too hastily).
- Fixed ```isEmpty()``` for enumerables and strings and ```isNull()``` for object conversions to be applied only if it is the terminal method call as ```isEmpty()``` and ```isNull()``` return void.
- Heavily reworked inspections for edge cases, such as multiple ```isEqualTo()``` calls inside a single statement.
- Some inspections could generate bogus code for weird situations, this has been made more fool-proof.
- Corrected highlighting for many inspections.
- Fixed family names for inspections in batch mode.
- Reworded many inspection messages for better understanding.
- Added a first version of a new inspection that tries to detect bogus uses of return statements in test methods and replaces them by ```assumeThat()``` calls.

#### V0.7 (28-Apr-19)
- Another fix for AssertThatGuavaOptional inspection regarding using the same family name for slightly different quick fix executions
  (really, Jetbrains, this sucks for no reason).
- Extended AssertThatSize inspection to transform ```hasSize()``` into ```hasSameSizeAs()```, if possible.
- Implemented first version of JoinAssertThatStatements inspection that will try to merge ```assertThat()``` statements with the same
  actual object together, preserving comments.

#### V0.6 (22-Apr-19)
- New AssertThatStringExpression inspection that will move ```isEmpty()```, ```equals()```, ```equalsIgnoreCase()```, ```contains()```,
  ```startsWith()```, and ```endsWith()``` out of actual expression.
- Extended AssertThatSize inspection to take ```String```s and ```CharSequences``` into account, too.
- New AssertThatInvertedBooleanCondition inspection that will remove inverted boolean expressions inside ```assertThat()```.
- Renamed a few inspections to better/shorter names.
- New AssertThatInstanceOf inspection that moves instanceof expressions out of ```assertThat()```.

#### V0.5 (18-Apr-19)
- Fixed incompatibility with IDEA versions < 2018.2 (affected AssertThatSizeInspection). Minimal version is now 2017.3.
- Fixed missing Guava imports (if not already present) for AssertThatGuavaInspection. This was a major PITA to get right.
- Added support for referencing and refactoring inside ```.extracting()``` methods with fields, properties and methods (though
  getter renaming does not work that perfect, but I'm giving up for now as the IntelliJ SDK docs are seriously lacking).
- Fixed an exception in batch mode if the description string was the same but for different fixes. 
  Now descriptions are different for quick fixes triggered by AssertThatJava8OptionalInspection and AssertThatGuavaOptionalInspection.

#### V0.4 (11-Apr-19)
- Reduced minimal supported IDEA version from 2018.2 to 2017.2.
- New inspection AssertThatJava8Optional that operates on Java 8 ```Optional``` objects and tries to use ```contains()```, ```containsSame()```, ```isPresent()```, and ```isNotPresent()``` instead.
- New inspection AssertThatGuavaOptional that operates on Guava ```Optional``` objects and tries to use ```contains()```, ```isPresent()```, and ```isAbsent()``` instead.
- Added support in AssertThatBinaryExpressionIsTrueOrFalse for ```is(Not)EqualTo(Boolean.TRUE/FALSE)```.

#### V0.3 (07-Apr-19)
- New inspection AssertThatBinaryExpressionIsTrueOrFalse that will find and fix common binary expressions and ```equals()``` statements (more than 150 combinations) inside ```assertThat()```.
- Merged AssertThatObjectIsNull and AssertThatObjectIsNotNull to AssertThatObjectIsNullOrNotNull.
- Support for ```hasSizeLessThan()```, ```hasSizeLessThanOrEqualTo()```, ```hasSizeGreaterThanOrEqualTo()```, and ```hasSizeGreaterThan()``` for AssertThatSizeInspection (with AssertJ >=3.12.0).
- Really fixed highlighting for JUnit conversion. Sorry.

#### V0.2 (01-Apr-19)
- Fixed descriptions and quick fix texts.
- Fixed highlighting of found problems and also 'Run inspection by Name' returning nothing.

#### V0.1 (31-Mar-19)
- Initial release.