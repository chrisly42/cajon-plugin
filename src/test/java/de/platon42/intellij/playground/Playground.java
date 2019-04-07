package de.platon42.intellij.playground;

import org.assertj.core.api.ListAssert;
import org.assertj.core.data.Offset;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class Playground {

    private void sizeOfList() {
        assertThat("string").as("foo").hasSize(0);
        assertThat(new StringBuilder()).as("bar").hasSize(0);
        ListAssert<String> etc = assertThat(new ArrayList<String>()).as("etc");
        etc.hasSize(0);
        assertThat(new Long[1]).as("etc").hasSize(0);

        assertThat("string").as("foo").isEmpty();
        assertThat(new StringBuilder()).as("bar").isEmpty();
        assertThat(new ArrayList<Long>()).as("etc").isEmpty();
        assertThat(new Long[1]).as("etc").isEmpty();

        assertThat(new ArrayList<>().size()).isEqualTo(1);
        assertThat(new ArrayList<String>().size()).isEqualTo(1);
        assertThat(new ArrayList<String>().size()).isGreaterThanOrEqualTo(1);
        assertThat(new ArrayList<String>().size()).isZero();
        assertThat(new ArrayList<String>()).hasSizeGreaterThan(1);
        assertThat(new ArrayList<String>()).hasSameSizeAs(new ArrayList<>());
        assertThat(new Long[1]).as("etc").hasSameSizeAs(new Long[2]);
    }

    private void sizeOfArray() {
        assertThat(new String[1].length).isLessThanOrEqualTo(1);
        assertThat(new String[1]).hasSameSizeAs(new Object());
        assertThat("").isEqualTo(null);
        assertThat(true).isTrue();
        assertThat(true).isEqualTo(true);
        assertThat(Boolean.TRUE).isEqualTo(Boolean.FALSE);
        assertThat(Boolean.TRUE).isEqualTo(true);
    }

    private void booleanIsTrueOrFalse() {
        boolean primitive = false;
        Boolean object = java.lang.Boolean.TRUE;

        assertThat(primitive).isEqualTo(Boolean.TRUE);
        assertThat(primitive).isEqualTo(Boolean.FALSE);
        assertThat(object).isEqualTo(Boolean.TRUE);
        assertThat(object).isEqualTo(Boolean.FALSE);
        assertThat(primitive).isEqualTo(true);
        assertThat(primitive).isEqualTo(false);
        assertThat(object).isEqualTo(true);
        assertThat(object).isEqualTo(false);

        assertThat(primitive).isNotEqualTo(Boolean.TRUE);
        assertThat(primitive).isNotEqualTo(Boolean.FALSE);
        assertThat(object).isNotEqualTo(Boolean.TRUE);
        assertThat(object).isNotEqualTo(Boolean.FALSE);
        assertThat(primitive).isNotEqualTo(true);
        assertThat(primitive).isNotEqualTo(false);
        assertThat(object).isNotEqualTo(true);
        assertThat(object).isNotEqualTo(false);

        assertThat(primitive).as("nah").isEqualTo(true && !true);
        assertThat(object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);

        assertThat("").isEqualTo(Boolean.TRUE);
    }

    private void binaryExpression() {
        int primExp = 42;
        int primAct = 1337;
        Double numberObjExp = 42.0;
        Double numberObjAct = 1337.0;
        String stringExp = "foo";
        String stringAct = "bar";

        assertThat(primAct == primExp).isTrue();
        assertThat(primAct == 1).isTrue();
        assertThat(primAct == primExp).isEqualTo(false);
        assertThat(primAct != primExp).isEqualTo(true);
        assertThat(1 != primAct).isTrue();
        assertThat(primAct != primExp).isNotEqualTo(true);
        assertThat(primAct > primExp).isNotEqualTo(false);
        assertThat(primAct > primExp).isFalse();
        assertThat(primAct > 1).isFalse();
        assertThat(primAct >= 1).isTrue();
        assertThat(primAct >= primExp).isEqualTo(false);
        assertThat(1 <= primAct).isFalse();
        assertThat(1 > primAct).isTrue();
        assertThat(primAct < primExp).isNotEqualTo(true);
        assertThat(primAct <= primExp).isNotEqualTo(false);
        assertThat(primAct <= primExp).isFalse();
        assertThat(primAct <= 1).isFalse();
        assertThat(numberObjAct == 1).isTrue();
        assertThat(numberObjAct == numberObjExp).isEqualTo(false);
        assertThat(numberObjAct != numberObjExp).isEqualTo(true);
        assertThat(1 != numberObjAct).isTrue();
        assertThat(numberObjAct != numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct > numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct > 1).isFalse();
        assertThat(numberObjAct >= numberObjExp).isTrue();
        assertThat(numberObjAct >= numberObjExp).isEqualTo(false);
        assertThat(1 <= numberObjAct).isFalse();
        assertThat(numberObjAct < numberObjExp).isEqualTo(true);
        assertThat(numberObjAct < numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct <= numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct <= 1).isFalse();
        assertThat(numberObjAct.equals(numberObjExp)).isFalse();
        assertThat(stringAct == stringExp).isNotEqualTo(false);
        assertThat(stringAct.equals(stringExp)).isEqualTo(true);
        assertThat(stringAct != stringExp).isFalse();
        assertThat(stringAct == null).isNotEqualTo(true);
        assertThat(null == stringAct).isEqualTo(false);

        assertThat(null == null).isTrue();
        assertThat(!false).isTrue();
    }

    private void stringIsEmpty() {
        String foo = "bar";
        assertThat(foo).isEqualTo("");
        assertThat(foo).hasSize(0);
    }

    private void java8Optional() {
        Optional<String> foo = Optional.empty();
        assertThat(foo.get()).isEqualTo("bla");
        assertThat(foo).contains("bla");
        assertThat(foo.isPresent()).isTrue();
        assertThat(!foo.isPresent()).isFalse();
        assertThat(foo).isPresent();
        assertThat(foo.isPresent()).isFalse();
        assertThat(!foo.isPresent()).isTrue();
        assertThat(foo).isNotPresent();
    }

    private void guavaOptional() {
        com.google.common.base.Optional<String> foo = com.google.common.base.Optional.absent();
        assertThat(foo.get()).isEqualTo("bla");
        assertThat(foo).contains("bla");
        assertThat(foo.isPresent()).isTrue();
        assertThat(!foo.isPresent()).isFalse();
        assertThat(foo).isPresent();
        assertThat(foo.isPresent()).isFalse();
        assertThat(!foo.isPresent()).isTrue();
        assertThat(foo).isAbsent();
    }

    private void junitAssertions() {
        assertTrue(true);
        assertTrue("message", true);
        assertFalse(true);
        assertFalse("message", true);
        assertEquals(1L, 2L);
        assertEquals("message", 1L, 2L);
        assertNotEquals(1L, 2L);
        assertNotEquals("message", 1L, 2L);
        assertEquals(4.0, 4.4, 3.3);
        assertThat(3.0).isCloseTo(4.0, Offset.offset(2.3));
        assertThat(new int[1]).isEqualTo(new int[2]);

        String foo = "foo";
        String bar = "bar";

        assertTrue(foo == "foo");
        assertTrue("oh no!", foo == "foo");
        assertFalse(foo == "bar");
        assertFalse("boom!", foo == "bar");

        assertNull(foo);
        assertNull("oh no!", foo);
        assertNotNull(foo);
        assertNotNull("oh no!", foo);

        assertEquals(bar, foo);
        assertEquals("equals", bar, foo);
        assertNotEquals(bar, foo);
        assertNotEquals("equals", bar, foo);

        assertSame(bar, foo);
        assertSame("same", bar, foo);
        assertNotSame(bar, foo);
        assertNotSame("same", bar, foo);

        assertEquals(1.0, 2.0, 0.1);
        assertEquals("equals", 1.0, 2.0, 0.1);
        assertEquals(1.0f, 2.0f, 0.1f);
        assertEquals("equals", 1.0f, 2.0f, 0.1f);

        assertNotEquals(1.0, 2.0);
        assertNotEquals(1.0, 2.0, 0.1);
        assertNotEquals("equals", 1.0, 2.0);
        assertNotEquals("equals", 1.0, 2.0, 0.1);
        assertNotEquals(1.0f, 2.0f);
        assertNotEquals(1.0f, 2.0f, 0.1f);
        assertNotEquals("equals", 1.0f, 2.0f);
        assertNotEquals("equals", 1.0f, 2.0f, 0.1f);

        assertArrayEquals(new int[2], new int[1]);
        assertArrayEquals("array equals", new int[2], new int[1]);

        assertArrayEquals(new double[2], new double[1], 1.0);
        assertArrayEquals("array equals", new double[2], new double[1], 1.0);
        assertArrayEquals(new float[2], new float[1], 1.0f);
        assertArrayEquals("array equals", new float[2], new float[1], 1.0f);


        assertThat(foo == "foo").isTrue();
        assertThat(foo == "foo").as("oh no!").isTrue();
        assertThat(foo == "bar").isFalse();
        assertThat(foo == "bar").as("boom!").isFalse();

        assertThat(foo).isNull();
        assertThat(foo).as("oh no!").isNull();
        assertThat(foo).isNotNull();
        assertThat(foo).as("oh no!").isNotNull();

        assertThat(foo).isEqualTo(bar);
        assertThat(foo).as("equals").isEqualTo(bar);
        assertThat(foo).isNotEqualTo(bar);
        assertThat(foo).as("equals").isNotEqualTo(bar);

        assertThat(foo).isSameAs(bar);
        assertThat(foo).as("same").isSameAs(bar);
        assertThat(foo).isNotSameAs(bar);
        assertThat(foo).as("same").isNotSameAs(bar);

        assertThat(2.0).isEqualTo(1.0);
        assertThat(2.0).isCloseTo(1.0, offset(0.1));
        assertThat(2.0).as("equals").isEqualTo(1.0);
        assertThat(2.0).as("equals").isCloseTo(1.0, offset(0.1));
        assertThat(2.0f).isEqualTo(1.0f);
        assertThat(2.0f).isCloseTo(1.0f, offset(0.1f));
        assertThat(2.0f).as("equals").isEqualTo(1.0f);
        assertThat(2.0f).as("equals").isCloseTo(1.0f, offset(0.1f));

        assertThat(2.0).isNotEqualTo(1.0);
        assertThat(2.0).isNotCloseTo(1.0, offset(0.1));
        assertThat(2.0).as("equals").isNotEqualTo(1.0);
        assertThat(2.0).as("equals").isNotCloseTo(1.0, offset(0.1));
        assertThat(2.0f).isNotEqualTo(1.0f);
        assertThat(2.0f).isNotCloseTo(1.0f, offset(0.1f));
        assertThat(2.0f).as("equals").isNotEqualTo(1.0f);
        assertThat(2.0f).as("equals").isNotCloseTo(1.0f, offset(0.1f));

        assertThat(new int[1]).isEqualTo(new int[2]);
        assertThat(new int[1]).as("array equals").isEqualTo(new int[2]);

        assertThat(new double[1]).containsExactly(new double[2], offset(1.0));
        assertThat(new double[1]).as("array equals").containsExactly(new double[2], offset(1.0));
        assertThat(new float[1]).containsExactly(new float[2], offset(1.0f));
        assertThat(new float[1]).as("array equals").containsExactly(new float[2], offset(1.0f));

        assertThat(new Object()).extracting(Object::toString, Object::hashCode);
    }
}