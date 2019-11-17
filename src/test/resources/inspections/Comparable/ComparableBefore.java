import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CompareTo {

    private void comparable() {
        String string = "string";
        assertThat(string.compareTo("foo")).isEqualTo(0);
        assertThat(string.compareTo("foo")).isZero();

        assertThat(string.compareTo("foo")).isNotEqualTo(0);
        assertThat(string.compareTo("foo")).isNotZero();

        assertThat(string.compareTo("foo")).isNotEqualTo(-1);
        assertThat(string.compareTo("foo")).isGreaterThanOrEqualTo(0);
        assertThat(string.compareTo("foo")).isGreaterThan(-1);
        assertThat(string.compareTo("foo")).isNotNegative();

        assertThat(string.compareTo("foo")).isEqualTo(1);
        assertThat(string.compareTo("foo")).isOne();
        assertThat(string.compareTo("foo")).isGreaterThan(0);
        assertThat(string.compareTo("foo")).isPositive();
        assertThat(string.compareTo("foo")).isGreaterThanOrEqualTo(1);

        assertThat(string.compareTo("foo")).isNotEqualTo(1);
        assertThat(string.compareTo("foo")).isLessThanOrEqualTo(0);
        assertThat(string.compareTo("foo")).isLessThan(1);
        assertThat(string.compareTo("foo")).isNotPositive();

        assertThat(string.compareTo("foo")).isEqualTo(-1);
        assertThat(string.compareTo("foo")).isLessThan(0);
        assertThat(string.compareTo("foo")).isNegative();
        assertThat(string.compareTo("foo")).isLessThanOrEqualTo(-1);

        assertThat(string.compareTo("foo")).isNotEqualTo(2);
        assertThat(string.compareTo("foo")).isEqualTo(2);

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
