import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class AssertThatGuavaOptional {

    private void assertThatGuavaOptional() {
        Optional<String> opt = Optional.absent();

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
        assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).isEqualTo(Optional.absent());
        assertThat(opt).isNotEqualTo(Optional.absent());

        org.assertj.guava.api.Assertions.assertThat(opt).isEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        org.assertj.guava.api.Assertions.assertThat(opt).isEqualTo(Optional.absent());
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.absent());

        org.assertj.core.api.Assertions.assertThat(opt).isEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        org.assertj.core.api.Assertions.assertThat(opt).isEqualTo(Optional.absent());
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.absent());
    }
}
