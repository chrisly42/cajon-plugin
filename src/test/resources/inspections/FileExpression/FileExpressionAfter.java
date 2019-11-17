import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class FileExpression {

    private void fileExpression() {
        File file = new File("foo");

        assertThat(file).as("foo").canRead();
        assertThat(file).canRead();
        assertThat(file).canRead();
        assertThat(file.canRead()).as("foo").isEqualTo(false);
        assertThat(file.canRead()).isNotEqualTo(true);
        assertThat(file.canRead()).isFalse();

        assertThat(file).as("foo").canWrite();
        assertThat(file).canWrite();
        assertThat(file).canWrite();
        assertThat(file.canWrite()).as("foo").isEqualTo(false);
        assertThat(file.canWrite()).isNotEqualTo(true);
        assertThat(file.canWrite()).isFalse();

        assertThat(file).as("foo").exists();
        assertThat(file).exists();
        assertThat(file).exists();
        assertThat(file).as("foo").doesNotExist();
        assertThat(file).doesNotExist();
        assertThat(file).doesNotExist();

        assertThat(file).as("foo").isAbsolute();
        assertThat(file).isAbsolute();
        assertThat(file).isAbsolute();
        assertThat(file).as("foo").isRelative();
        assertThat(file).isRelative();
        assertThat(file).isRelative();

        assertThat(file).as("foo").isDirectory();
        assertThat(file).isDirectory();
        assertThat(file).isDirectory();
        assertThat(file.isDirectory()).as("foo").isEqualTo(false);
        assertThat(file.isDirectory()).isNotEqualTo(true);
        assertThat(file.isDirectory()).isFalse();

        assertThat(file).as("foo").isFile();
        assertThat(file).isFile();
        assertThat(file).isFile();
        assertThat(file.isFile()).as("foo").isEqualTo(false);
        assertThat(file.isFile()).isNotEqualTo(true);
        assertThat(file.isFile()).isFalse();

        assertThat(file).hasName("foo");
        assertThat(file.getName()).isNotEqualTo("foo");
        assertThat(file).hasName(null);
        assertThat(file.getName()).isNull();
        assertThat(file.getName()).isNotEqualTo(null);
        assertThat(file.getName()).isNotNull();
        assertThat(file.getName()).isEmpty();
        assertThat(file.getName()).isNotEmpty();

        assertThat(file).hasParent("foo");
        assertThat(file.getParent()).isNotEqualTo("foo");
        assertThat(file).hasNoParent();
        assertThat(file).hasNoParent();
        assertThat(file.getParent()).isNotEqualTo(null);
        assertThat(file.getParent()).isNotNull();
        assertThat(file.getParent()).isEmpty();
        assertThat(file.getParent()).isNotEmpty();

        assertThat(file).hasParent(new File("foo"));
        assertThat(file.getParentFile()).isNotEqualTo(new File("foo"));
        assertThat(file).hasNoParent();
        assertThat(file).hasNoParent();
        assertThat(file.getParentFile()).isNotEqualTo(null);
        assertThat(file.getParentFile()).isNotNull();

        assertThat(file.listFiles()).isNull();
        assertThat(file.listFiles()).isNullOrEmpty();
        assertThat(file).isEmptyDirectory();
        assertThat(file).isNotEmptyDirectory();

        assertThat(file.listFiles(f -> f.canExecute())).isNull();
        assertThat(file.listFiles(f -> f.canExecute())).isNullOrEmpty();
        assertThat(file.listFiles(f -> f.canExecute())).isEmpty();
        assertThat(file.listFiles(f -> f.canExecute())).isNotEmpty();

        assertThat(file.list()).isNull();
        assertThat(file.list()).isNullOrEmpty();
        assertThat(file).isEmptyDirectory();
        assertThat(file).isNotEmptyDirectory();

        assertThat(file.getName()).endsWith(".foo"); // could be turned into .hasExtension("foo"), but not always.

        assertThat(file).as("foo").hasName("foo").as("bar").hasName("bar");

        org.junit.Assert.assertThat(file, null);
        fail("oh no!");
    }
}
