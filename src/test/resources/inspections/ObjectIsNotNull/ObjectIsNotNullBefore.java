import static org.assertj.core.api.Assertions.assertThat;

public class ObjectIsNotNull {

    private void objectIsNotNull() {
        assertThat("").isNotEqualTo(null);
        assertThat("").as("nah").isNotEqualTo(null);
        assertThat(new Object).isNotEqualTo(null);
    }
}
