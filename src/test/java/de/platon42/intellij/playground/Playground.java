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
    }
}
