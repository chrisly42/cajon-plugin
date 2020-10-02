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

        assertThat("string").as("foo").hasSizeLessThanOrEqualTo(0);
        assertThat(new StringBuilder()).as("bar").hasSizeLessThanOrEqualTo(0 + 0);
        assertThat(new ArrayList<Long>()).as("etc").hasSizeLessThanOrEqualTo(10 / 100);
        assertThat(new Long[1]).as("etc").hasSizeLessThanOrEqualTo(1 - 1);

        assertThat("string").as("foo").hasSizeLessThan(1);
        assertThat(new StringBuilder()).as("bar").hasSizeLessThan(0 + 1);
        assertThat(new ArrayList<Long>()).as("etc").hasSizeLessThan(10 / 10);
        assertThat(new Long[1]).as("etc").hasSizeLessThan(1 - 0);

        assertThat("string").as("foo").hasSizeGreaterThan(0);
        assertThat(new StringBuilder()).as("bar").hasSizeGreaterThan(0 + 0);
        assertThat(new ArrayList<Long>()).as("etc").hasSizeGreaterThan(10 / 100);
        assertThat(new Long[1]).as("etc").hasSizeGreaterThan(1 - 1);

        assertThat("string").as("foo").hasSizeGreaterThanOrEqualTo(1);
        assertThat(new StringBuilder()).as("bar").hasSizeGreaterThanOrEqualTo(0 + 1);
        assertThat(new ArrayList<Long>()).as("etc").hasSizeGreaterThanOrEqualTo(10 / 10);
        assertThat(new Long[1]).as("etc").hasSizeGreaterThanOrEqualTo(1 - 0);

        assertThat("string").as("foo").hasSize(0).hasSameSizeAs("foo").hasSize(0);
        assertThat("string").as("foo").hasSizeLessThanOrEqualTo(0).hasSameSizeAs("foo").hasSizeLessThanOrEqualTo(0);
        assertThat("string").as("foo").hasSizeLessThan(1).hasSameSizeAs("foo").hasSizeLessThan(1);

        assertThat("string").as("foo").hasSizeGreaterThan(0).hasSameSizeAs("foo").hasSizeGreaterThan(0);
        assertThat("string").as("foo").hasSizeGreaterThanOrEqualTo(1).hasSameSizeAs("foo").hasSizeGreaterThanOrEqualTo(1);

        org.junit.Assert.assertThat("foo", null);
        fail("oh no!");
    }
}
