import static org.assertj.core.api.Assertions.assertThat;

public class BooleanCondition {

    private void booleanCondition() {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(primitive).as("foo").isTrue();
        assertThat(primitive).isFalse();
        assertThat(object).isTrue();
        assertThat(object).isFalse();
        assertThat(primitive).isTrue();
        assertThat(primitive).isFalse();
        assertThat(object).isTrue();
        assertThat(object).isFalse();

        assertThat(primitive).as("foo").isFalse();
        assertThat(primitive).isTrue();
        assertThat(object).isFalse();
        assertThat(object).isTrue();
        assertThat(primitive).isFalse();
        assertThat(primitive).isTrue();
        assertThat(object).isFalse();
        assertThat(object).isTrue();

        assertThat(primitive).as("nah").isFalse();
        assertThat(object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);

        assertThat("").isEqualTo(Boolean.TRUE);

        assertThat(primitive).isTrue().as("foo").isTrue().as("bar").isTrue().isFalse();

        assertThat(object).isEqualTo(Boolean.TYPE);
        org.junit.Assert.assertThat(foo, null);
        fail("oh no!");
    }
}
