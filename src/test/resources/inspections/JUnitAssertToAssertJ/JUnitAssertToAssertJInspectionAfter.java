import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.junit.Assert.*;

public class JUnitAssertToAssertJ {

    private void jUnitAssertToAssertJ() {
        String foo = "foo";
        String bar = "bar";
        assertThat(foo == "foo").isTrue();
        assertThat(foo == "foo").as("oh no!").isTrue();
        assertThat(foo == "bar").isFalse();
        assertThat(foo == "bar").as("boom!").isFalse();

        assertThat(foo).isNull();
        assertThat(foo).as("oh no!").isNull();
        assertThat(foo).isNotNull();
        assertThat(foo).as("oh no!").isNotNull();

        assertThat(foo).isEqualTo(bar);
        assertThat(foo).as("equals").isEqualTo(bar);
        assertThat(foo).isNotEqualTo(bar);
        assertThat(foo).as("equals").isNotEqualTo(bar);

        assertThat(foo).isSameAs(bar);
        assertThat(foo).as("same").isSameAs(bar);
        assertThat(foo).isNotSameAs(bar);
        assertThat(foo).as("same").isNotSameAs(bar);

        assertThat(2.0).isEqualTo(1.0);
        assertThat(2.0).isCloseTo(1.0, offset(0.1));
        assertThat(2.0).as("equals").isEqualTo(1.0);
        assertThat(2.0).as("equals").isCloseTo(1.0, offset(0.1));
        assertThat(2.0f).isEqualTo(1.0f);
        assertThat(2.0f).isCloseTo(1.0f, offset(0.1f));
        assertThat(2.0f).as("equals").isEqualTo(1.0f);
        assertThat(2.0f).as("equals").isCloseTo(1.0f, offset(0.1f));

        assertThat(2.0).isNotEqualTo(1.0);
        assertThat(2.0).isNotCloseTo(1.0, offset(0.1));
        assertThat(2.0).as("equals").isNotEqualTo(1.0);
        assertThat(2.0).as("equals").isNotCloseTo(1.0, offset(0.1));
        assertThat(2.0f).isNotEqualTo(1.0f);
        assertThat(2.0f).isNotCloseTo(1.0f, offset(0.1f));
        assertThat(2.0f).as("equals").isNotEqualTo(1.0f);
        assertThat(2.0f).as("equals").isNotCloseTo(1.0f, offset(0.1f));

        assertThat(new int[1]).containsExactly(new int[2]);
        assertThat(new int[1]).as("array equals").containsExactly(new int[2]);

        assertThat(new double[1]).containsExactly(new double[2], offset(1.0));
        assertThat(new double[1]).as("array equals").containsExactly(new double[2], offset(1.0));
        assertThat(new float[1]).containsExactly(new float[2], offset(1.0f));
        assertThat(new float[1]).as("array equals").containsExactly(new float[2], offset(1.0f));
    }
}
