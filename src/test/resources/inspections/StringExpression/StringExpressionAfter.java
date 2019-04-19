import static org.assertj.core.api.Assertions.assertThat;

public class StringExpression {

    private void stringExpression() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

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
    }
}
