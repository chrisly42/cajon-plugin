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
        assertThat(list.size()).isGreaterThan(otherList.size() * 2);
        assertThat(list.size()).isGreaterThanOrEqualTo(otherList.size() * 2);
        assertThat(list.size()).isLessThan(otherList.size() * 2);
        assertThat(list.size()).isLessThanOrEqualTo(otherList.size() * 2);
        assertThat(list).hasSize(otherList.size());
        assertThat(list).hasSize(array.length);
        assertThat(list).hasSize(string.length());
        assertThat(list).hasSize(stringBuilder.length());

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
        assertThat(array).hasSize(list.size());
        assertThat(array).hasSize(otherArray.length);
        assertThat(array).hasSize(string.length());
        assertThat(array).hasSize(stringBuilder.length());

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
        assertThat(string).hasSize(otherList.size());
        assertThat(string).hasSize(array.length);
        assertThat(string).hasSize(string.length());
        assertThat(string).hasSize(stringBuilder.length());

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
        assertThat(stringBuilder).hasSize(otherList.size());
        assertThat(stringBuilder).hasSize(array.length);
        assertThat(stringBuilder).hasSize(string.length());
        assertThat(stringBuilder).hasSize(stringBuilder.length());

        assertThat(stringBuilder.length()).as("foo").isEqualTo(0).isZero().as("bar").isNotZero().isEqualTo(10);

        assertThat(stringBuilder).as("foo").isNotEmpty().hasSize(2).as("bar").hasSize(otherList.size()).hasSize(array.length);
    }
}
