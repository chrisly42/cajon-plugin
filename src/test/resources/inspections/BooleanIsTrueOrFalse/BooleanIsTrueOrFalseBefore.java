import static org.assertj.core.api.Assertions.assertThat;

public class BooleanIsTrueOrFalse {

    private void booleanIsTrueOrFalse() {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(primitive).isEqualTo(Boolean.TRUE);
        assertThat(primitive).isEqualTo(Boolean.FALSE);
        assertThat(object).isEqualTo(Boolean.TRUE);
        assertThat(object).isEqualTo(Boolean.FALSE);
        assertThat(primitive).isEqualTo(true);
        assertThat(primitive).isEqualTo(false);
        assertThat(object).isEqualTo(true);
        assertThat(object).isEqualTo(false);

        assertThat(primitive).isNotEqualTo(Boolean.TRUE);
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
    }
}
