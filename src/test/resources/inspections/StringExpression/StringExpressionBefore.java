import static org.assertj.core.api.Assertions.assertThat;

public class StringExpression {

    private void stringExpression() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(string.isEmpty()).isEqualTo(true);
        assertThat(string.isEmpty()).isTrue();
        assertThat(string.equals("foo")).isEqualTo(true);
        assertThat(string.equals("foo")).isTrue();
        assertThat(string.equalsIgnoreCase("foo")).isEqualTo(true);
        assertThat(string.equalsIgnoreCase("foo")).isTrue();
        assertThat(string.contentEquals("foo")).isEqualTo(true);
        assertThat(string.contentEquals("foo")).isTrue();
        assertThat(string.contentEquals(stringBuilder)).isTrue();
        assertThat(string.contentEquals(stringBuilder)).isEqualTo(true);
        assertThat(string.contains("foo")).isEqualTo(true);
        assertThat(string.contains("foo")).isTrue();
        assertThat(string.contains(stringBuilder)).isEqualTo(true);
        assertThat(string.contains(stringBuilder)).isTrue();
        assertThat(string.startsWith("foo")).isEqualTo(true);
        assertThat(string.startsWith("foo")).isTrue();
        assertThat(string.endsWith("foo")).isEqualTo(true);
        assertThat(string.endsWith("foo")).isTrue();

        assertThat(string.isEmpty()).isEqualTo(false);
        assertThat(string.isEmpty()).isFalse();
        assertThat(string.equals("foo")).isEqualTo(false);
        assertThat(string.equals("foo")).isFalse();
        assertThat(string.equalsIgnoreCase("foo")).isEqualTo(false);
        assertThat(string.equalsIgnoreCase("foo")).isFalse();
        assertThat(string.contentEquals("foo")).isEqualTo(false);
        assertThat(string.contentEquals("foo")).isFalse();
        assertThat(string.contentEquals(stringBuilder)).isFalse();
        assertThat(string.contentEquals(stringBuilder)).isEqualTo(false);
        assertThat(string.contains("foo")).isEqualTo(false);
        assertThat(string.contains("foo")).isFalse();
        assertThat(string.contains(stringBuilder)).isEqualTo(false);
        assertThat(string.contains(stringBuilder)).isFalse();
        assertThat(string.startsWith("foo")).isEqualTo(false);
        assertThat(string.startsWith("foo")).isFalse();
        assertThat(string.endsWith("foo")).isEqualTo(false);
        assertThat(string.endsWith("foo")).isFalse();
    }
}