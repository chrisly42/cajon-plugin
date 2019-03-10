import static org.assertj.core.api.Assertions.assertThat;

public class ObjectIsNull {

    private void objectIsNull() {
        assertThat("").isEqualTo(null);
        assertThat("").as("nah").isEqualTo(null);
        assertThat(new Object).isEqualTo(null);
    }
}
