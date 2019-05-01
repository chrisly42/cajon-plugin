import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumerableIsEmpty {

    private void enumerableIsEmpty() {
        assertThat("string").as("foo").isEmpty();
        assertThat(new StringBuilder()).as("bar").isEmpty();
        assertThat(new ArrayList<Long>()).as("etc").isEmpty();
        assertThat(new Long[1]).as("etc").isEmpty();

        assertThat("string").as("foo").hasSize(1);
        assertThat(new StringBuilder()).as("bar").hasSize(1);
        assertThat(new ArrayList<Long>()).as("etc").hasSize(1);
        assertThat(new Long[1]).as("etc").hasSize(1);

        assertThat("string").as("foo").hasSize(0).hasSameSizeAs("foo").isEmpty();
    }
}
