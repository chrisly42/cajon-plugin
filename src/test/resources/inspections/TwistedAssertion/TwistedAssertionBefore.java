import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TwistedAssertions {

    private static final int SOME_CONST = 10;
    private static final String SOME_CONST_STRING = "bar";

    private void twistedAssertions() {
        List<String> list = new ArrayList<>();
        String foo = "foo";
        String bar = "bar";
        int number = 4;

        assertThat(5 + 2).as("foo").isEqualTo(number);
        assertThat(8).as("foo").isNotEqualTo(number);
        assertThat(5 * 2).as("foo").isGreaterThan(number);
        assertThat(4 + (1 - 2)).as("foo").isGreaterThanOrEqualTo(number + 1);
        assertThat(3).as("foo").isLessThan(number * 2);
        assertThat(2 + SOME_CONST).as("foo").isLessThanOrEqualTo(number / 2);
        assertThat("foo").as("foo").isEqualTo(foo);
        assertThat(SOME_CONST_STRING).as("foo").isSameAs(foo);
        assertThat("bar").as("foo").isNotEqualTo(foo);
        assertThat("bar").as("foo").isNotSameAs(foo);
        assertThat("bar").as("foo").startsWith(foo);
        assertThat("foo").as("foo").endsWith(foo);

        assertThat(bar).isEqualTo(foo);

        assertThat(4).isEqualTo(number).isNotEqualTo(number * 2);
        assertThat(4).usingComparator(Comparator.reverseOrder()).isGreaterThanOrEqualTo(number);

        org.junit.Assert.assertThat(list, null);
        fail("oh no!");
    }
}
