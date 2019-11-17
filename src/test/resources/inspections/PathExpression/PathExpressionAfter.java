import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class PathExpression {

    private void pathExpression() {
        Path path = Paths.get("foo");
        Path otherPath = Paths.get("bar");

        assertThat(path).as("foo").isAbsolute();
        assertThat(path).isAbsolute();
        assertThat(path).isAbsolute();
        assertThat(path).as("foo").isRelative();
        assertThat(path).isRelative();
        assertThat(path).isRelative();

        assertThat(path).hasParentRaw(otherPath);
        assertThat(path.getParent()).isNotEqualTo(otherPath);
        assertThat(path).hasNoParentRaw();
        assertThat(path).hasNoParentRaw();
        assertThat(path.getParent()).isNotEqualTo(null);
        assertThat(path.getParent()).isNotNull();

        assertThat(path).as("foo").startsWithRaw(otherPath);
        assertThat(path).startsWithRaw(otherPath);
        assertThat(path).startsWithRaw(otherPath);
        assertThat(path.startsWith(otherPath)).as("foo").isEqualTo(false);
        assertThat(path.startsWith(otherPath)).isNotEqualTo(true);
        assertThat(path.startsWith(otherPath)).isFalse();

        assertThat(path.startsWith("otherPath")).as("foo").isEqualTo(true);
        assertThat(path.startsWith("otherPath")).isNotEqualTo(false);
        assertThat(path.startsWith("otherPath")).isTrue();
        assertThat(path.startsWith("otherPath")).as("foo").isEqualTo(false);
        assertThat(path.startsWith("otherPath")).isNotEqualTo(true);
        assertThat(path.startsWith("otherPath")).isFalse();

        assertThat(path).as("foo").endsWithRaw(otherPath);
        assertThat(path).endsWithRaw(otherPath);
        assertThat(path).endsWithRaw(otherPath);
        assertThat(path.endsWith(otherPath)).as("foo").isEqualTo(false);
        assertThat(path.endsWith(otherPath)).isNotEqualTo(true);
        assertThat(path.endsWith(otherPath)).isFalse();

        assertThat(path.endsWith("otherPath")).as("foo").isEqualTo(true);
        assertThat(path.endsWith("otherPath")).isNotEqualTo(false);
        assertThat(path.endsWith("otherPath")).isTrue();
        assertThat(path.endsWith("otherPath")).as("foo").isEqualTo(false);
        assertThat(path.endsWith("otherPath")).isNotEqualTo(true);
        assertThat(path.endsWith("otherPath")).isFalse();

        assertThat(Files.isReadable(path)).as("foo").isEqualTo(true);
        assertThat(Files.isReadable(path)).isNotEqualTo(false);
        assertThat(Files.isReadable(path)).isTrue();
        assertThat(Files.isReadable(path)).as("foo").isEqualTo(false);
        assertThat(Files.isReadable(path)).isNotEqualTo(true);
        assertThat(Files.isReadable(path)).isFalse();

        assertThat(Files.isWritable(path)).as("foo").isEqualTo(true);
        assertThat(Files.isWritable(path)).isNotEqualTo(false);
        assertThat(Files.isWritable(path)).isTrue();
        assertThat(Files.isWritable(path)).as("foo").isEqualTo(false);
        assertThat(Files.isWritable(path)).isNotEqualTo(true);
        assertThat(Files.isWritable(path)).isFalse();

        assertThat(Files.isExecutable(path)).as("foo").isEqualTo(true);
        assertThat(Files.isExecutable(path)).isNotEqualTo(false);
        assertThat(Files.isExecutable(path)).isTrue();
        assertThat(Files.isExecutable(path)).as("foo").isEqualTo(false);
        assertThat(Files.isExecutable(path)).isNotEqualTo(true);
        assertThat(Files.isExecutable(path)).isFalse();

        assertThat(Files.isDirectory(path)).as("foo").isEqualTo(true);
        assertThat(Files.isDirectory(path)).isNotEqualTo(false);
        assertThat(Files.isDirectory(path)).isTrue();
        assertThat(Files.isDirectory(path)).as("foo").isEqualTo(false);
        assertThat(Files.isDirectory(path)).isNotEqualTo(true);
        assertThat(Files.isDirectory(path)).isFalse();

        assertThat(Files.isRegularFile(path)).as("foo").isEqualTo(true);
        assertThat(Files.isRegularFile(path)).isNotEqualTo(false);
        assertThat(Files.isRegularFile(path)).isTrue();
        assertThat(Files.isRegularFile(path)).as("foo").isEqualTo(false);
        assertThat(Files.isRegularFile(path)).isNotEqualTo(true);
        assertThat(Files.isRegularFile(path)).isFalse();

        assertThat(Files.isSymbolicLink(path)).as("foo").isEqualTo(true);
        assertThat(Files.isSymbolicLink(path)).isNotEqualTo(false);
        assertThat(Files.isSymbolicLink(path)).isTrue();
        assertThat(Files.isSymbolicLink(path)).as("foo").isEqualTo(false);
        assertThat(Files.isSymbolicLink(path)).isNotEqualTo(true);
        assertThat(Files.isSymbolicLink(path)).isFalse();

        assertThat(Files.exists(path)).as("foo").isEqualTo(true);
        assertThat(Files.exists(path)).isNotEqualTo(false);
        assertThat(Files.exists(path)).isTrue();
        assertThat(Files.exists(path)).as("foo").isEqualTo(false);
        assertThat(Files.exists(path)).isNotEqualTo(true);
        assertThat(Files.exists(path)).isFalse();

        assertThat(Files.notExists(path)).as("foo").isEqualTo(true);
        assertThat(Files.notExists(path)).isNotEqualTo(false);
        assertThat(Files.notExists(path)).isTrue();
        assertThat(Files.notExists(path)).as("foo").isEqualTo(false);
        assertThat(Files.notExists(path)).isNotEqualTo(true);
        assertThat(Files.notExists(path)).isFalse();

        assertThat(Files.list(path)).isEmpty();
        assertThat(Files.list(path)).isNotEmpty();

        assertThat(Files.readAllBytes(path)).isEqualTo(new byte[1]);
        assertThat(Files.readAllLines(path)).containsExactly("foo");
        assertThat(Files.lines(path)).containsExactly("foo");

        assertThat(path.getName()).endsWith(".foo"); // could be turned into .hasExtension("foo"), but not always.

        assertThat(path.getName()).as("foo").isEqualTo("foo").as("bar").isEqualTo("bar");

        org.junit.Assert.assertThat(path, null);
        fail("oh no!");
    }
}
