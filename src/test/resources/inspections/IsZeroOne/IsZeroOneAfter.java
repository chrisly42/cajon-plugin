import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IsZeroOne {

    private void isZeroOne() {
        short shortValue = 0;
        int intValue = 0;
        long longValue = 0L;
        float floatValue = 0.0f;
        double doubleValue = 0.0;

        assertThat(shortValue).as("foo").isZero();
        assertThat(shortValue).isZero();
        assertThat(shortValue).as("foo").isOne();
        assertThat(shortValue).isOne();

        assertThat(shortValue).as("foo").isNotZero();
        assertThat(shortValue).isNotZero();
        assertThat(shortValue).as("foo").isNotEqualTo(1);
        assertThat(shortValue).isNotEqualTo(0 + 1);

        assertThat(intValue).as("foo").isZero();
        assertThat(intValue).isZero();
        assertThat(intValue).as("foo").isOne();
        assertThat(intValue).isOne();

        assertThat(intValue).as("foo").isNotZero();
        assertThat(intValue).isNotZero();
        assertThat(intValue).as("foo").isNotEqualTo(1);
        assertThat(intValue).isNotEqualTo(0 + 1);

        assertThat(longValue).as("foo").isZero();
        assertThat(longValue).isZero();
        assertThat(longValue).as("foo").isOne();
        assertThat(longValue).isOne();

        assertThat(longValue).as("foo").isNotZero();
        assertThat(longValue).isNotZero();
        assertThat(longValue).as("foo").isNotEqualTo(1L);
        assertThat(longValue).isNotEqualTo(0L + 1L);

        assertThat(floatValue).as("foo").isZero();
        assertThat(floatValue).isZero();
        assertThat(floatValue).as("foo").isOne();
        assertThat(floatValue).isOne();

        assertThat(floatValue).as("foo").isNotZero();
        assertThat(floatValue).isNotZero();
        assertThat(floatValue).as("foo").isNotEqualTo(1.0f);
        assertThat(floatValue).isNotEqualTo(0.0f + 1.0f);

        assertThat(doubleValue).as("foo").isZero();
        assertThat(doubleValue).isZero();
        assertThat(doubleValue).as("foo").isOne();
        assertThat(doubleValue).isOne();

        assertThat(doubleValue).as("foo").isNotZero();
        assertThat(doubleValue).isNotZero();
        assertThat(doubleValue).as("foo").isNotEqualTo(1.0);
        assertThat(doubleValue).isNotEqualTo(0.0 + 1.0);

        assertThat(intValue).as("foo").isEqualTo(2);

        fail("oh no!");
    }
}
