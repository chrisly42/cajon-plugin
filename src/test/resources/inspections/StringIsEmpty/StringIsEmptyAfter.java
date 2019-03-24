import static org.assertj.core.api.Assertions.assertThat;

public class StringIsEmpty {

    private void stringIsEmpty() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(string).isEqualTo("foo");
        assertThat(string).as("foo").isEmpty();
        assertThat(string).as("bar").isEmpty();

        assertThat(stringBuilder).isEqualTo("foo");
        assertThat(stringBuilder).as("foo").isEmpty();
        assertThat(stringBuilder).as("bar").isEmpty();

        assertThat(new Object()).isEqualTo("");
    }
}