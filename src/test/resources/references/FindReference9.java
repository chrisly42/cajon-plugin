import org.assertj.core.extractor.Extractors;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindReference9 {

    private void findReferences() {
        List<Contact> contactList = Collections.emptyList();

        assertThat(contactList).flatExtracting("age", "address.street<caret>", "streetName").containsExactly(1, "Elmst. 42");
    }
}