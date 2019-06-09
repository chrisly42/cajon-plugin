import static org.assertj.core.api.Assertions.assertThat;

public class ObjectExpression {

    private void objectExpression() {
        Integer object = 1;
        Integer otherObject = 1;
        boolean foo = false;

        assertThat(object).hasToString("foo");
        assertThat(object.toString()).isNotEqualTo("foo");

        assertThat(object).hasSameHashCodeAs(otherObject);
        assertThat(object.hashCode()).isEqualTo(123);

        assertThat(object).as("doh!").isEqualTo(otherObject);
        assertThat(object).isEqualTo(otherObject);
        assertThat(object).isEqualTo(otherObject);
        assertThat(object).isNotEqualTo(otherObject);
        assertThat(object).isNotEqualTo(otherObject);
        assertThat(object).isNotEqualTo(otherObject);

        assertThat(object.equals(otherObject)).isEqualTo(foo);

        assertThat(object).as("doh!").isEqualTo(otherObject).isEqualTo(otherObject);
    }
}
