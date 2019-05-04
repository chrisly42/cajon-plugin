import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class Java8Optional {

    private void java8Optional() {
        Optional<String> opt = Optional.empty();

        assertThat(opt.isPresent()).as("foo").isEqualTo(true);
        assertThat(opt.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isNotEqualTo(false);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isTrue();

        assertThat(opt.isPresent()).isEqualTo(false);
        assertThat(opt.isPresent()).isEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isNotEqualTo(true);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isFalse();

        assertThat(opt.get()).as("foo").isEqualTo("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).as("foo").isEqualTo(Optional.of("foo"));
        assertThat(opt).isEqualTo(Optional.ofNullable("foo"));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).as("foo").isEqualTo(Optional.empty());
        assertThat(opt).isNotEqualTo(Optional.empty());

        assertThat(opt.isPresent()).as("foo").isEqualTo(true).as("bar").isTrue();
        assertThat(opt.isPresent()).as("foo").isEqualTo(false).as("bar").isTrue();

        assertThat(opt.get()).isEqualTo("foo").isSameAs("foo").isNotEqualTo("foo").isNotSameAs("foo");
    }
}
