import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BooleanCondition {

    private void booleanCondition() {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(primitive).as("foo").isEqualTo(Boolean.TRUE);
        assertThat(primitive).isEqualTo(Boolean.FALSE);
        assertThat(object).isEqualTo(Boolean.TRUE);
        assertThat(object).isEqualTo(Boolean.FALSE);
        assertThat(primitive).isEqualTo(true);
        assertThat(primitive).isEqualTo(false);
        assertThat(object).isEqualTo(true);
        assertThat(object).isEqualTo(false);

        assertThat(primitive).as("foo").isNotEqualTo(Boolean.TRUE);
        assertThat(primitive).isNotEqualTo(Boolean.FALSE);
        assertThat(object).isNotEqualTo(Boolean.TRUE);
        assertThat(object).isNotEqualTo(Boolean.FALSE);
        assertThat(primitive).isNotEqualTo(true);
        assertThat(primitive).isNotEqualTo(false);
        assertThat(object).isNotEqualTo(true);
        assertThat(object).isNotEqualTo(false);

        assertThat(primitive).as("nah").isEqualTo(true && !true);
        assertThat(object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);

        assertThat("").isEqualTo(Boolean.TRUE);

        assertThat(primitive).isEqualTo(Boolean.TRUE).as("foo").isEqualTo(true).as("bar").isTrue().isFalse();

        assertThat(object).isEqualTo(Boolean.TYPE);
        org.junit.Assert.assertThat(object, null);
        fail("oh no!");
    }
}
