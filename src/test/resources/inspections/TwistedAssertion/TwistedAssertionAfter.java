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

        assertThat(number).as("foo").isEqualTo(5 + 2);
        assertThat(number).as("foo").isNotEqualTo(8);
        assertThat(number).as("foo").isLessThanOrEqualTo(5 * 2);
        assertThat(number + 1).as("foo").isLessThan(4 + (1 - 2));
        assertThat(number * 2).as("foo").isGreaterThanOrEqualTo(3);
        assertThat(number / 2).as("foo").isGreaterThan(2 + SOME_CONST);
        assertThat(foo).as("foo").isEqualTo("foo");
        assertThat(foo).as("foo").isSameAs(SOME_CONST_STRING);
        assertThat(foo).as("foo").isNotEqualTo("bar");
        assertThat(foo).as("foo").isNotSameAs("bar");
        assertThat("bar").as("foo").startsWith(foo);
        assertThat("foo").as("foo").endsWith(foo);

        assertThat(bar).isEqualTo(foo);

        assertThat(4).isEqualTo(number).isNotEqualTo(number * 2);
        assertThat(4).usingComparator(Comparator.reverseOrder()).isGreaterThanOrEqualTo(number);

        org.junit.Assert.assertThat(list, null);
        fail("oh no!");
    }
}
