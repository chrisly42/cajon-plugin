import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class Size {

    private void size() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> otherList = new ArrayList<>();
        long[] array = new long[5];
        long[] otherArray = new long[4];
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> otherMap = new HashMap<>();

        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).as("hi").isNotEmpty();
        assertThat(list).isNotEmpty();
        assertThat(list).isEmpty();
        assertThat(list).isEmpty();
        assertThat(list).hasSameSizeAs(otherList);
        assertThat(list).hasSameSizeAs(array);
        assertThat(list).hasSize(string.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(list).hasSize(stringBuilder.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(list).hasSize(map.size()); // does currently not support hasSameSizeAs() in assertj
        assertThat(list).hasSize(1);
        assertThat(list).hasSizeGreaterThan(otherList.size() * 2);
        assertThat(list).hasSizeGreaterThanOrEqualTo(otherList.size() * 2);
        assertThat(list).hasSizeLessThan(otherList.size() * 2);
        assertThat(list).hasSizeLessThanOrEqualTo(otherList.size() * 2);
        assertThat(list).hasSameSizeAs(otherList);
        assertThat(list).hasSameSizeAs(array);
        assertThat(list).hasSize(string.length()); // currently unsupported in assertj
        assertThat(list).hasSize(stringBuilder.length()); // currently unsupported in assertj
        assertThat(list).hasSize(map.size()); // currently unsupported in assertj

        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).as("hi").isNotEmpty();
        assertThat(array).isNotEmpty();
        assertThat(array).isEmpty();
        assertThat(array).isEmpty();
        assertThat(array).hasSameSizeAs(list);
        assertThat(array).hasSameSizeAs(otherArray);
        assertThat(array).hasSize(string.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(array).hasSize(stringBuilder.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(array).hasSize(map.size()); // does currently not support hasSameSizeAs() in assertj
        assertThat(array).hasSize(1);
        assertThat(array).hasSizeGreaterThan(otherArray.length - 1);
        assertThat(array).hasSizeGreaterThanOrEqualTo(otherArray.length + 1);
        assertThat(array).hasSizeLessThan(otherArray.length - 3);
        assertThat(array).hasSizeLessThanOrEqualTo(1 - otherArray.length);
        assertThat(array).hasSameSizeAs(list);
        assertThat(array).hasSameSizeAs(otherArray);
        assertThat(array).hasSize(string.length()); // currently unsupported in assertj
        assertThat(array).hasSize(stringBuilder.length()); // currently unsupported in assertj
        assertThat(array).hasSize(map.size()); // currently unsupported in assertj

        assertThat(string).isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).as("hi").isNotEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).hasSameSizeAs(list);
        assertThat(string).hasSameSizeAs(array);
        assertThat(string).hasSameSizeAs(string);
        assertThat(string).hasSameSizeAs(stringBuilder);
        assertThat(string).hasSize(map.size()); // currently unsupported in assertj
        assertThat(string).hasSize(1);
        assertThat(string).hasSizeGreaterThan(array.length - 1);
        assertThat(string).hasSizeGreaterThanOrEqualTo(array.length + 1);
        assertThat(string).hasSizeLessThan(array.length - 3);
        assertThat(string).hasSizeLessThanOrEqualTo(1 - array.length);
        assertThat(string).hasSameSizeAs(list);
        assertThat(string).hasSameSizeAs(array);
        assertThat(string).hasSameSizeAs(string);
        assertThat(string).hasSameSizeAs(stringBuilder);
        assertThat(string).hasSize(map.size()); // currently unsupported in assertj

        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isNotEmpty();
        assertThat(stringBuilder).as("hi").isNotEmpty();
        assertThat(stringBuilder).isNotEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).isEmpty();
        assertThat(stringBuilder).hasSameSizeAs(list);
        assertThat(stringBuilder).hasSameSizeAs(array);
        assertThat(stringBuilder).hasSameSizeAs(string);
        assertThat(stringBuilder).hasSameSizeAs(stringBuilder);
        assertThat(stringBuilder).hasSize(map.size()); // does currently not support hasSameSizeAs() in assertj
        assertThat(stringBuilder).hasSize(1);
        assertThat(stringBuilder).hasSizeGreaterThan(array.length - 1);
        assertThat(stringBuilder).hasSizeGreaterThanOrEqualTo(array.length + 1);
        assertThat(stringBuilder).hasSizeLessThan(array.length - 3);
        assertThat(stringBuilder).hasSizeLessThanOrEqualTo(1 - array.length);
        assertThat(stringBuilder).hasSameSizeAs(list);
        assertThat(stringBuilder).hasSameSizeAs(array);
        assertThat(stringBuilder).hasSameSizeAs(string);
        assertThat(stringBuilder).hasSameSizeAs(stringBuilder);
        assertThat(stringBuilder).hasSize(map.size()); // currently unsupported in assertj

        assertThat(map).isEmpty();
        assertThat(map).isEmpty();
        assertThat(map).isNotEmpty();
        assertThat(map).as("hi").isNotEmpty();
        assertThat(map).isNotEmpty();
        assertThat(map).isEmpty();
        assertThat(map).isEmpty();
        assertThat(map).hasSameSizeAs(list);
        assertThat(map).hasSameSizeAs(array);
        assertThat(map).hasSize(string.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(map).hasSize(stringBuilder.length()); // does currently not support hasSameSizeAs() in assertj
        assertThat(map).hasSameSizeAs(otherMap);
        assertThat(map).hasSize(1);
        assertThat(map).hasSizeGreaterThan(array.length - 1);
        assertThat(map).hasSizeGreaterThanOrEqualTo(array.length + 1);
        assertThat(map).hasSizeLessThan(array.length - 3);
        assertThat(map).hasSizeLessThanOrEqualTo(1 - array.length);
        assertThat(map).hasSameSizeAs(list);
        assertThat(map).hasSameSizeAs(array);
        assertThat(map).hasSize(string.length()); // currently unsupported in assertj
        assertThat(map).hasSize(stringBuilder.length()); // currently unsupported in assertj
        assertThat(map).hasSameSizeAs(otherMap);

        assertThat(stringBuilder.length()).as("foo").isEqualTo(0).isZero().as("bar").isNotZero().isEqualTo(10);

        assertThat(stringBuilder).as("foo").isNotEmpty().hasSize(2).as("bar").hasSameSizeAs(otherList).hasSameSizeAs(array);

        int foo = 1;
        assertThat(foo).isEqualTo(0);
        assertThat(string.length()).isPositive();

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
