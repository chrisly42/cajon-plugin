import org.assertj.core.extractor.Extractors;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindReference1 {

    private void findReferences() {
        Contact contact = new Contact();

        assertThat(contact).extracting("name<caret>").isEqualTo("foo");
    }
}