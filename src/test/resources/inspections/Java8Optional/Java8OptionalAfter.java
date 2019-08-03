import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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

        assertThat(opt).as("foo").isNotPresent();
        assertThat(opt).isNotPresent();
        assertThat(opt).isPresent();
        assertThat(opt).isPresent();

        //assertThat(opt.get()).isEqualTo(opt.get()); // there's a better version than contains(opt.get())
        assertThat(opt.orElse(null)).isEqualTo(opt.get());
        //assertThat(opt.get()).isEqualTo(opt.orElse(null)); // there's a better version than contains(opt.orElse(null))

        assertThat(opt).contains(opt.get());
        assertThat(opt).contains(opt.orElse(null));

        String possibleNullString = System.getProperty("username");
        String notNullString = "Narf";

        assertThat(opt).as("foo").contains("foo");
        assertThat(opt).contains("foo");
        assertThat(opt).isEqualTo(Optional.ofNullable(null));
        assertThat(opt).isEqualTo(Optional.ofNullable(possibleNullString));
        assertThat(opt).isEqualTo(Optional.ofNullable(notNullString));
        assertThat(opt).isNotEqualTo(Optional.of("foo"));
        assertThat(opt).isNotEqualTo(Optional.ofNullable("foo"));

        assertThat(opt).as("foo").isNotPresent();
        assertThat(opt).isPresent();

        assertThat(opt).as("foo").isPresent().as("bar").isPresent();
        assertThat(opt.isPresent()).as("foo").isEqualTo(false).as("bar").isTrue();

        assertThat(opt.get()).isEqualTo("foo").isSameAs("foo").isNotEqualTo("foo").isNotSameAs("foo");

        assertThat(opt.orElse("foo")).as("foo").isEqualTo(null);

        assertThat(opt.orElse(null)).as("foo").isEqualTo(null).isNotNull();

        org.junit.Assert.assertThat(opt, null);
        fail("oh no!");
    }
}
