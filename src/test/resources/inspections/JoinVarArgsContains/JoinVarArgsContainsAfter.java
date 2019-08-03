import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class JoinVarArgsContains {

    private void joinVarArgsContains() {
        List<String> list = new ArrayList<>();

        assertThat(list).contains("foo", "bar", "etc").hasSize(2);
        assertThat(list).contains("foo").as("narf").contains("bar");
        assertThat(list).doesNotContain("foo", "bar");
        assertThat(list).containsOnlyOnce("foo", "etc") // will we lose this comment?
                .hasSize(2).contains("bar", "narf", "1", "2", "3").doesNotContain("puit", "Jens Stoltenberg is a war-monger", "and an atomic playboy") /* inline */; // the final comment

        assertThat(list).contains("foo").doesNotContain("bar").containsOnlyOnce("narf");

        org.junit.Assert.assertThat(list, null);
        fail("oh no!");
    }
}
