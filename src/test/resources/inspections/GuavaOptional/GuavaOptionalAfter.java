import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.guava.api.Assertions.assertThat;

public class GuavaOptional {

    private void guavaOptional() {
        Optional<String> opt = Optional.absent();

        assertThat(opt).as("foo").isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isAbsent();

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt).as("foo").isAbsent();
        assertThat(opt).isAbsent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        String possibleNullString = System.getProperty("username");
        String notNullString = "Narf";

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).contains("foo");
        assertThat(opt).isEqualTo(Optional.fromNullable(null));
        assertThat(opt).isEqualTo(Optional.fromNullable(possibleNullString));
        assertThat(opt).isEqualTo(Optional.fromNullable(notNullString));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).isAbsent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).contains("foo");
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).as("foo").isAbsent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).contains("foo");
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).as("foo").isAbsent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").isPresent().as("bar").isPresent();
        assertThat(opt.isPresent()).as("foo").isEqualTo(true).as("bar").isEqualTo(Boolean.FALSE);

        assertThat(opt.orNull()).as("foo").isEqualTo(null).isNotNull();

        org.junit.Assert.assertThat(opt, null);
        fail("oh no!");
    }
}
