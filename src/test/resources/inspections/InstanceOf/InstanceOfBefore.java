import static org.assertj.core.api.Assertions.assertThat;

public class InstanceOf {

    private void instanceOf() {
        Boolean object = Boolean.TRUE;

        assertThat(object instanceof Boolean).isEqualTo(Boolean.TRUE);
        assertThat(object instanceof Boolean).isEqualTo(true);
        assertThat(object instanceof Boolean).isNotEqualTo(Boolean.FALSE);
        assertThat(object instanceof Boolean).isNotEqualTo(false);
        assertThat(object instanceof Boolean).isTrue();

        assertThat(object instanceof Boolean).isEqualTo(Boolean.FALSE);
        assertThat(object instanceof Boolean).isEqualTo(false);
        assertThat(object instanceof Boolean).isNotEqualTo(Boolean.TRUE);
        assertThat(object instanceof Boolean).isNotEqualTo(true);
        assertThat(object instanceof Boolean).isFalse();

        assertThat(((object)) instanceof Boolean).as("nah").isEqualTo(true && !true);
    }
}
