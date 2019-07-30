import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class EnumerableIsEmpty {

    private void enumerableIsEmpty() {
        assertThat("string").as("foo").hasSize(0);
        assertThat(new StringBuilder()).as("bar").hasSize(0 + 0);
        assertThat(new ArrayList<Long>()).as("etc").hasSize(10 / 100);
        assertThat(new Long[1]).as("etc").hasSize(1 - 1);

        assertThat("string").as("foo").hasSize(1);
        assertThat(new StringBuilder()).as("bar").hasSize(1);
        assertThat(new ArrayList<Long>()).as("etc").hasSize(1);
        assertThat(new Long[1]).as("etc").hasSize(1);

        assertThat("string").as("foo").hasSize(0).hasSameSizeAs("foo").hasSize(0);

        org.junit.Assert.assertThat("foo", null);
        fail("oh no!");
    }
}
