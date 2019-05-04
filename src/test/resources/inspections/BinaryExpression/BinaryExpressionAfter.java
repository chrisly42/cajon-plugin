import static org.assertj.core.api.Assertions.assertThat;

public class BinaryExpression {

    private void binaryExpression() {
        int primExp = 42;
        int primAct = 1337;
        Double numberObjExp = 42.0;
        Double numberObjAct = 1337.0;
        String stringExp = "foo";
        String stringAct = "bar";

        assertThat(primAct).as("doh!").isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isNotEqualTo(1);

        assertThat(primAct).as("doh!").isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isEqualTo(1);

        assertThat(primAct).as("doh!").isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isLessThanOrEqualTo(1);

        assertThat(primAct).as("doh!").isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isLessThan(1);

        assertThat(primAct).as("doh!").isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isGreaterThanOrEqualTo(1);

        assertThat(primAct).as("doh!").isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isGreaterThan(1);

        assertThat(numberObjAct).as("doh!").isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(1);

        assertThat(numberObjAct).as("doh!").isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isEqualTo(1);

        assertThat(numberObjAct).as("doh!").isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);

        assertThat(numberObjAct).as("doh!").isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isLessThan(1);

        assertThat(numberObjAct).as("doh!").isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);

        assertThat(numberObjAct).as("doh!").isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isGreaterThan(1);

        assertThat(numberObjAct).as("doh!").isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);

        assertThat(stringAct).as("doh!").isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);

        assertThat(stringAct).as("doh!").isEqualTo(stringExp);
        assertThat(stringAct).isEqualTo(stringExp);
        assertThat(stringAct).isEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);

        assertThat(stringAct).as("doh!").isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);

        assertThat(stringAct).as("doh!").isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();

        assertThat(stringAct).as("doh!").isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();

        assertThat(null == null).isTrue();
        assertThat(!false).isTrue();

        assertThat(primAct).as("doh!").isEqualTo(primExp).isEqualTo(primExp);
        assertThat(primAct == primExp).isFalse().as("doh!").isEqualTo(true);

        assertThat(numberObjAct).as("doh!").isEqualTo(numberObjExp).isEqualTo(numberObjExp);
    }
}
