package de.platon42.intellij.playground;

import org.assertj.core.api.ListAssert;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
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

    private void stringIsEmpty() {
        String foo = "bar";
        assertThat(foo).isEqualTo("");
        assertThat(foo).hasSize(0);
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
    }

}
