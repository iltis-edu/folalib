package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.ArrayList;
import java.util.Set;
import org.junit.Test;

/** Test class for class for {@link RegularLanguage#getEquivalenceClasses()} */
public class EquivalenceClassesTest {

    @Test
    public void testTwoEquivalenceClasses() {

        Alphabet<IndexedSymbol> alphabet =
                new Alphabet<>(
                        new IndexedSymbol("a"), new IndexedSymbol("b"), new IndexedSymbol("c"));
        RegularLanguage<IndexedSymbol> language =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a(a+b+c)*+b(a+b+c)*+a*+b*",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass1 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(a+b)(a+b+c)*",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass2 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "c(a+b+c)*",
                                RegularExpressionReaderProperties.createDefault(alphabet)));

        Set<RegularLanguage<IndexedSymbol>> equivalenceClassesSet =
                language.getEquivalenceClasses();
        ArrayList<RegularLanguage<IndexedSymbol>> equivalenceClasses =
                new ArrayList<>(equivalenceClassesSet);

        RegularLanguage<IndexedSymbol> firstEquivalenceClass = equivalenceClasses.get(0);
        RegularLanguage<IndexedSymbol> secondEquivalenceClass = equivalenceClasses.get(1);

        assertLanguageEqual(testClass1, firstEquivalenceClass);
        assertLanguageEqual(testClass2, secondEquivalenceClass);

        assertLanguageNotEqual(testClass1, secondEquivalenceClass);
        assertLanguageNotEqual(testClass2, firstEquivalenceClass);
    }

    @Test
    public void testThreeEquivalenceClasses() {

        Alphabet<IndexedSymbol> alphabet =
                new Alphabet<>(new IndexedSymbol("a"), new IndexedSymbol("b"));
        RegularLanguage<IndexedSymbol> language =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(ba*)+(a*ba*)",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass1 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a*", RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass2 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a*ba*",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass3 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "a*ba*b(a+b)*",
                                RegularExpressionReaderProperties.createDefault(alphabet)));

        Set<RegularLanguage<IndexedSymbol>> equivalenceClassesSet =
                language.getEquivalenceClasses();
        ArrayList<RegularLanguage<IndexedSymbol>> equivalenceClasses =
                new ArrayList<>(equivalenceClassesSet);

        RegularLanguage<IndexedSymbol> firstEquivalenceClass = equivalenceClasses.get(0);
        RegularLanguage<IndexedSymbol> secondEquivalenceClass = equivalenceClasses.get(1);
        RegularLanguage<IndexedSymbol> thirdEquivalenceClass = equivalenceClasses.get(2);

        assertLanguageEqual(testClass1, thirdEquivalenceClass);
        assertLanguageEqual(testClass2, secondEquivalenceClass);
        assertLanguageEqual(testClass3, firstEquivalenceClass);

        assertLanguageNotEqual(testClass1, firstEquivalenceClass);
        assertLanguageNotEqual(testClass1, secondEquivalenceClass);
        assertLanguageNotEqual(testClass2, firstEquivalenceClass);
        assertLanguageNotEqual(testClass2, thirdEquivalenceClass);
        assertLanguageNotEqual(testClass3, secondEquivalenceClass);
        assertLanguageNotEqual(testClass3, thirdEquivalenceClass);
    }

    /**
     * Example taken from Buchin TI lecture slide 58 from topic "Reguläre Sprachen" (page 104 in
     * PDF)
     */
    @Test
    public void testThreeEquivalenceClasses2() {

        Alphabet<IndexedSymbol> alphabet =
                new Alphabet<>(new IndexedSymbol("0"), new IndexedSymbol("1"));
        RegularLanguage<IndexedSymbol> language =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(0+1)* 00",
                                RegularExpressionReaderProperties.createDefault(alphabet)));

        RegularLanguage<IndexedSymbol> testClass1 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(0+1)*1 + eps",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass2 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(0+1)*10 + 0",
                                RegularExpressionReaderProperties.createDefault(alphabet)));
        RegularLanguage<IndexedSymbol> testClass3 =
                new RegularLanguage<>(
                        RegularExpression.fromString(
                                "(0+1)*00",
                                RegularExpressionReaderProperties.createDefault(alphabet)));

        Set<RegularLanguage<IndexedSymbol>> equivalenceClassesSet =
                language.getEquivalenceClasses();
        ArrayList<RegularLanguage<IndexedSymbol>> equivalenceClasses =
                new ArrayList<>(equivalenceClassesSet);

        RegularLanguage<IndexedSymbol> firstEquivalenceClass = equivalenceClasses.get(0);
        RegularLanguage<IndexedSymbol> secondEquivalenceClass = equivalenceClasses.get(1);
        RegularLanguage<IndexedSymbol> thirdEquivalenceClass = equivalenceClasses.get(2);

        assertLanguageEqual(testClass3, firstEquivalenceClass);
        assertLanguageEqual(testClass2, secondEquivalenceClass);
        assertLanguageEqual(testClass1, thirdEquivalenceClass);

        assertLanguageNotEqual(testClass1, firstEquivalenceClass);
        assertLanguageNotEqual(testClass1, secondEquivalenceClass);
        assertLanguageNotEqual(testClass2, firstEquivalenceClass);
        assertLanguageNotEqual(testClass2, thirdEquivalenceClass);
        assertLanguageNotEqual(testClass3, secondEquivalenceClass);
        assertLanguageNotEqual(testClass3, thirdEquivalenceClass);
    }

    private void assertLanguageEqual(
            RegularLanguage<IndexedSymbol> firstLanguage,
            RegularLanguage<IndexedSymbol> secondLanguage) {
        assertTrue(firstLanguage.isEqualTo(secondLanguage));
    }

    private void assertLanguageNotEqual(
            RegularLanguage<IndexedSymbol> firstLanguage,
            RegularLanguage<IndexedSymbol> secondLanguage) {
        assertFalse(firstLanguage.isEqualTo(secondLanguage));
    }
}
