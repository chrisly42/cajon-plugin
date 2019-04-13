import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;

public class AssertThatGuavaOptional {

    private void assertThatGuavaOptional() {
        Optional<String> opt = Optional.absent();

        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();

        assertThat(opt).contains("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).contains("foo");
        assertThat(opt).contains("foo");
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).isAbsent();
        assertThat(opt).isPresent();

        org.assertj.guava.api.Assertions.assertThat(opt).contains("foo");
        org.assertj.guava.api.Assertions.assertThat(opt).contains("foo");
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        org.assertj.guava.api.Assertions.assertThat(opt).isAbsent();
        org.assertj.guava.api.Assertions.assertThat(opt).isPresent();

        assertThat(opt).contains("foo");
        assertThat(opt).contains("foo");
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).isAbsent();
        assertThat(opt).isPresent();
    }
}
