import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InvertedBooleanCondition {

    private void invertedBooleanCondition(boolean primitiveVariable, Boolean objectVariable) {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(primitive).as("foo").isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();
        assertThat(primitive).isFalse();

        assertThat(object).as("foo").isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();
        assertThat(object).isFalse();

        assertThat(primitive).as("foo").isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();
        assertThat(primitive).isTrue();

        assertThat(object).as("foo").isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();
        assertThat(object).isTrue();

        assertThat(!((primitive))).as("nah").isTrue();
        assertThat(!object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);

        assertThat(primitive).as("foo").isFalse().as("bar").isFalse();
        assertThat(primitive).as("foo").isFalse().as("bar").isTrue();

        assertThat(primitive).isNotEqualTo(primitiveVariable);
        assertThat(primitive).isEqualTo(primitiveVariable);

        assertThat(object).isNotEqualTo(primitiveVariable);
        assertThat(object).isEqualTo(primitiveVariable);

        assertThat(primitive).isEqualTo(!objectVariable); // not safe for null value
        assertThat(primitive).isNotEqualTo(!objectVariable); // not safe for null value

        assertThat(object).isEqualTo(!objectVariable); // not safe for null value
        assertThat(object).isNotEqualTo(!objectVariable); // not safe for null value

        assertThat(primitive).isNotEqualTo(primitiveVariable);
        assertThat(primitive).isNotEqualTo(primitiveVariable);

        assertThat(primitive).isEqualTo(!primitiveVariable && objectVariable);

        org.junit.Assert.assertThat(object, null);
        fail("oh no!");
    }
}
