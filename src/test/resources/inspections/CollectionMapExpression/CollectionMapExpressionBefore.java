import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

public class CollectionMapExpression {

    private void collectionMapExpression() {
        List<String> stringList = new ArrayList<>();
        List<String> anotherList = new ArrayList<>();
        Map<String, Integer> keyValueMap = new HashMap<>();

        assertThat(stringList.isEmpty()).as("foo").isEqualTo(true);
        assertThat(stringList.isEmpty()).isTrue();
        assertThat(stringList.contains("foo")).isEqualTo(true);
        assertThat(stringList.contains("foo")).isTrue();
        assertThat(stringList.containsAll(anotherList)).isEqualTo(true);
        assertThat(stringList.containsAll(anotherList)).isTrue();

        assertThat(stringList.isEmpty()).as("foo").isEqualTo(false);
        assertThat(stringList.isEmpty()).isFalse();
        assertThat(stringList.contains("foo")).isEqualTo(false);
        assertThat(stringList.contains("foo")).isFalse();
        assertThat(stringList.containsAll(anotherList)).isEqualTo(false);
        assertThat(stringList.containsAll(anotherList)).isFalse();

        assertThat(keyValueMap.isEmpty()).as("foo").isEqualTo(true);
        assertThat(keyValueMap.isEmpty()).isTrue();
        assertThat(keyValueMap.containsKey("foo")).isEqualTo(true);
        assertThat(keyValueMap.containsKey("foo")).isTrue();
        assertThat(keyValueMap.containsValue(2)).isEqualTo(true);
        assertThat(keyValueMap.containsValue(2)).isTrue();

        assertThat(keyValueMap.isEmpty()).as("foo").isEqualTo(false);
        assertThat(keyValueMap.isEmpty()).isFalse();
        assertThat(keyValueMap.containsKey("foo")).isEqualTo(false);
        assertThat(keyValueMap.containsKey("foo")).isFalse();
        assertThat(keyValueMap.containsValue(2)).isEqualTo(false);
        assertThat(keyValueMap.containsValue(2)).isFalse();

        assertThat(stringList.isEmpty()).as("foo").isEqualTo(false).as("bar").isFalse();
        assertThat(stringList.isEmpty()).as("foo").isEqualTo(false).as("bar").isTrue();
        assertThat(stringList.isEmpty()).as("foo").satisfies(it -> it.booleanValue()).as("bar").isFalse();
    }
}
