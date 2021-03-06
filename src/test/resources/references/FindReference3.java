import org.assertj.core.extractor.Extractors;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FindReference3 {

    private void findReferences() {
        Contact contact = new Contact();

        assertThat(contact).extracting("address.street<caret>", "address.REALLYnoMAILINGS").containsExactly(1, "Elmst. 42");
    }
}