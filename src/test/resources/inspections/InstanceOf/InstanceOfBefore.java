import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InstanceOf {

    private void instanceOf() {
        Boolean object = Boolean.TRUE;

        assertThat(object instanceof Boolean).as("foo").isEqualTo(Boolean.TRUE);
        assertThat(object instanceof Boolean).isEqualTo(true);
        assertThat(object instanceof Boolean).isNotEqualTo(Boolean.FALSE);
        assertThat(object instanceof Boolean).isNotEqualTo(false);
        assertThat(object instanceof Boolean).isTrue();

        assertThat(object instanceof Boolean).as("foo").isEqualTo(Boolean.FALSE);
        assertThat(object instanceof Boolean).isEqualTo(false);
        assertThat(object instanceof Boolean).isNotEqualTo(Boolean.TRUE);
        assertThat(object instanceof Boolean).isNotEqualTo(true);
        assertThat(object instanceof Boolean).isFalse();

        assertThat(((object)) instanceof Boolean).as("nah").isEqualTo(true && !true);

        assertThat(object instanceof Boolean).as("foo").isEqualTo(Boolean.TRUE).as("bar").isEqualTo(true);
        assertThat(object instanceof Boolean).as("foo").isEqualTo(Boolean.TRUE).as("bar").isEqualTo(false);

        org.junit.Assert.assertThat(object, null);
        fail("oh no!");
    }
}
