import static org.assertj.core.api.Assertions.assertThat;

public class InvertedBooleanCondition {

    private void invertedBooleanCondition() {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();

        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();

        assertThat(!((primitive))).as("nah").isTrue();
        assertThat(!object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);
    }
}
