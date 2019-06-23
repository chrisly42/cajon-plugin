import org.assertj.core.extractor.Extractors;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindReference10 {

    private void findReferences() {
        List<Contact> contactList = Collections.emptyList();

        assertThat(contactList).extracting("narf<caret>").isEqualTo("foo");
    }
}