import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class GuavaOptional {

    private void guavaOptional() {
        Optional<String> opt = Optional.absent();

        assertThat(opt).isEqualTo(Optional.of("foo"));
        assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt.isPresent()).isTrue();
        assertThat(opt.isPresent()).isFalse();

        assertThat(opt.get()).isEqualTo("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).isEqualTo(Optional.absent());
        assertThat(opt).isNotEqualTo(Optional.absent());
    }
}
