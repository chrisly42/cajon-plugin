import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FileExpression {

    private void fileExpression() {
        File file = new File("foo");

        assertThat(file.canRead()).as("foo").isEqualTo(true);
        assertThat(file.canRead()).isNotEqualTo(false);
        assertThat(file.canRead()).isTrue();
        assertThat(file.canRead()).as("foo").isEqualTo(false);
        assertThat(file.canRead()).isNotEqualTo(true);
        assertThat(file.canRead()).isFalse();

        assertThat(file.canWrite()).as("foo").isEqualTo(true);
        assertThat(file.canWrite()).isNotEqualTo(false);
        assertThat(file.canWrite()).isTrue();
        assertThat(file.canWrite()).as("foo").isEqualTo(false);
        assertThat(file.canWrite()).isNotEqualTo(true);
        assertThat(file.canWrite()).isFalse();

        assertThat(file.exists()).as("foo").isEqualTo(true);
        assertThat(file.exists()).isNotEqualTo(false);
        assertThat(file.exists()).isTrue();
        assertThat(file.exists()).as("foo").isEqualTo(false);
        assertThat(file.exists()).isNotEqualTo(true);
        assertThat(file.exists()).isFalse();

        assertThat(file.isAbsolute()).as("foo").isEqualTo(true);
        assertThat(file.isAbsolute()).isNotEqualTo(false);
        assertThat(file.isAbsolute()).isTrue();
        assertThat(file.isAbsolute()).as("foo").isEqualTo(false);
        assertThat(file.isAbsolute()).isNotEqualTo(true);
        assertThat(file.isAbsolute()).isFalse();

        assertThat(file.isDirectory()).as("foo").isEqualTo(true);
        assertThat(file.isDirectory()).isNotEqualTo(false);
        assertThat(file.isDirectory()).isTrue();
        assertThat(file.isDirectory()).as("foo").isEqualTo(false);
        assertThat(file.isDirectory()).isNotEqualTo(true);
        assertThat(file.isDirectory()).isFalse();

        assertThat(file.isFile()).as("foo").isEqualTo(true);
        assertThat(file.isFile()).isNotEqualTo(false);
        assertThat(file.isFile()).isTrue();
        assertThat(file.isFile()).as("foo").isEqualTo(false);
        assertThat(file.isFile()).isNotEqualTo(true);
        assertThat(file.isFile()).isFalse();

        assertThat(file.getName()).isEqualTo("foo");
        assertThat(file.getName()).isNotEqualTo("foo");
        assertThat(file.getName()).isEqualTo(null);
        assertThat(file.getName()).isNull();
        assertThat(file.getName()).isNotEqualTo(null);
        assertThat(file.getName()).isNotNull();
        assertThat(file.getName()).isEmpty();
        assertThat(file.getName()).isNotEmpty();

        assertThat(file.getParent()).isEqualTo("foo");
        assertThat(file.getParent()).isNotEqualTo("foo");
        assertThat(file.getParent()).isEqualTo(null);
        assertThat(file.getParent()).isNull();
        assertThat(file.getParent()).isNotEqualTo(null);
        assertThat(file.getParent()).isNotNull();
        assertThat(file.getParent()).isEmpty();
        assertThat(file.getParent()).isNotEmpty();

        assertThat(file.getParentFile()).isEqualTo(new File("foo"));
        assertThat(file.getParentFile()).isNotEqualTo(new File("foo"));
        assertThat(file.getParentFile()).isEqualTo(null);
        assertThat(file.getParentFile()).isNull();
        assertThat(file.getParentFile()).isNotEqualTo(null);
        assertThat(file.getParentFile()).isNotNull();

        assertThat(file.listFiles()).isNull();
        assertThat(file.listFiles()).isNullOrEmpty();
        assertThat(file.listFiles()).isEmpty();
        assertThat(file.listFiles()).isNotEmpty();

        assertThat(file.list()).isNull();
        assertThat(file.list()).isNullOrEmpty();
        assertThat(file.list()).isEmpty();
        assertThat(file.list()).isNotEmpty();

        assertThat(file.getName()).endsWith(".foo"); // could be turned into .hasExtension("foo"), but not always.

        assertThat(file.getName()).as("foo").isEqualTo("foo").as("bar").isEqualTo("bar");

        org.junit.Assert.assertThat(file, null);
        fail("oh no!");
    }
}
