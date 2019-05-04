import static org.assertj.core.api.Assertions.assertThat;

public class InstanceOf {

    private void instanceOf() {
        Boolean object = Boolean.TRUE;

        assertThat(object).as("foo").isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);

        assertThat(object).as("foo").isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);

        assertThat(object).as("nah").isNotInstanceOf(Boolean.class);

        assertThat(object).as("foo").isInstanceOf(Boolean.class).as("bar").isInstanceOf(Boolean.class);
        assertThat(object instanceof Boolean).as("foo").isEqualTo(Boolean.TRUE).as("bar").isEqualTo(false);
    }
}
