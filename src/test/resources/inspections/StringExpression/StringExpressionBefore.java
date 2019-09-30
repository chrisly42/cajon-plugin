import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StringExpression {

    private void stringExpression() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(string.isEmpty()).as("foo").isEqualTo(true);
        assertThat(string.isEmpty()).isNotEqualTo(false);
        assertThat(string.isEmpty()).isTrue();
        assertThat(string.equals("foo")).isEqualTo(true);
        assertThat(string.equals("foo")).isTrue();
        assertThat(string.equalsIgnoreCase("foo")).isEqualTo(true);
        assertThat(string.equalsIgnoreCase("foo")).isTrue();
        assertThat(string.contentEquals("foo")).isEqualTo(true);
        assertThat(string.contentEquals("foo")).isTrue();
        assertThat(string.contentEquals(stringBuilder)).isEqualTo(true);
        assertThat(string.contentEquals(stringBuilder)).isTrue();
        assertThat(string.contains("foo")).isEqualTo(true);
        assertThat(string.contains("foo")).isTrue();
        assertThat(string.contains(stringBuilder)).isEqualTo(true);
        assertThat(string.contains(stringBuilder)).isTrue();
        assertThat(string.startsWith("foo")).isEqualTo(true);
        assertThat(string.startsWith("foo")).isTrue();
        assertThat(string.endsWith("foo")).isEqualTo(true);
        assertThat(string.endsWith("foo")).isTrue();
        assertThat(string.matches("foo")).isEqualTo(true);
        assertThat(string.matches("foo")).isTrue();

        assertThat(string.isEmpty()).as("foo").isEqualTo(false);
        assertThat(string.isEmpty()).isNotEqualTo(true);
        assertThat(string.isEmpty()).isFalse();
        assertThat(string.equals("foo")).isEqualTo(false);
        assertThat(string.equals("foo")).isFalse();
        assertThat(string.equalsIgnoreCase("foo")).isEqualTo(false);
        assertThat(string.equalsIgnoreCase("foo")).isFalse();
        assertThat(string.contentEquals("foo")).isEqualTo(false);
        assertThat(string.contentEquals("foo")).isFalse();
        assertThat(string.contentEquals(stringBuilder)).isEqualTo(false);
        assertThat(string.contentEquals(stringBuilder)).isFalse();
        assertThat(string.contains("foo")).isEqualTo(false);
        assertThat(string.contains("foo")).isFalse();
        assertThat(string.contains(stringBuilder)).isEqualTo(false);
        assertThat(string.contains(stringBuilder)).isFalse();
        assertThat(string.startsWith("foo")).isEqualTo(false);
        assertThat(string.startsWith("foo")).isFalse();
        assertThat(string.endsWith("foo")).isEqualTo(false);
        assertThat(string.endsWith("foo")).isFalse();
        assertThat(string.matches("foo")).isEqualTo(false);
        assertThat(string.matches("foo")).isFalse();

        assertThat(string.endsWith("foo")).as("foo").isEqualTo(false).as("bar").isFalse();
        assertThat(string.endsWith("foo")).as("foo").isEqualTo(false).as("bar").isTrue();
        assertThat(string.endsWith("foo")).as("foo").satisfies(it -> it.booleanValue()).as("bar").isFalse();

        assertThat(string.compareToIgnoreCase("foo")).isEqualTo(0);
        assertThat(string.compareToIgnoreCase("foo")).isZero();
        assertThat(string.compareToIgnoreCase("foo")).isEqualTo(1);
        assertThat(string.compareToIgnoreCase("foo")).isLessThan(0);

        assertThat(string.compareToIgnoreCase("foo")).isNotEqualTo(0);
        assertThat(string.compareToIgnoreCase("foo")).isNotZero();
        assertThat(string.compareToIgnoreCase("foo")).isNotEqualTo(1);
        assertThat(string.compareToIgnoreCase("foo")).isGreaterThan(0);

        assertThat(string.indexOf("foo")).isZero();
        assertThat(string.indexOf("foo")).isNotZero();
        assertThat(string.indexOf("foo")).isEqualTo(0);
        assertThat(string.indexOf("foo")).isEqualTo(1);
        assertThat(string.indexOf("foo")).isEqualTo(-1);
        assertThat(string.indexOf("foo")).isNotEqualTo(0);
        assertThat(string.indexOf("foo")).isNotEqualTo(1);
        assertThat(string.indexOf("foo")).isNotEqualTo(-1);
        assertThat(string.indexOf("foo")).isLessThan(0);
        assertThat(string.indexOf("foo")).isLessThanOrEqualTo(-1);
        assertThat(string.indexOf("foo")).isGreaterThan(-1);
        assertThat(string.indexOf("foo")).isGreaterThanOrEqualTo(0);
        assertThat(string.indexOf("foo")).isNegative();
        assertThat(string.indexOf("foo")).isPositive();
        assertThat(string.indexOf("foo")).isNotNegative();
        assertThat(string.indexOf("foo")).isNotPositive();

        assertThat(string.trim()).isEmpty(); // would turn into isJavaBlank(), which is deprecated. Should be isNotNull().isBlank() then...
        assertThat(string.trim()).isNotEmpty();

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
