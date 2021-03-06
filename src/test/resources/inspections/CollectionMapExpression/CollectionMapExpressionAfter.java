import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.*;

public class CollectionMapExpression {

    private void collectionMapExpression() {
        List<String> stringList = new ArrayList<>();
        List<String> anotherList = new ArrayList<>();
        Map<String, Integer> keyValueMap = new HashMap<>();

        assertThat(stringList).as("foo").isEmpty();
        assertThat(stringList).isEmpty();
        assertThat(stringList).contains("foo");
        assertThat(stringList).contains("foo");
        assertThat(stringList).containsAll(anotherList);
        assertThat(stringList).containsAll(anotherList);

        assertThat(stringList).as("foo").isNotEmpty();
        assertThat(stringList).isNotEmpty();
        assertThat(stringList).doesNotContain("foo");
        assertThat(stringList).doesNotContain("foo");
        assertThat(stringList.containsAll(anotherList)).isEqualTo(false);
        assertThat(stringList.containsAll(anotherList)).isFalse();

        assertThat(keyValueMap).as("foo").isEmpty();
        assertThat(keyValueMap).isEmpty();
        assertThat(keyValueMap).containsKey("foo");
        assertThat(keyValueMap).containsKey("foo");
        assertThat(keyValueMap).containsValue(2);
        assertThat(keyValueMap).containsValue(2);

        assertThat(keyValueMap).as("foo").isNotEmpty();
        assertThat(keyValueMap).isNotEmpty();
        assertThat(keyValueMap).doesNotContainKey("foo");
        assertThat(keyValueMap).doesNotContainKey("foo");
        assertThat(keyValueMap).doesNotContainValue(2);
        assertThat(keyValueMap).doesNotContainValue(2);

        assertThat(keyValueMap).containsEntry("foo", 2);
        assertThat(keyValueMap).doesNotContainEntry("foo", 3);
        assertThat(keyValueMap).doesNotContainKey("foo");
        assertThat(keyValueMap).doesNotContainKey("foo");
        assertThat(keyValueMap).containsKey("foo");
        assertThat(keyValueMap).containsKey("foo");

        Map<String, String> stringStringMap = new HashMap<>();
        assertThat(stringStringMap).containsEntry("foo", "bar");
        assertThat(stringStringMap).doesNotContainEntry("foo", "bar");
        assertThat(stringStringMap).doesNotContainKey("foo");
        assertThat(stringStringMap).doesNotContainKey("foo");
        assertThat(stringStringMap).containsKey("foo");
        assertThat(stringStringMap).containsKey("foo");

        assertThat(stringList).as("foo").isNotEmpty().as("bar").isNotEmpty();
        assertThat(stringList.isEmpty()).as("foo").isEqualTo(false).as("bar").isTrue();
        assertThat(stringList.isEmpty()).as("foo").satisfies(it -> it.booleanValue()).as("bar").isFalse();

        org.junit.Assert.assertThat(stringList, null);
        fail("oh no!");
    }
}
