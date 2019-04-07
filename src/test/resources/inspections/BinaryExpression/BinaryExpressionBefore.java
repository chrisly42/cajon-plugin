import static org.assertj.core.api.Assertions.assertThat;

public class BinaryExpression {

    private void binaryExpression() {
        int primExp = 42;
        int primAct = 1337;
        Double numberObjExp = 42.0;
        Double numberObjAct = 1337.0;
        String stringExp = "foo";
        String stringAct = "bar";

        assertThat(primAct == primExp).isTrue();
        assertThat(primAct == primExp).isEqualTo(true);
        assertThat(primAct == primExp).isNotEqualTo(false);
        assertThat(primAct == 1).isTrue();
        assertThat(1 == primAct).isTrue();
        assertThat(primAct == primExp).isFalse();
        assertThat(primAct == primExp).isEqualTo(false);
        assertThat(primAct == primExp).isNotEqualTo(true);
        assertThat(primAct == 1).isFalse();
        assertThat(1 == primAct).isFalse();

        assertThat(primAct != primExp).isTrue();
        assertThat(primAct != primExp).isEqualTo(true);
        assertThat(primAct != primExp).isNotEqualTo(false);
        assertThat(primAct != 1).isTrue();
        assertThat(1 != primAct).isTrue();
        assertThat(primAct != primExp).isFalse();
        assertThat(primAct != primExp).isEqualTo(false);
        assertThat(primAct != primExp).isNotEqualTo(true);
        assertThat(primAct != 1).isFalse();
        assertThat(1 != primAct).isFalse();

        assertThat(primAct > primExp).isTrue();
        assertThat(primAct > primExp).isEqualTo(true);
        assertThat(primAct > primExp).isNotEqualTo(false);
        assertThat(primAct > 1).isTrue();
        assertThat(1 < primAct).isTrue();
        assertThat(primAct > primExp).isFalse();
        assertThat(primAct > primExp).isEqualTo(false);
        assertThat(primAct > primExp).isNotEqualTo(true);
        assertThat(primAct > 1).isFalse();
        assertThat(1 < primAct).isFalse();

        assertThat(primAct >= primExp).isTrue();
        assertThat(primAct >= primExp).isEqualTo(true);
        assertThat(primAct >= primExp).isNotEqualTo(false);
        assertThat(primAct >= 1).isTrue();
        assertThat(1 <= primAct).isTrue();
        assertThat(primAct >= primExp).isFalse();
        assertThat(primAct >= primExp).isEqualTo(false);
        assertThat(primAct >= primExp).isNotEqualTo(true);
        assertThat(primAct >= 1).isFalse();
        assertThat(1 <= primAct).isFalse();

        assertThat(primAct < primExp).isTrue();
        assertThat(primAct < primExp).isEqualTo(true);
        assertThat(primAct < primExp).isNotEqualTo(false);
        assertThat(primAct < 1).isTrue();
        assertThat(1 > primAct).isTrue();
        assertThat(primAct < primExp).isFalse();
        assertThat(primAct < primExp).isEqualTo(false);
        assertThat(primAct < primExp).isNotEqualTo(true);
        assertThat(primAct < 1).isFalse();
        assertThat(1 > primAct).isFalse();

        assertThat(primAct <= primExp).isTrue();
        assertThat(primAct <= primExp).isEqualTo(true);
        assertThat(primAct <= primExp).isNotEqualTo(false);
        assertThat(primAct <= 1).isTrue();
        assertThat(1 >= primAct).isTrue();
        assertThat(primAct <= primExp).isFalse();
        assertThat(primAct <= primExp).isEqualTo(false);
        assertThat(primAct <= primExp).isNotEqualTo(true);
        assertThat(primAct <= 1).isFalse();
        assertThat(1 >= primAct).isFalse();

        assertThat(numberObjAct == numberObjExp).isTrue();
        assertThat(numberObjAct == numberObjExp).isEqualTo(true);
        assertThat(numberObjAct == numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct == 1).isTrue();
        assertThat(1 == numberObjAct).isTrue();
        assertThat(numberObjAct == numberObjExp).isFalse();
        assertThat(numberObjAct == numberObjExp).isEqualTo(false);
        assertThat(numberObjAct == numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct == 1).isFalse();
        assertThat(1 == numberObjAct).isFalse();

        assertThat(numberObjAct != numberObjExp).isTrue();
        assertThat(numberObjAct != numberObjExp).isEqualTo(true);
        assertThat(numberObjAct != numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct != 1).isTrue();
        assertThat(1 != numberObjAct).isTrue();
        assertThat(numberObjAct != numberObjExp).isFalse();
        assertThat(numberObjAct != numberObjExp).isEqualTo(false);
        assertThat(numberObjAct != numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct != 1).isFalse();
        assertThat(1 != numberObjAct).isFalse();

        assertThat(numberObjAct > numberObjExp).isTrue();
        assertThat(numberObjAct > numberObjExp).isEqualTo(true);
        assertThat(numberObjAct > numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct > 1).isTrue();
        assertThat(1 < numberObjAct).isTrue();
        assertThat(numberObjAct > numberObjExp).isFalse();
        assertThat(numberObjAct > numberObjExp).isEqualTo(false);
        assertThat(numberObjAct > numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct > 1).isFalse();
        assertThat(1 < numberObjAct).isFalse();

        assertThat(numberObjAct >= numberObjExp).isTrue();
        assertThat(numberObjAct >= numberObjExp).isEqualTo(true);
        assertThat(numberObjAct >= numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct >= 1).isTrue();
        assertThat(1 <= numberObjAct).isTrue();
        assertThat(numberObjAct >= numberObjExp).isFalse();
        assertThat(numberObjAct >= numberObjExp).isEqualTo(false);
        assertThat(numberObjAct >= numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct >= 1).isFalse();
        assertThat(1 <= numberObjAct).isFalse();

        assertThat(numberObjAct < numberObjExp).isTrue();
        assertThat(numberObjAct < numberObjExp).isEqualTo(true);
        assertThat(numberObjAct < numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct < 1).isTrue();
        assertThat(1 > numberObjAct).isTrue();
        assertThat(numberObjAct < numberObjExp).isFalse();
        assertThat(numberObjAct < numberObjExp).isEqualTo(false);
        assertThat(numberObjAct < numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct < 1).isFalse();
        assertThat(1 > numberObjAct).isFalse();

        assertThat(numberObjAct <= numberObjExp).isTrue();
        assertThat(numberObjAct <= numberObjExp).isEqualTo(true);
        assertThat(numberObjAct <= numberObjExp).isNotEqualTo(false);
        assertThat(numberObjAct <= 1).isTrue();
        assertThat(1 >= numberObjAct).isTrue();
        assertThat(numberObjAct <= numberObjExp).isFalse();
        assertThat(numberObjAct <= numberObjExp).isEqualTo(false);
        assertThat(numberObjAct <= numberObjExp).isNotEqualTo(true);
        assertThat(numberObjAct <= 1).isFalse();
        assertThat(1 >= numberObjAct).isFalse();

        assertThat(numberObjAct.equals(numberObjExp)).isTrue();
        assertThat(numberObjAct.equals(numberObjExp)).isEqualTo(true);
        assertThat(numberObjAct.equals(numberObjExp)).isNotEqualTo(false);
        assertThat(numberObjAct.equals(numberObjExp)).isFalse();
        assertThat(numberObjAct.equals(numberObjExp)).isEqualTo(false);
        assertThat(numberObjAct.equals(numberObjExp)).isNotEqualTo(true);

        assertThat(stringAct == stringExp).isTrue();
        assertThat(stringAct == stringExp).isEqualTo(true);
        assertThat(stringAct == stringExp).isNotEqualTo(false);
        assertThat(stringAct == stringExp).isFalse();
        assertThat(stringAct == stringExp).isEqualTo(false);
        assertThat(stringAct == stringExp).isNotEqualTo(true);

        assertThat(stringAct.equals(stringExp)).isTrue();
        assertThat(stringAct.equals(stringExp)).isEqualTo(true);
        assertThat(stringAct.equals(stringExp)).isNotEqualTo(false);
        assertThat(stringAct.equals(stringExp)).isFalse();
        assertThat(stringAct.equals(stringExp)).isEqualTo(false);
        assertThat(stringAct.equals(stringExp)).isNotEqualTo(true);

        assertThat(stringAct != stringExp).isTrue();
        assertThat(stringAct != stringExp).isEqualTo(true);
        assertThat(stringAct != stringExp).isNotEqualTo(false);
        assertThat(stringAct != stringExp).isFalse();
        assertThat(stringAct != stringExp).isEqualTo(false);
        assertThat(stringAct != stringExp).isNotEqualTo(true);

        assertThat(stringAct == null).isTrue();
        assertThat(stringAct == null).isEqualTo(true);
        assertThat(stringAct == null).isNotEqualTo(false);
        assertThat(stringAct == null).isFalse();
        assertThat(stringAct == null).isEqualTo(false);
        assertThat(stringAct == null).isNotEqualTo(true);

        assertThat(null == stringAct).isTrue();
        assertThat(null == stringAct).isEqualTo(true);
        assertThat(null == stringAct).isNotEqualTo(false);
        assertThat(null == stringAct).isFalse();
        assertThat(null == stringAct).isEqualTo(false);
        assertThat(null == stringAct).isNotEqualTo(true);

        assertThat(null == null).isTrue();
        assertThat(!false).isTrue();
    }
}
