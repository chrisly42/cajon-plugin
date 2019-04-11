import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AssertThatJava8Optional {

    private void assertThatJava8Optional() {
        Optional<String> opt = Optional.empty();

        assertThat(opt.isPresent()).isEqualTo(true);
        assertThat(opt.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isNotEqualTo(false);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isTrue();

        assertThat(opt.isPresent()).isEqualTo(false);
        assertThat(opt.isPresent()).isEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isNotEqualTo(true);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isFalse();

        assertThat(opt.get()).isEqualTo("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).isEqualTo(Optional.of("foo"));
        assertThat(opt).isEqualTo(Optional.ofNullable("foo"));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).isEqualTo(Optional.empty());
        assertThat(opt).isNotEqualTo(Optional.empty());
    }
}
