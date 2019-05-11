import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinStatements {

    private void joinStatements() {
        List<String> list = new ArrayList<>();
        List<String> otherList = new ArrayList<>();

        // the future is always born in pain
        /* tricky */
        assertThat(list).as("foo").hasSize(2)
                /* do another */
                /* do one */.as("bar").contains("barbar")
                // comment to keep
                .doesNotContain("barbara") // another comment to keep
                .doesNotContain("wrzlbrmpft")
                /* and a multi line comment
                    after the statement */
                // across two lines
                .as("etc")/* what a nasty comment */.contains("etcetc")
                // moar!
                .doesNotContain("foobar");

        assertThat("narf").isNotEqualTo("puit");
        assertThat(list).as("bar").contains("barbar").as("foo").hasSize(2);
        assertThat(list).as("evil").extracting(String::length).contains(2);

        assertThat(list).as("bar").contains("barbar");
        assertThat(otherList).contains("puit");
        assertThat(list).as("foo").hasSize(2);
        if (true) {
            assertThat(list).doesNotContain("narf").as("bar").contains("barbar");
        }
        assertThat(list.get(0)).isNotEmpty().hasSize(3).isEqualTo("bar");

        assertThat(otherList.get(0)).isNotEmpty();
        assertThat(list.get(0)).hasSize(3);

        assertThat(list.get(0) + "foo").isEqualTo("bar").doesNotStartWith("foo");

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
    }
}
