import static org.assertj.core.api.Assertions.assertThat;

public class ObjectIsNull {

    private void objectIsNull() {
        assertThat("").isEqualTo(null);
        assertThat("").as("nah").isEqualTo(null);
        assertThat(new Object()).isEqualTo(null);

        assertThat("").isNotEqualTo(null);
        assertThat("").as("nah").isNotEqualTo(null);
        assertThat(new Object()).isNotEqualTo(null);

        assertThat(new Object()).as("foo").isNotEqualTo(null).as("bar").isEqualTo(new Object()).as("etc").isEqualTo(null);
        assertThat(new Object()).as("foo").isEqualTo(null).as("bar").isEqualTo(new Object()).as("etc").isNotEqualTo(null);
    }
}
