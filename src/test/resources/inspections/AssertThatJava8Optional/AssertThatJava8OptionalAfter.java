import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertThatJava8Optional {

    private void assertThatJava8Optional() {
        Optional<String> opt = Optional.empty();

        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();

        assertThat(opt).contains("foo");
        assertThat(opt).containsSame("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).contains("foo");
        assertThat(opt).contains("foo");
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).isNotPresent();
        assertThat(opt).isPresent();
    }
}
