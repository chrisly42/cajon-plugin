import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class JUnit5AssertToAssertJ {

    private void jUnit5AssertToAssertJ() {
        String foo = "foo";
        String bar = "bar";
        int someInt = 1;
        double someDouble = 1.0;
        float someFloat = 1.0f;

        assertTrue(foo == "foo");
        assertTrue(foo == "foo", "oh no!");
        assertFalse(foo == "bar");
        assertFalse(foo == "bar", "boom!");

        assertNull(foo);
        assertNull(foo, "oh no!");
        assertNotNull(foo);
        assertNotNull(foo, "oh no!");

        assertEquals(bar, foo);
        assertEquals(bar, foo, "equals");
        assertNotEquals(bar, foo);
        assertNotEquals(bar, foo, "equals");

        assertSame(bar, foo);
        assertSame(bar, foo, "same");
        assertNotSame(bar, foo);
        assertNotSame(bar, foo, "same");

        assertEquals(1.0, 2.0);
        assertEquals(1.0, 2.0, 0.1);
        assertEquals(1.0, 2.0, "equals");
        assertEquals(1.0, 2.0, 0.1, "equals");
        assertEquals(1.0f, 2.0f);
        assertEquals(1.0f, 2.0f, 0.1f);
        assertEquals(1.0f, 2.0f, "equals");
        assertEquals(1.0f, 2.0f, 0.1f, "equals");

        assertNotEquals(1.0, 2.0);
        assertNotEquals(1.0, 2.0, 0.1);
        assertNotEquals(1.0, 2.0, "equals");
        assertNotEquals(1.0, 2.0, 0.1, "equals");
        assertNotEquals(1.0f, 2.0f);
        assertNotEquals(1.0f, 2.0f, 0.1f);
        assertNotEquals(1.0f, 2.0f, "equals");
        assertNotEquals(1.0f, 2.0f, 0.1f, "equals");

        assertArrayEquals(new int[2], new int[1]);
        assertArrayEquals(new int[2], new int[1], "array equals");

        assertArrayEquals(new double[2], new double[1], 1.0);
        assertArrayEquals(new double[2], new double[1], 1.0, "array equals");
        assertArrayEquals(new float[2], new float[1], 1.0f);
        assertArrayEquals(new float[2], new float[1], 1.0f, "array equals");

        assertEquals("bar", foo);
        assertEquals(bar, "foo", "equals");
        assertNotEquals(bar, "foo");
        assertNotEquals("bar", foo, "equals");

        assertEquals(someInt, 2);
        assertEquals(someDouble, 2.0, 0.1);
        assertEquals(1.0, someDouble, "equals");
        assertEquals(1.0, someDouble, 0.1, "equals");
        assertEquals(1.0f, someFloat);
        assertEquals(someFloat, 2.0f, 0.1f);

        fail();
        fail("oh no!")
    }

    private void jUnit5AssumeToAssertJ() {
        String foo = "foo";
        String bar = "bar";
        assumeTrue(foo == "foo");
        assumeTrue(foo == "foo", "oh no!");
        assumeFalse(foo == "bar");
        assumeFalse(foo == "bar", "boom!");
    }
}
