import static org.junit.Assert.*;
import static org.junit.Assume.*;

public class JUnitAssertToAssertJ {

    private void jUnitAssertToAssertJ() {
        String foo = "foo";
        String bar = "bar";
        int someInt = 1;
        double someDouble = 1.0;
        float someFloat = 1.0f;

        assertTrue(foo == "foo");
        assertTrue("oh no!", foo == "foo");
        assertFalse(foo == "bar");
        assertFalse("boom!", foo == "bar");

        assertNull(foo);
        assertNull("oh no!", foo);
        assertNotNull(foo);
        assertNotNull("oh no!", foo);

        assertEquals(bar, foo);
        assertEquals("equals", bar, foo);
        assertNotEquals(bar, foo);
        assertNotEquals("equals", bar, foo);

        assertSame(bar, foo);
        assertSame("same", bar, foo);
        assertNotSame(bar, foo);
        assertNotSame("same", bar, foo);

        assertEquals(1.0, 2.0);
        assertEquals(1.0, 2.0, 0.1);
        assertEquals("equals",1.0, 2.0);
        assertEquals("equals",1.0, 2.0, 0.1);
        assertEquals(1.0f, 2.0f);
        assertEquals(1.0f, 2.0f, 0.1f);
        assertEquals("equals",1.0f, 2.0f);
        assertEquals("equals",1.0f, 2.0f, 0.1f);

        assertNotEquals(1.0, 2.0);
        assertNotEquals(1.0, 2.0, 0.1);
        assertNotEquals("equals",1.0, 2.0);
        assertNotEquals("equals",1.0, 2.0, 0.1);
        assertNotEquals(1.0f, 2.0f);
        assertNotEquals(1.0f, 2.0f, 0.1f);
        assertNotEquals("equals",1.0f, 2.0f);
        assertNotEquals("equals",1.0f, 2.0f, 0.1f);

        assertArrayEquals(new int[2], new int[1]);
        assertArrayEquals("array equals", new int[2], new int[1]);

        assertArrayEquals(new double[2], new double[1], 1.0);
        assertArrayEquals("array equals", new double[2], new double[1], 1.0);
        assertArrayEquals(new float[2], new float[1], 1.0f);
        assertArrayEquals("array equals", new float[2], new float[1], 1.0f);

        assertEquals("bar", foo);
        assertEquals("equals", bar, "foo");
        assertNotEquals(bar, "foo");
        assertNotEquals("equals", "bar", foo);

        assertEquals(someInt, 2);
        assertEquals(someDouble, 2.0, 0.1);
        assertEquals("equals",1.0, someDouble);
        assertEquals("equals",1.0, someDouble, 0.1);
        assertEquals(1.0f, someFloat);
        assertEquals(someFloat, 2.0f, 0.1f);

        fail();
        fail("oh no!")
    }

    private void jUnitAssumeToAssertJ() {
        String foo = "foo";
        String bar = "bar";
        assumeTrue(foo == "foo");
        assumeTrue("oh no!", foo == "foo");
        assumeFalse(foo == "bar");
        assumeFalse("boom!", foo == "bar");

        assumeNotNull(foo);
        assumeNotNull(foo, bar);

        assumeNoException(new IllegalArgumentException());
        assumeNoException("oh no!", new IllegalArgumentException());
    }
}
