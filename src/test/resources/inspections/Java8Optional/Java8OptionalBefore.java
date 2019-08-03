import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

        assertThat(opt.orElse(null)).as("foo").isEqualTo(null);
        assertThat(opt.orElse(null)).isNull();
        assertThat(opt.orElse(null)).isNotEqualTo(null);
        assertThat(opt.orElse(null)).isNotNull();

        //assertThat(opt.get()).isEqualTo(opt.get()); // there's a better version than contains(opt.get())
        assertThat(opt.orElse(null)).isEqualTo(opt.get());
        //assertThat(opt.get()).isEqualTo(opt.orElse(null)); // there's a better version than contains(opt.orElse(null))

        assertThat(opt).contains(opt.get());
        assertThat(opt).contains(opt.orElse(null));

        String possibleNullString = System.getProperty("username");
        String notNullString = "Narf";

        assertThat(opt).as("foo").isEqualTo(Optional.of("foo"));
        assertThat(opt).isEqualTo(Optional.ofNullable("foo"));
        assertThat(opt).isEqualTo(Optional.ofNullable(null));
        assertThat(opt).isEqualTo(Optional.ofNullable(possibleNullString));
        assertThat(opt).isEqualTo(Optional.ofNullable(notNullString));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).as("foo").isEqualTo(Optional.empty());
        assertThat(opt).isNotEqualTo(Optional.empty());

        assertThat(opt.isPresent()).as("foo").isEqualTo(true).as("bar").isTrue();
        assertThat(opt.isPresent()).as("foo").isEqualTo(false).as("bar").isTrue();

        assertThat(opt.get()).isEqualTo("foo").isSameAs("foo").isNotEqualTo("foo").isNotSameAs("foo");

        assertThat(opt.orElse("foo")).as("foo").isEqualTo(null);

        assertThat(opt.orElse(null)).as("foo").isEqualTo(null).isNotNull();

        org.junit.Assert.assertThat(opt, null);
        fail("oh no!");
    }
}
