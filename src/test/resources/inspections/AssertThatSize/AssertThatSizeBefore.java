import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertThatSize {

    private void assertThatSize() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> otherList = new ArrayList<>();
        long[] array = new long[5];
        long[] otherArray = new long[4];

        assertThat(list.size()).isEqualTo(0);
        assertThat(list.size()).isZero();
        assertThat(list.size()).isNotZero();
        assertThat(list.size()).as("hi").isGreaterThan(0);
        assertThat(list.size()).isGreaterThanOrEqualTo(1);
        assertThat(list.size()).isLessThan(1);
        assertThat(list.size()).isLessThanOrEqualTo(0);
        assertThat(list.size()).isEqualTo(otherList.size());
        assertThat(list.size()).isEqualTo(array.length);
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.size()).isGreaterThan(list.size() * 2);
        assertThat(list.size()).isGreaterThanOrEqualTo(list.size() * 2);
        assertThat(list.size()).isLessThan(list.size() * 2);
        assertThat(list.size()).isLessThanOrEqualTo(list.size() * 2);

        assertThat(array.length).isEqualTo(0);
        assertThat(array.length).isZero();
        assertThat(array.length).isNotZero();
        assertThat(array.length).as("hi").isGreaterThan(0);
        assertThat(array.length).isGreaterThanOrEqualTo(1);
        assertThat(array.length).isLessThan(1);
        assertThat(array.length).isLessThanOrEqualTo(0);
        assertThat(array.length).isEqualTo(list.size());
        assertThat(array.length).isEqualTo(otherArray.length);
        assertThat(array.length).isEqualTo(1);
        assertThat(array.length).isGreaterThan(otherArray.length - 1);
        assertThat(array.length).isGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(array.length).isLessThan(otherArray.length - 3);
        assertThat(array.length).isLessThanOrEqualTo(1 - otherArray.length);
    }
}
