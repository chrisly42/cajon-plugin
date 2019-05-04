import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Java8Optional {

    private void java8Optional() {
        Optional<String> opt = Optional.empty();

        assertThat(opt).as("foo").isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isNotPresent();

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).containsSame("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).contains("foo");
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).as("foo").isNotPresent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").isPresent().as("bar").isPresent();
        assertThat(opt.isPresent()).as("foo").isEqualTo(false).as("bar").isTrue();

        assertThat(opt.get()).isEqualTo("foo").isSameAs("foo").isNotEqualTo("foo").isNotSameAs("foo");
    }
}
