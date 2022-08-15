import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class IsZeroOne {

    private void isZeroOne() {
        short shortValue = 0;
        int intValue = 0;
        long longValue = 0L;
        float floatValue = 0.0f;
        double doubleValue = 0.0;

        assertThat(shortValue).as("foo").isEqualTo(0);
        assertThat(shortValue).isEqualTo(1 - 1);
        assertThat(shortValue).as("foo").isEqualTo(1);
        assertThat(shortValue).isEqualTo(0 + 1);

        assertThat(shortValue).as("foo").isNotEqualTo(0);
        assertThat(shortValue).isNotEqualTo(1 - 1);
        assertThat(shortValue).as("foo").isNotEqualTo(1);
        assertThat(shortValue).isNotEqualTo(0 + 1);

        assertThat(intValue).as("foo").isEqualTo(0);
        assertThat(intValue).isEqualTo(1 - 1);
        assertThat(intValue).as("foo").isEqualTo(1);
        assertThat(intValue).isEqualTo(0 + 1);

        assertThat(intValue).as("foo").isNotEqualTo(0);
        assertThat(intValue).isNotEqualTo(1 - 1);
        assertThat(intValue).as("foo").isNotEqualTo(1);
        assertThat(intValue).isNotEqualTo(0 + 1);

        assertThat(longValue).as("foo").isEqualTo(0L);
        assertThat(longValue).isEqualTo(1L - 1L);
        assertThat(longValue).as("foo").isEqualTo(1L);
        assertThat(longValue).isEqualTo(0L + 1L);

        assertThat(longValue).as("foo").isNotEqualTo(0L);
        assertThat(longValue).isNotEqualTo(1L - 1L);
        assertThat(longValue).as("foo").isNotEqualTo(1L);
        assertThat(longValue).isNotEqualTo(0L + 1L);

        assertThat(floatValue).as("foo").isEqualTo(0.0f);
        assertThat(floatValue).isEqualTo(1.0f - 1.0f);
        assertThat(floatValue).as("foo").isEqualTo(1.0f);
        assertThat(floatValue).isEqualTo(0.0f + 1.0f);

        assertThat(floatValue).as("foo").isNotEqualTo(0.0f);
        assertThat(floatValue).isNotEqualTo(1.0f - 1.0f);
        assertThat(floatValue).as("foo").isNotEqualTo(1.0f);
        assertThat(floatValue).isNotEqualTo(0.0f + 1.0f);

        assertThat(doubleValue).as("foo").isEqualTo(0.0);
        assertThat(doubleValue).isEqualTo(1.0 - 1.0);
        assertThat(doubleValue).as("foo").isEqualTo(1.0);
        assertThat(doubleValue).isEqualTo(0.0 + 1.0);

        assertThat(doubleValue).as("foo").isNotEqualTo(0.0);
        assertThat(doubleValue).isNotEqualTo(1.0 - 1.0);
        assertThat(doubleValue).as("foo").isNotEqualTo(1.0);
        assertThat(doubleValue).isNotEqualTo(0.0 + 1.0);

        assertThat(intValue).as("foo").isEqualTo(2);

        fail("oh no!");
    }
}
