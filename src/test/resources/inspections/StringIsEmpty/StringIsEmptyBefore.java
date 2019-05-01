import static org.assertj.core.api.Assertions.assertThat;

public class StringIsEmpty {

    private void stringIsEmpty() {
        String string = "string";
        StringBuilder stringBuilder = new StringBuilder();

        assertThat(string).isEqualTo("foo");
        assertThat(string).as("foo").isEqualTo("");
        assertThat(string).as("bar").hasSize(0);

        assertThat(stringBuilder).isEqualTo("foo");
        assertThat(stringBuilder).as("foo").isEqualTo("" + "");
        assertThat(stringBuilder).as("bar").hasSize(0);

        assertThat(new Object()).isEqualTo("");

        assertThat(string).as("foo").isEqualTo("").as("bar").hasSize(0).hasSameSizeAs("foo").isEqualTo("");
        assertThat(string).as("foo").isEqualTo("").as("bar").hasSize(0).hasSameSizeAs("foo").hasSize(0);
    }
}
