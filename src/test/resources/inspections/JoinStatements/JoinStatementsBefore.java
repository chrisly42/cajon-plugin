import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class JoinStatements {

    private void joinStatements() {
        List<String> list = new ArrayList<>();
        List<String> otherList = new ArrayList<>();

        // the future is always born in pain
        /* tricky */assertThat(list).as("foo").hasSize(2); /* do one */ /* do another */
        assertThat(list).as("bar").contains("barbar"); // comment to keep
        assertThat(list).doesNotContain("barbara") // another comment to keep
                .doesNotContain("wrzlbrmpft") // across two lines
        ; /* and a multi line comment
        after the statement */
        assertThat(list).as("etc")/* what a nasty comment */.contains("etcetc");

        // moar!
        assertThat(list).doesNotContain("foobar");

        assertThat("narf").isNotEqualTo("puit");
        assertThat(list).as("bar").contains("barbar");
        assertThat(list).as("foo").hasSize(2);
        assertThat(list).as("evil").extracting(String::length).contains(2);

        assertThat(list).as("bar").contains("barbar");
        assertThat(otherList).contains("puit");
        assertThat(list).as("foo").hasSize(2);
        if (true) {
            assertThat(list).doesNotContain("narf");
            assertThat(list).as("bar").contains("barbar");
        }
        assertThat(list.get(0)).isNotEmpty();
        assertThat(list.get(0)).hasSize(3);
        assertThat(list.get(0)).isEqualTo("bar");

        assertThat(otherList.get(0)).isNotEmpty();
        assertThat(list.get(0)).hasSize(3);

        assertThat(list.get(0) + "foo").isNotNull();
        // hey, a comment mixed with line breaks due to too many joins
        assertThat(list.get(0) + "foo").isEqualTo("bar");
        assertThat(list.get(0) + "foo").doesNotStartWith("foo");

        assertThat(otherList.get(0) + "foo").isEqualTo("bar");
        assertThat(list.get(0) + "foo").doesNotStartWith("foo");

        Iterator<String> iterator = list.iterator();
        assertThat(iterator.next()).isEqualTo("foo");
        assertThat(iterator.next()).isEqualTo("bar");
        assertThat(iterator.next().toLowerCase()).isEqualTo("foo");
        assertThat(iterator.next().toLowerCase()).isEqualTo("bar");
        assertThat(iterator.next() + "bar").isEqualTo("foobar");
        assertThat(iterator.next() + "bar").isEqualTo("barbar");
        int i = 0;
        assertThat(++i).isEqualTo(1);
        assertThat(++i).isEqualTo(2);
        assertThat(list.get(i++).toLowerCase()).isEqualTo("foo");
        assertThat(list.get(i++).toLowerCase()).isEqualTo("foo");
        assertThat(list.get(--i)).isEqualTo("foo");
        assertThat(list.get(--i)).isEqualTo("foo");

        org.junit.Assert.assertThat(foo, null);
        fail("oh no!");
    }
}
