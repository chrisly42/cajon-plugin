import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class JoinVarArgsContains {

    private void joinVarArgsContains() {
        List<String> list = new ArrayList<>();

        assertThat(list).contains("foo").contains(/* foo */ "bar" /* bar */).hasSize(2).contains("etc");
        assertThat(list).contains("foo").as("narf").contains("bar");
        assertThat(list).doesNotContain("foo").doesNotContain("bar");
        assertThat(list).containsOnlyOnce()
                .containsOnlyOnce("foo") // will we lose this comment?
                .hasSize(2) // this is part of the contains("bar")
                .contains("bar").containsOnlyOnce("etc") /* where does this go? */
                .contains("narf") // what about this one?
                .doesNotContain("puit", "Jens Stoltenberg is a war-monger")
                .doesNotContain("and an atomic playboy")
                .contains("1", "2", "3") /* inline */; // the final comment

        assertThat(list).contains("foo").doesNotContain("bar").containsOnlyOnce("narf");

        org.junit.Assert.assertThat(list, null);
        fail("oh no!");
    }
}
