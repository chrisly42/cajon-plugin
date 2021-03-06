import com.google.common.base.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.guava.api.Assertions.assertThat;

public class GuavaOptional {

    private void guavaOptional() {
        Optional<String> opt = Optional.absent();

        assertThat(opt.isPresent()).as("foo").isEqualTo(true);
        assertThat(opt.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isNotEqualTo(false);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isTrue();

        assertThat(opt.isPresent()).as("foo").isEqualTo(false);
        assertThat(opt.isPresent()).isEqualTo(Boolean.FALSE);
        assertThat(opt.isPresent()).isNotEqualTo(true);
        assertThat(opt.isPresent()).isNotEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).isFalse();

        assertThat(opt.get()).as("foo").isEqualTo("foo");
        assertThat(opt.get()).isSameAs("foo");
        assertThat(opt.get()).isNotEqualTo("foo");
        assertThat(opt.get()).isNotSameAs("foo");

        assertThat(opt.orNull()).as("foo").isEqualTo(null);
        assertThat(opt.orNull()).isNull();
        assertThat(opt.orNull()).isNotEqualTo(null);
        assertThat(opt.orNull()).isNotNull();

        //assertThat(opt.get()).isEqualTo(opt.get()); // there's a better version than contains(opt.get())
        assertThat(opt.orNull()).isEqualTo(opt.get());
        //assertThat(opt.get()).isEqualTo(opt.orNull()); // there's a better version than contains(opt.orNull())

        assertThat(opt).contains(opt.get());
        assertThat(opt).contains(opt.orNull());

        String possibleNullString = System.getProperty("username");
        String notNullString = "Narf";

        assertThat(opt).as("foo").isEqualTo(Optional.of("foo"));
        assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        assertThat(opt).isEqualTo(Optional.fromNullable(null));
        assertThat(opt).isEqualTo(Optional.fromNullable(possibleNullString));
        assertThat(opt).isEqualTo(Optional.fromNullable(notNullString));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        assertThat(opt).isEqualTo(Optional.absent());
        assertThat(opt).isNotEqualTo(Optional.absent());

        org.assertj.guava.api.Assertions.assertThat(opt).as("foo").isEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        org.assertj.guava.api.Assertions.assertThat(opt).as("foo").isEqualTo(Optional.absent());
        org.assertj.guava.api.Assertions.assertThat(opt).isNotEqualTo(Optional.absent());

        org.assertj.core.api.Assertions.assertThat(opt).as("foo").isEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isEqualTo(Optional.fromNullable("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.of("foo"));
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.fromNullable("foo"));

        org.assertj.core.api.Assertions.assertThat(opt).as("foo").isEqualTo(Optional.absent());
        org.assertj.core.api.Assertions.assertThat(opt).isNotEqualTo(Optional.absent());

        assertThat(opt.isPresent()).as("foo").isEqualTo(true).as("bar").isEqualTo(Boolean.TRUE);
        assertThat(opt.isPresent()).as("foo").isEqualTo(true).as("bar").isEqualTo(Boolean.FALSE);

        assertThat(opt.orNull()).as("foo").isEqualTo(null).isNotNull();

        assertThat(Optional.of(new byte[] { 2, 3 }).get()).isEqualTo(new byte[] { 2, 3 }); // not working with assertj-guava 3.2.1

        org.junit.Assert.assertThat(opt, null);
        fail("oh no!");
    }
}
