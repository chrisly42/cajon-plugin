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

        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).as("hi").isNotEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).hasSameSizeAs(otherList);
        assertThat(list).hasSameSizeAs(array);
        assertThat(list).hasSize(string.length());
        assertThat(list).hasSize(stringBuilder.length());
        assertThat(list).hasSize(1);
        assertThat(list).hasSizeGreaterThan(otherList.size() * 2);
        assertThat(list).hasSizeGreaterThanOrEqualTo(otherList.size() * 2);
        assertThat(list).hasSizeLessThan(otherList.size() * 2);
        assertThat(list).hasSizeLessThanOrEqualTo(otherList.size() * 2);
        assertThat(list).hasSameSizeAs(otherList);
        assertThat(list).hasSameSizeAs(array);
        assertThat(list).hasSize(string.length());
        assertThat(list).hasSize(stringBuilder.length());

        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).as("hi").isNotEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).hasSameSizeAs(list);
        assertThat(array).hasSameSizeAs(otherArray);
        assertThat(array).hasSize(string.length());
        assertThat(array).hasSize(stringBuilder.length());
        assertThat(array).hasSize(1);
        assertThat(array).hasSizeGreaterThan(otherArray.length - 1);
        assertThat(array).hasSizeGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(array).hasSizeLessThan(otherArray.length - 3);
        assertThat(array).hasSizeLessThanOrEqualTo(1 - otherArray.length);
        assertThat(array).hasSameSizeAs(list);
        assertThat(array).hasSameSizeAs(otherArray);
        assertThat(array).hasSize(string.length());
        assertThat(array).hasSize(stringBuilder.length());

        assertThat(string).isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).as("hi").isNotEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).hasSameSizeAs(list);
        assertThat(string).hasSameSizeAs(otherArray);
        assertThat(string).hasSameSizeAs(string);
        assertThat(string).hasSameSizeAs(stringBuilder);
        assertThat(string).hasSize(1);
        assertThat(string).hasSizeGreaterThan(otherArray.length - 1);
        assertThat(string).hasSizeGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(string).hasSizeLessThan(otherArray.length - 3);
        assertThat(string).hasSizeLessThanOrEqualTo(1 - otherArray.length);
        assertThat(string).hasSameSizeAs(otherList);
        assertThat(string).hasSameSizeAs(array);
        assertThat(string).hasSameSizeAs(string);
        assertThat(string).hasSameSizeAs(stringBuilder);

        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isNotEmpty();
        assertThat(stringBuilder).as("hi").isNotEmpty();
        assertThat(stringBuilder).isNotEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).hasSameSizeAs(list);
        assertThat(stringBuilder).hasSameSizeAs(otherArray);
        assertThat(stringBuilder).hasSameSizeAs(string);
        assertThat(stringBuilder).hasSameSizeAs(stringBuilder);
        assertThat(stringBuilder).hasSize(1);
        assertThat(stringBuilder).hasSizeGreaterThan(otherArray.length - 1);
        assertThat(stringBuilder).hasSizeGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(stringBuilder).hasSizeLessThan(otherArray.length - 3);
        assertThat(stringBuilder).hasSizeLessThanOrEqualTo(1 - otherArray.length);
        assertThat(stringBuilder).hasSameSizeAs(otherList);
        assertThat(stringBuilder).hasSameSizeAs(array);
        assertThat(stringBuilder).hasSameSizeAs(string);
        assertThat(stringBuilder).hasSameSizeAs(stringBuilder);

        assertThat(stringBuilder.length()).as("foo").isEqualTo(0).isZero().as("bar").isNotZero().isEqualTo(10);

        assertThat(stringBuilder).as("foo").isNotEmpty().hasSize(2).as("bar").hasSameSizeAs(otherList).hasSameSizeAs(array);
    }
}
