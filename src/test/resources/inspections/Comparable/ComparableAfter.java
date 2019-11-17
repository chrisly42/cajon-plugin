import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CompareTo {

    private void comparable() {
        String string = "string";
        assertThat(string).isEqualByComparingTo("foo");
        assertThat(string).isEqualByComparingTo("foo");

        assertThat(string).isNotEqualByComparingTo("foo");
        assertThat(string).isNotEqualByComparingTo("foo");

        assertThat(string).isGreaterThanOrEqualTo("foo");
        assertThat(string).isGreaterThanOrEqualTo("foo");
        assertThat(string).isGreaterThanOrEqualTo("foo");
        assertThat(string).isGreaterThanOrEqualTo("foo");

        assertThat(string).isGreaterThan("foo");
        assertThat(string).isGreaterThan("foo");
        assertThat(string).isGreaterThan("foo");
        assertThat(string).isGreaterThan("foo");
        assertThat(string).isGreaterThan("foo");

        assertThat(string).isLessThanOrEqualTo("foo");
        assertThat(string).isLessThanOrEqualTo("foo");
        assertThat(string).isLessThanOrEqualTo("foo");
        assertThat(string).isLessThanOrEqualTo("foo");

        assertThat(string).isLessThan("foo");
        assertThat(string).isLessThan("foo");
        assertThat(string).isLessThan("foo");
        assertThat(string).isLessThan("foo");

        assertThat(string.compareTo("foo")).isNotEqualTo(2);
        assertThat(string.compareTo("foo")).isEqualTo(2);

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
