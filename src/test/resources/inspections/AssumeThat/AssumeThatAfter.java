import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

public class AssumeThat {

    @Test
    public void junit4_return_very_early() {
        assumeThat(new Random().nextBoolean()).isFalse();
        System.out.println("sweet!"); // single else statement
        String foobar = System.getProperty("foobar");
        assertThat(foobar).isNotEmpty();
    }

    @Test
    public void junit4_return_in_else_branch_with_declaration_in_code_block_and_lots_of_comments() {
        // primary comments
        assumeThat(new Random().nextBoolean()).isTrue();
        {
            // Block start comment will be retained
            String anotherString = "narf"; // This one is likely to be kept
            assertThat(foobar).isNotEmpty();
            assertThat(foobar).isEqualTo(anotherString);
            // Block end comment is also going to be retained
        } /* weird places */
        // well, well, let's do some checks
        String foobar = System.getProperty("foobar");
        assertThat(foobar).isNotEmpty();
    }

    @org.junit.jupiter.api.Test
    public void junit5_return_after_call_without_else_branch() {
        String foobar = System.getProperty("car_manufacturer");
        assumeThat(foobar.equals("Volkswagen")).isFalse();
        assertThat(foobar).isNotEmpty();
    }

    @org.junit.jupiter.api.Test
    public void junit5_return_with_empty_else_block() {
        String foobar = System.getProperty("car_manufacturer");
        assumeThat(foobar.equals("Volkswagen")).isFalse();
        assertThat(foobar).isNotEmpty();
    }

    @org.junit.jupiter.api.TestTemplate
    public void junit5_return_inside_recursion_with_comments() {
        String foobar = System.getProperty("car_manufacturer");
        if (foobar != null) {
            assumeThat(!foobar.equals("Volkswagen")).isTrue();
            // I doubted this comment will be retained -- but surprise!
            assertThat(foobar).isNotEmpty();
            // we might keep this one alright.
            foobar = "narf";
            // And the final one? Again what learned.
        }
        assertThat(foobar).startsWith("another ecology rapist");
        if(foobar.length() == 0)
        {
            return;
        }
        assertThat(foobar).endsWith("!!!");
    }

    @Test
    public void junit4_return_after_assertion() {
        String foobar = System.getProperty("car_manufacturer");
        assertThat(foobar).isNotNull();
        if (!foobar.equals("Volkswagen")) {
            assertThat(foobar).isNotEmpty();
        } else {
            return;
        }
        assertThat(foobar).startsWith("another ecology rapist");
    }

    @Test
    public void junit4_return_after_assertions_in_subroutine() {
        String foobar = System.getProperty("car_manufacturer");
        assertStuff(foobar);
        if (!foobar.equals("Volkswagen")) {
            assertThat(foobar).isNotEmpty();
        } else {
            return;
        }
        assertThat(foobar).startsWith("another ecology rapist");
    }

    @Test
    public void junit4_return_after_assertions_in_deep_subroutine() {
        String foobar = System.getProperty("car_manufacturer");
        firstMethod();
        if (!foobar.equals("Volkswagen")) {
            assertThat(foobar).isNotEmpty();
        } else {
            return;
        }
        assertThat(foobar).startsWith("another ecology rapist");
    }

    @Test
    public void junit4_return_after_assertions_in_deep_subroutine_with_recursion() {
        String foobar = System.getProperty("car_manufacturer");
        firstMethodWithPotentialRecursion();
        if (!foobar.equals("Volkswagen")) {
            assertThat(foobar).isNotEmpty();
        } else {
            return;
        }
        assertThat(foobar).startsWith("another ecology rapist");
    }

    @Test
    public void junit4_no_harm_with_infinite_recursion() {
        String foobar = System.getProperty("car_manufacturer");
        infiniteRecursion();
        if (!foobar.equals("Volkswagen")) {
            assertThat(foobar).isNotEmpty();
        } else {
            return;
        }
        assertThat(foobar).startsWith("another ecology rapist");
    }

    private void assertStuff(String foobar) {
        if (new Random().nextBoolean()) {
            assertThat(foobar).isNotEmpty();
        } else {
            assertThat(foobar).isNull();
        }
    }

    private void firstMethod() {
        if (new Random().nextBoolean()) {
            assertStuff("narf");
        }
    }

    private void firstMethodWithPotentialRecursion() {
        if (new Random().nextBoolean()) {
            assertStuff("narf");
        } else {
            infiniteRecursion();
        }
    }

    private void infiniteRecursion() {
        secondaryRecursion();
        secondaryRecursion();
    }

    private void secondaryRecursion() {
        infiniteRecursion();
        infiniteRecursion();
    }
}
