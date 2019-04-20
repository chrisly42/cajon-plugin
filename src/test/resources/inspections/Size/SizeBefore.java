import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class Size {

    private void size() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> otherList = new ArrayList<>();
        long[] array = new long[5];
        long[] otherArray = new long[4];
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(list.size()).isEqualTo(0);
        assertThat(list.size()).isZero();
        assertThat(list.size()).isNotZero();
        assertThat(list.size()).as("hi").isGreaterThan(0);
        assertThat(list.size()).isGreaterThanOrEqualTo(1);
        assertThat(list.size()).isLessThan(1);
        assertThat(list.size()).isLessThanOrEqualTo(0);
        assertThat(list.size()).isEqualTo(otherList.size());
        assertThat(list.size()).isEqualTo(array.length);
        assertThat(list.size()).isEqualTo(string.length());
        assertThat(list.size()).isEqualTo(stringBuilder.length());
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
        assertThat(array.length).isEqualTo(string.length());
        assertThat(array.length).isEqualTo(stringBuilder.length());
        assertThat(array.length).isEqualTo(1);
        assertThat(array.length).isGreaterThan(otherArray.length - 1);
        assertThat(array.length).isGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(array.length).isLessThan(otherArray.length - 3);
        assertThat(array.length).isLessThanOrEqualTo(1 - otherArray.length);

        assertThat(string.length()).isEqualTo(0);
        assertThat(string.length()).isZero();
        assertThat(string.length()).isNotZero();
        assertThat(string.length()).as("hi").isGreaterThan(0);
        assertThat(string.length()).isGreaterThanOrEqualTo(1);
        assertThat(string.length()).isLessThan(1);
        assertThat(string.length()).isLessThanOrEqualTo(0);
        assertThat(string.length()).isEqualTo(list.size());
        assertThat(string.length()).isEqualTo(otherArray.length);
        assertThat(string.length()).isEqualTo(string.length());
        assertThat(string.length()).isEqualTo(stringBuilder.length());
        assertThat(string.length()).isEqualTo(1);
        assertThat(string.length()).isGreaterThan(otherArray.length - 1);
        assertThat(string.length()).isGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(string.length()).isLessThan(otherArray.length - 3);
        assertThat(string.length()).isLessThanOrEqualTo(1 - otherArray.length);

        assertThat(stringBuilder.length()).isEqualTo(0);
        assertThat(stringBuilder.length()).isZero();
        assertThat(stringBuilder.length()).isNotZero();
        assertThat(stringBuilder.length()).as("hi").isGreaterThan(0);
        assertThat(stringBuilder.length()).isGreaterThanOrEqualTo(1);
        assertThat(stringBuilder.length()).isLessThan(1);
        assertThat(stringBuilder.length()).isLessThanOrEqualTo(0);
        assertThat(stringBuilder.length()).isEqualTo(list.size());
        assertThat(stringBuilder.length()).isEqualTo(otherArray.length);
        assertThat(stringBuilder.length()).isEqualTo(string.length());
        assertThat(stringBuilder.length()).isEqualTo(stringBuilder.length());
        assertThat(stringBuilder.length()).isEqualTo(1);
        assertThat(stringBuilder.length()).isGreaterThan(otherArray.length - 1);
        assertThat(stringBuilder.length()).isGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(stringBuilder.length()).isLessThan(otherArray.length - 3);
        assertThat(stringBuilder.length()).isLessThanOrEqualTo(1 - otherArray.length);
    }
}
