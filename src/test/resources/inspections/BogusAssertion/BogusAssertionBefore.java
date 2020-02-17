import java.io.File;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class BogusAssertions {

    private void bogusAssertions() {
        boolean[] boolarray = new boolean[10];
        byte[] bytearray = new byte[10];
        short[] shortarray = new short[10];
        int[] intarray = new int[10];
        long[] longarray = new long[10];
        float[] floatarray = new float[10];
        double[] doublearray = new double[10];
        char[] chararray = new char[10];
        Object[] objarray = new Object[10];
        String string = "foo";
        List<String> list = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        String bar = "bar";

        assertThat(boolarray).isEqualTo(boolarray);
        assertThat(boolarray).isSameAs(boolarray);
        assertThat(boolarray).hasSameClassAs(boolarray);
        assertThat(boolarray).hasSameHashCodeAs(boolarray);
        assertThat(boolarray).hasSameSizeAs(boolarray);
        assertThat(boolarray).contains(boolarray);
        assertThat(boolarray).containsAnyOf(boolarray);
        assertThat(boolarray).containsExactly(boolarray);
        assertThat(boolarray).containsExactlyInAnyOrder(boolarray);
        assertThat(boolarray).containsOnly(boolarray);
        assertThat(boolarray).containsSequence(boolarray);
        assertThat(boolarray).containsSubsequence(boolarray);
        assertThat(boolarray).startsWith(boolarray);
        assertThat(boolarray).endsWith(boolarray);

        assertThat(bytearray).isEqualTo(bytearray);
        assertThat(bytearray).isSameAs(bytearray);
        assertThat(bytearray).hasSameClassAs(bytearray);
        assertThat(bytearray).hasSameHashCodeAs(bytearray);
        assertThat(bytearray).hasSameSizeAs(bytearray);
        assertThat(bytearray).contains(bytearray);
        assertThat(bytearray).containsAnyOf(bytearray);
        assertThat(bytearray).containsExactly(bytearray);
        assertThat(bytearray).containsExactlyInAnyOrder(bytearray);
        assertThat(bytearray).containsOnly(bytearray);
        assertThat(bytearray).containsSequence(bytearray);
        assertThat(bytearray).containsSubsequence(bytearray);
        assertThat(bytearray).startsWith(bytearray);
        assertThat(bytearray).endsWith(bytearray);

        assertThat(shortarray).isEqualTo(shortarray);
        assertThat(shortarray).isSameAs(shortarray);
        assertThat(shortarray).hasSameClassAs(shortarray);
        assertThat(shortarray).hasSameHashCodeAs(shortarray);
        assertThat(shortarray).hasSameSizeAs(shortarray);
        assertThat(shortarray).contains(shortarray);
        assertThat(shortarray).containsAnyOf(shortarray);
        assertThat(shortarray).containsExactly(shortarray);
        assertThat(shortarray).containsExactlyInAnyOrder(shortarray);
        assertThat(shortarray).containsOnly(shortarray);
        assertThat(shortarray).containsSequence(shortarray);
        assertThat(shortarray).containsSubsequence(shortarray);
        assertThat(shortarray).startsWith(shortarray);
        assertThat(shortarray).endsWith(shortarray);

        assertThat(intarray).isEqualTo(intarray);
        assertThat(intarray).isSameAs(intarray);
        assertThat(intarray).hasSameClassAs(intarray);
        assertThat(intarray).hasSameHashCodeAs(intarray);
        assertThat(intarray).hasSameSizeAs(intarray);
        assertThat(intarray).contains(intarray);
        assertThat(intarray).containsAnyOf(intarray);
        assertThat(intarray).containsExactly(intarray);
        assertThat(intarray).containsExactlyInAnyOrder(intarray);
        assertThat(intarray).containsOnly(intarray);
        assertThat(intarray).containsSequence(intarray);
        assertThat(intarray).containsSubsequence(intarray);
        assertThat(intarray).startsWith(intarray);
        assertThat(intarray).endsWith(intarray);

        assertThat(longarray).isEqualTo(longarray);
        assertThat(longarray).isSameAs(longarray);
        assertThat(longarray).hasSameClassAs(longarray);
        assertThat(longarray).hasSameHashCodeAs(longarray);
        assertThat(longarray).hasSameSizeAs(longarray);
        assertThat(longarray).contains(longarray);
        assertThat(longarray).containsAnyOf(longarray);
        assertThat(longarray).containsExactly(longarray);
        assertThat(longarray).containsExactlyInAnyOrder(longarray);
        assertThat(longarray).containsOnly(longarray);
        assertThat(longarray).containsSequence(longarray);
        assertThat(longarray).containsSubsequence(longarray);
        assertThat(longarray).startsWith(longarray);
        assertThat(longarray).endsWith(longarray);

        assertThat(floatarray).isEqualTo(floatarray);
        assertThat(floatarray).isSameAs(floatarray);
        assertThat(floatarray).hasSameClassAs(floatarray);
        assertThat(floatarray).hasSameHashCodeAs(floatarray);
        assertThat(floatarray).hasSameSizeAs(floatarray);
        assertThat(floatarray).contains(floatarray);
        assertThat(floatarray).containsAnyOf(floatarray);
        assertThat(floatarray).containsExactly(floatarray);
        assertThat(floatarray).containsExactlyInAnyOrder(floatarray);
        assertThat(floatarray).containsOnly(floatarray);
        assertThat(floatarray).containsSequence(floatarray);
        assertThat(floatarray).containsSubsequence(floatarray);
        assertThat(floatarray).startsWith(floatarray);
        assertThat(floatarray).endsWith(floatarray);

        assertThat(doublearray).isEqualTo(doublearray);
        assertThat(doublearray).isSameAs(doublearray);
        assertThat(doublearray).hasSameClassAs(doublearray);
        assertThat(doublearray).hasSameHashCodeAs(doublearray);
        assertThat(doublearray).hasSameSizeAs(doublearray);
        assertThat(doublearray).contains(doublearray);
        assertThat(doublearray).containsAnyOf(doublearray);
        assertThat(doublearray).containsExactly(doublearray);
        assertThat(doublearray).containsExactlyInAnyOrder(doublearray);
        assertThat(doublearray).containsOnly(doublearray);
        assertThat(doublearray).containsSequence(doublearray);
        assertThat(doublearray).containsSubsequence(doublearray);
        assertThat(doublearray).startsWith(doublearray);
        assertThat(doublearray).endsWith(doublearray);

        assertThat(chararray).isEqualTo(chararray);
        assertThat(chararray).isSameAs(chararray);
        assertThat(chararray).hasSameClassAs(chararray);
        assertThat(chararray).hasSameHashCodeAs(chararray);
        assertThat(chararray).hasSameSizeAs(chararray);
        assertThat(chararray).contains(chararray);
        assertThat(chararray).containsAnyOf(chararray);
        assertThat(chararray).containsExactly(chararray);
        assertThat(chararray).containsExactlyInAnyOrder(chararray);
        assertThat(chararray).containsOnly(chararray);
        assertThat(chararray).containsSequence(chararray);
        assertThat(chararray).containsSubsequence(chararray);
        assertThat(chararray).startsWith(chararray);
        assertThat(chararray).endsWith(chararray);

        assertThat(objarray).isEqualTo(objarray);
        assertThat(objarray).isSameAs(objarray);
        assertThat(objarray).hasSameClassAs(objarray);
        assertThat(objarray).hasSameHashCodeAs(objarray);
        assertThat(objarray).hasSameSizeAs(objarray);
        assertThat(objarray).contains(objarray);
        assertThat(objarray).containsAnyOf(objarray);
        assertThat(objarray).containsExactly(objarray);
        assertThat(objarray).containsExactlyInAnyOrder(objarray);
        assertThat(objarray).containsOnly(objarray);
        assertThat(objarray).containsSequence(objarray);
        assertThat(objarray).containsSubsequence(objarray);
        assertThat(objarray).startsWith(objarray);
        assertThat(objarray).endsWith(objarray);

        assertThat(string).as("foo").isEqualTo(string);
        assertThat(string).as("foo").isSameAs(string);
        assertThat(string).as("foo").hasSameClassAs(string);
        assertThat(string).as("foo").hasSameHashCodeAs(string);
        assertThat(string).as("foo").hasSameSizeAs(string);
        assertThat(string).as("foo").isEqualToIgnoringCase(string);
        assertThat(string).as("foo").containsSequence(string);
        assertThat(string).as("foo").containsSubsequence(string);
        assertThat(string).as("foo").startsWith(string);
        assertThat(string).as("foo").endsWith(string);

        assertThat(list).as("foo").isEqualTo(list);
        assertThat(list).as("foo").isSameAs(list);
        assertThat(list).as("foo").hasSameClassAs(list);
        assertThat(list).as("foo").hasSameHashCodeAs(list);
        assertThat(list).as("foo").hasSameSizeAs(list);
        assertThat(list).as("foo").containsAll(list);
        assertThat(list).as("foo").containsAnyElementsOf(list);
        assertThat(list).as("foo").containsOnlyElementsOf(list);
        assertThat(list).as("foo").containsExactlyElementsOf(list);
        assertThat(list).as("foo").hasSameElementsAs(list);
        assertThat(list).as("foo").containsSequence(list);
        assertThat(list).as("foo").containsSubsequence(list);

        assertThat(map).as("foo").isEqualTo(map);
        assertThat(map).as("foo").isSameAs(map);
        assertThat(map).as("foo").hasSameClassAs(map);
        assertThat(map).as("foo").hasSameHashCodeAs(map);
        assertThat(map).as("foo").hasSameSizeAs(map);
        assertThat(map).as("foo").containsAllEntriesOf(map);
        assertThat(map).as("foo").containsExactlyEntriesOf(map);
        assertThat(map).as("foo").containsExactlyInAnyOrderEntriesOf(map);

        assertThat(bar).isEqualTo(string);

        assertThat(new Random().nextBoolean()).isEqualTo(new Random().nextBoolean());
        assertThat(generateString()).isEqualTo(generateString());

        int number = 4;
        assertThat(number++).isEqualTo(number++);
        assertThat(number++).isEqualTo(number++);

        org.junit.Assert.assertThat(list, null);
        fail("oh no!");
    }

    private void test_equals() {
        assertThat("foo").isEqualTo("foo");
        assertThat(new File("foo")).isEqualTo(new File("foo"));
    }

    private void test_HasHCode() {
        assertThat("foo").hasSameHashCodeAs("foo");
    }

    private String generateString()
    {
        return "foo";
    }
}
