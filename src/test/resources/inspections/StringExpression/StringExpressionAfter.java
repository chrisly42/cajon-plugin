import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StringExpression {

    private void stringExpression() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(string).as("foo").isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isEmpty();
        assertThat(string).isEqualTo("foo");
        assertThat(string).isEqualTo("foo");
        assertThat(string).isEqualToIgnoringCase("foo");
        assertThat(string).isEqualToIgnoringCase("foo");
        assertThat(string).isEqualTo("foo");
        assertThat(string).isEqualTo("foo");
        assertThat(string).isEqualTo(stringBuilder);
        assertThat(string).isEqualTo(stringBuilder);
        assertThat(string).contains("foo");
        assertThat(string).contains("foo");
        assertThat(string).contains(stringBuilder);
        assertThat(string).contains(stringBuilder);
        assertThat(string).startsWith("foo");
        assertThat(string).startsWith("foo");
        assertThat(string).endsWith("foo");
        assertThat(string).endsWith("foo");
        assertThat(string).matches("foo");
        assertThat(string).matches("foo");

        assertThat(string).as("foo").isNotEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).isNotEmpty();
        assertThat(string).isNotEqualTo("foo");
        assertThat(string).isNotEqualTo("foo");
        assertThat(string).isNotEqualToIgnoringCase("foo");
        assertThat(string).isNotEqualToIgnoringCase("foo");
        assertThat(string).isNotEqualTo("foo");
        assertThat(string).isNotEqualTo("foo");
        assertThat(string).isNotEqualTo(stringBuilder);
        assertThat(string).isNotEqualTo(stringBuilder);
        assertThat(string).doesNotContain("foo");
        assertThat(string).doesNotContain("foo");
        assertThat(string).doesNotContain(stringBuilder);
        assertThat(string).doesNotContain(stringBuilder);
        assertThat(string).doesNotStartWith("foo");
        assertThat(string).doesNotStartWith("foo");
        assertThat(string).doesNotEndWith("foo");
        assertThat(string).doesNotEndWith("foo");
        assertThat(string).doesNotMatch("foo");
        assertThat(string).doesNotMatch("foo");

        assertThat(string).as("foo").doesNotEndWith("foo").as("bar").doesNotEndWith("foo");
        assertThat(string.endsWith("foo")).as("foo").isEqualTo(false).as("bar").isTrue();
        assertThat(string.endsWith("foo")).as("foo").satisfies(it -> it.booleanValue()).as("bar").isFalse();

        assertThat(string).isEqualToIgnoringCase("foo");
        assertThat(string).isEqualToIgnoringCase("foo");
        assertThat(string.compareToIgnoreCase("foo")).isEqualTo(1);
        assertThat(string.compareToIgnoreCase("foo")).isLessThan(0);

        assertThat(string).isNotEqualToIgnoringCase("foo");
        assertThat(string).isNotEqualToIgnoringCase("foo");
        assertThat(string.compareToIgnoreCase("foo")).isNotEqualTo(1);
        assertThat(string.compareToIgnoreCase("foo")).isGreaterThan(0);

        assertThat(string).startsWith("foo");
        assertThat(string).doesNotStartWith("foo");
        assertThat(string).startsWith("foo");
        assertThat(string.indexOf("foo")).isEqualTo(1);
        assertThat(string).doesNotContain("foo");
        assertThat(string).doesNotStartWith("foo");
        assertThat(string.indexOf("foo")).isNotEqualTo(1);
        assertThat(string).contains("foo");
        assertThat(string).doesNotContain("foo");
        assertThat(string).doesNotContain("foo");
        assertThat(string).contains("foo");
        assertThat(string).contains("foo");
        assertThat(string).doesNotContain("foo");
        assertThat(string.indexOf("foo")).isPositive();
        assertThat(string).contains("foo");
        assertThat(string.indexOf("foo")).isNotPositive();

        assertThat(string.trim()).isEmpty(); // would turn into isJavaBlank(), which is deprecated. Should be isNotNull().isBlank() then...
        assertThat(string).isNotBlank();

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
