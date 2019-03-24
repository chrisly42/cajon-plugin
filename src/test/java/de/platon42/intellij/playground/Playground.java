package de.platon42.intellij.playground;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class Playground {

    private void sizeOfList() {
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

}
