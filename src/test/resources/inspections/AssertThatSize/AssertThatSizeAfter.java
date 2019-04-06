import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertThatSize {

    private void assertThatSize() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> otherList = new ArrayList<>();
        long[] array = new long[5];
        long[] otherArray = new long[4];

        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).as("hi").isNotEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).hasSameSizeAs(otherList);
        assertThat(list).hasSameSizeAs(array);
        assertThat(list).hasSize(1);
        assertThat(list).hasSizeGreaterThan(list.size() * 2);
        assertThat(list).hasSizeGreaterThanOrEqualTo(list.size() * 2);
        assertThat(list).hasSizeLessThan(list.size() * 2);
        assertThat(list).hasSizeLessThanOrEqualTo(list.size() * 2);

        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).as("hi").isNotEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).hasSameSizeAs(list);
        assertThat(array).hasSameSizeAs(otherArray);
        assertThat(array).hasSize(1);
        assertThat(array).hasSizeGreaterThan(otherArray.length - 1);
        assertThat(array).hasSizeGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(array).hasSizeLessThan(otherArray.length - 3);
        assertThat(array).hasSizeLessThanOrEqualTo(1 - otherArray.length);
    }
}
