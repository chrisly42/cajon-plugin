import static org.assertj.core.api.Assertions.assertThat;

public class ObjectIsNotNull {

    private void objectIsNotNull() {
        assertThat("").isNotNull();
        assertThat("").as("nah").isNotNull();
        assertThat(new Object).isNotNull();
    }
}
