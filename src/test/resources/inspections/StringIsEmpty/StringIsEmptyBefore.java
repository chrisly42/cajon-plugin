import static org.assertj.core.api.Assertions.assertThat;

public class StringIsEmpty {

    private void stringIsEmpty() {
        String string = "string";

        assertThat(string).isEqualTo("foo");
        assertThat(string).as("foo").isEqualTo("");
        assertThat(new Object()).isEqualTo("");
    }
}
