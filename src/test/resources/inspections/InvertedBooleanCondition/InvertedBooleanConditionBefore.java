import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class InvertedBooleanCondition {

    private void invertedBooleanCondition(boolean primitiveVariable, Boolean objectVariable) {
        boolean primitive = false;
        Boolean object = Boolean.TRUE;

        assertThat(!primitive).as("foo").isEqualTo(Boolean.TRUE);
        assertThat(!primitive).isEqualTo(true);
        assertThat(!primitive).isNotEqualTo(Boolean.FALSE);
        assertThat(!primitive).isNotEqualTo(false);
        assertThat(!primitive).isTrue();

        assertThat(!object).as("foo").isEqualTo(Boolean.TRUE);
        assertThat(!object).isEqualTo(true);
        assertThat(!object).isNotEqualTo(Boolean.FALSE);
        assertThat(!object).isNotEqualTo(false);
        assertThat(!object).isTrue();

        assertThat(!primitive).as("foo").isEqualTo(Boolean.FALSE);
        assertThat(!primitive).isEqualTo(false);
        assertThat(!primitive).isNotEqualTo(Boolean.TRUE);
        assertThat(!primitive).isNotEqualTo(true);
        assertThat(!primitive).isFalse();

        assertThat(!object).as("foo").isEqualTo(Boolean.FALSE);
        assertThat(!object).isEqualTo(false);
        assertThat(!object).isNotEqualTo(Boolean.TRUE);
        assertThat(!object).isNotEqualTo(true);
        assertThat(!object).isFalse();

        assertThat(!(((!((primitive)))))).as("nah").isEqualTo(true && !true);
        assertThat(!object).isEqualTo(Boolean.TRUE && !Boolean.TRUE);

        assertThat(!primitive).as("foo").isEqualTo(Boolean.TRUE).as("bar").isNotEqualTo(false);
        assertThat(!primitive).as("foo").isEqualTo(Boolean.TRUE).as("bar").isNotEqualTo(true);

        assertThat(primitive).isEqualTo(!primitiveVariable);
        assertThat(primitive).isNotEqualTo(!primitiveVariable);

        assertThat(object).isEqualTo(!primitiveVariable);
        assertThat(object).isNotEqualTo(!primitiveVariable);

        assertThat(primitive).isEqualTo(!objectVariable); // not safe for null value
        assertThat(primitive).isNotEqualTo(!objectVariable); // not safe for null value

        assertThat(object).isEqualTo(!objectVariable); // not safe for null value
        assertThat(object).isNotEqualTo(!objectVariable); // not safe for null value

        assertThat(primitive).isEqualTo((!primitiveVariable));
        assertThat(primitive).isEqualTo(!(primitiveVariable));

        assertThat(primitive).isEqualTo(!primitiveVariable && objectVariable);

        org.junit.Assert.assertThat(object, null);
        fail("oh no!");
    }
}
