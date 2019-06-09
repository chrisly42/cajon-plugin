package de.platon42.intellij.plugins.cajon.references

import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture
import de.platon42.intellij.jupiter.MyFixture
import de.platon42.intellij.jupiter.TestDataSubPath
import de.platon42.intellij.plugins.cajon.AbstractCajonTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


@TestDataSubPath("references")
internal class ExtractorReferenceContributorTest : AbstractCajonTest() {

    @Test
    internal fun extractor_is_able_to_find_reference_for_field_extracting(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference1.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).isEqualTo("private String name;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_for_first_part_of_a_path(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference2.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).isEqualTo("protected Address address;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_for_second_part_of_a_path_and_both_getter_and_field(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference3.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("private String street;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_on_a_bare_method_call(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference4.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("public Boolean getREALLYnoMAILINGS()")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_with_only_Getter_on_second_part(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference5.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("public boolean isNoMailings()")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_using_byName_extractor(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference6.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).isEqualTo("private String name;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_using_resultOf_extractor(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference7.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("public String getStreetName()")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_for_field_extraction_on_list(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference8.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).isEqualTo("private String name;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_for_field_flat_extraction_of_path_on_list(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference9.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("private String street;")
    }

    @Test
    internal fun extractor_is_able_to_find_reference_for_extraction_on_result_of_method(@MyFixture myFixture: JavaCodeInsightTestFixture) {
        myFixture.configureByFiles("FindReference10.java", "Address.java", "Contact.java")
        assertThat(myFixture.elementAtCaret.text).startsWith("public String getStreetName()")
    }
}