import static org.assertj.core.api.Assertions.assertThat;

public class BinaryExpression {

    private void binaryExpression() {
        int primExp = 42;
        int primAct = 1337;
        Double numberObjExp = 42.0;
        Double numberObjAct = 1337.0;
        String stringExp = "foo";
        String stringAct = "bar";

        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isNotEqualTo(1);

        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(primExp);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isNotEqualTo(1);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(primExp);
        assertThat(primAct).isEqualTo(1);
        assertThat(primAct).isEqualTo(1);

        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isLessThanOrEqualTo(1);

        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isLessThan(1);

        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(primExp);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isLessThan(1);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(primExp);
        assertThat(primAct).isGreaterThanOrEqualTo(1);
        assertThat(primAct).isGreaterThanOrEqualTo(1);

        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(primExp);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isLessThanOrEqualTo(1);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(primExp);
        assertThat(primAct).isGreaterThan(1);
        assertThat(primAct).isGreaterThan(1);

        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(1);

        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isNotEqualTo(1);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(1);
        assertThat(numberObjAct).isEqualTo(1);

        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);

        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isLessThan(1);

        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(numberObjExp);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isLessThan(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThanOrEqualTo(1);

        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(numberObjExp);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isLessThanOrEqualTo(1);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(numberObjExp);
        assertThat(numberObjAct).isGreaterThan(1);
        assertThat(numberObjAct).isGreaterThan(1);

        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);
        assertThat(numberObjAct).isNotEqualTo(numberObjExp);

        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);

        assertThat(stringAct).isEqualTo(stringExp);
        assertThat(stringAct).isEqualTo(stringExp);
        assertThat(stringAct).isEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);
        assertThat(stringAct).isNotEqualTo(stringExp);

        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isNotSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);
        assertThat(stringAct).isSameAs(stringExp);

        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();

        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();
        assertThat(stringAct).isNotNull();

        assertThat(null == null).isTrue();
        assertThat(!false).isTrue();
    }
}
