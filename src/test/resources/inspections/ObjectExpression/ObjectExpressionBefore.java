import static org.assertj.core.api.Assertions.assertThat;

public class ObjectExpression {

    private void objectExpression() {
        Integer object = 1;
        Integer otherObject = 1;
        boolean foo = false;

        assertThat(object.toString()).isEqualTo("foo");
        assertThat(object.toString()).isNotEqualTo("foo");

        assertThat(object.hashCode()).isEqualTo(otherObject.hashCode());
        assertThat(object.hashCode()).isEqualTo(123);

        assertThat(object.equals(otherObject)).as("doh!").isTrue();
        assertThat(object.equals(otherObject)).isEqualTo(true);
        assertThat(object.equals(otherObject)).isNotEqualTo(false);
        assertThat(object.equals(otherObject)).isFalse();
        assertThat(object.equals(otherObject)).isEqualTo(false);
        assertThat(object.equals(otherObject)).isNotEqualTo(true);

        assertThat(object.equals(otherObject)).isEqualTo(foo);

        assertThat(object.equals(otherObject)).as("doh!").isTrue().isEqualTo(true);

        org.junit.Assert.assertThat(foo, null);
        fail("oh no!");
    }
}
