import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

        assertThat(string).as("foo").isEqualTo("").as("bar").hasSize(0).hasSameSizeAs("foo").isEmpty();
        assertThat(string).as("foo").isEqualTo("").as("bar").hasSize(0).hasSameSizeAs("foo").isEmpty();

        org.junit.Assert.assertThat(string, null);
        fail("oh no!");
    }
}
