import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinStatements {

    private void joinStatements() {
        List<String> list = new ArrayList<>();
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
        assertThat("narf").isNotEqualTo("puit");
        assertThat(list).as("foo").hasSize(2);
        if (true) {
            assertThat(list).doesNotContain("narf");
            assertThat(list).as("bar").contains("barbar");
        }
        assertThat(list.get(0)).isNotEmpty();
        assertThat(list.get(0)).hasSize(3);
        assertThat(list.get(0)).isEqualTo("bar");

        assertThat(list.get(0) + "foo").isEqualTo("bar");
        assertThat(list.get(0) + "foo").doesNotStartWith("foo");

        Iterator<String> iterator = list.iterator();
        assertThat(iterator.next()).isEqualTo("foo");
        assertThat(iterator.next()).isEqualTo("bar");
    }
}
