import static org.assertj.core.api.Assertions.assertThat;

public class InstanceOf {

    private void instanceOf() {
        Boolean object = Boolean.TRUE;

        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);
        assertThat(object).isInstanceOf(Boolean.class);

        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);
        assertThat(object).isNotInstanceOf(Boolean.class);

        assertThat(object).as("nah").isNotInstanceOf(Boolean.class);
    }
}
