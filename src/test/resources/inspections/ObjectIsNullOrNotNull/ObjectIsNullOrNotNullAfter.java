import static org.assertj.core.api.Assertions.assertThat;

public class ObjectIsNull {

    private void objectIsNull() {
        assertThat("").isNull();
        assertThat("").as("nah").isNull();
        assertThat(new Object()).isNull();

        assertThat("").isNotNull();
        assertThat("").as("nah").isNotNull();
        assertThat(new Object()).isNotNull();

        assertThat(new Object()).as("foo").isNotNull().as("bar").isEqualTo(new Object()).as("etc").isNull();
        assertThat(new Object()).as("foo").isEqualTo(null).as("bar").isEqualTo(new Object()).as("etc").isNotNull();

        org.junit.Assert.assertThat(foo, null);
        fail("oh no!");
    }
}
