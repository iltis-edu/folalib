package de.tudortmund.cs.iltis.folalib.languages;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReader;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class HeuristicEquivalenceCheckTest {

    Alphabet<IndexedSymbol> alphabet;
    Alphabet<IndexedSymbol> alphabetAB;
    Alphabet<IndexedSymbol> alphabetABC;

    RegularLanguage<IndexedSymbol> languageA; // Equivalent to B
    RegularLanguage<IndexedSymbol> languageB; // Equivalent to A
    RegularLanguage<IndexedSymbol> languageC; // Different language

    IndexedSymbol a = new IndexedSymbol("a");
    IndexedSymbol b = new IndexedSymbol("b");
    IndexedSymbol c = new IndexedSymbol("c");
    IndexedSymbol d = new IndexedSymbol("d");

    RegularExpressionReader reader;
    RegularExpressionReader readerAB;
    RegularExpressionReader readerABC;

    @Before
    public void init() {
        alphabet = new Alphabet<>(a, b, c, d);
        alphabetAB = new Alphabet<>(a, b);
        alphabetABC = new Alphabet<>(a, b, c);

        reader =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(alphabet));
        readerAB =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(alphabetAB));
        readerABC =
                new RegularExpressionReader(
                        RegularExpressionReaderProperties.createDefault(alphabetABC));

        languageA = new RegularLanguage<>(reader.read("((a*c*)*+(bbb*))*"));
        languageB = new RegularLanguage<>(reader.read("((a+c)*+(bbb*)*)*"));

        // Check if actually equivalent (this is possible with regular languages)
        assertTrue(languageA.isEqualTo(languageB));

        languageC = new RegularLanguage<>(reader.read("(abcd)*"));
    }

    @Test
    public void testGivenWords() {
        Collection<Word<IndexedSymbol>> words =
                List.of(
                        new Word<>(a, b, c, d),
                        new Word<>(a, b, c, b),
                        new Word<>(c, b, b, a),
                        new Word<>(b, b),
                        new Word<>(a, c, b, c, d),
                        new Word<>(d, d),
                        new Word<>(a, b, c));

        assertTrue(
                HeuristicEquivalenceCheck.testGivenWords(languageA, languageB, words)
                        instanceof HeuristicEquivalenceResult.EquivalencePossible);

        HeuristicEquivalenceResult<IndexedSymbol> result =
                HeuristicEquivalenceCheck.testGivenWords(languageA, languageC, words);
        assertTrue(result instanceof HeuristicEquivalenceResult.EquivalenceDisproved);
        assertEquals(
                new Word<>(a, b, c, d),
                ((HeuristicEquivalenceResult.EquivalenceDisproved<IndexedSymbol>) result)
                        .getCounterExample());
    }

    @Test
    public void testAllWords() {
        assertTrue(
                HeuristicEquivalenceCheck.testAllWordsToLength(languageA, languageB, 6)
                        instanceof HeuristicEquivalenceResult.EquivalencePossible);

        HeuristicEquivalenceResult<IndexedSymbol> result =
                HeuristicEquivalenceCheck.testAllWordsToLength(languageA, languageC, 6);
        assertTrue(result instanceof HeuristicEquivalenceResult.EquivalenceDisproved);
    }

    @Test
    public void testEqualityDisprovedIgnoringAlphabets() {
        RegularLanguage<IndexedSymbol> languageA =
                new RegularLanguage<>(readerABC.read("a + ab + abc"));
        RegularLanguage<IndexedSymbol> languageB = new RegularLanguage<>(readerAB.read("a + ab"));

        HeuristicEquivalenceResult<IndexedSymbol> result =
                HeuristicEquivalenceCheck.testAllWordsToLength(languageA, languageB, 4);
        assertTrue(result instanceof HeuristicEquivalenceResult.EquivalenceDisproved);
    }

    @Test
    public void testEqualityPossibleIgnoringAlphabets() {
        RegularLanguage<IndexedSymbol> languageA = new RegularLanguage<>(readerABC.read("a + ab"));
        RegularLanguage<IndexedSymbol> languageB = new RegularLanguage<>(readerAB.read("a + ab"));

        HeuristicEquivalenceResult<IndexedSymbol> result =
                HeuristicEquivalenceCheck.testAllWordsToLength(languageA, languageB, 4);
        assertTrue(
                result instanceof HeuristicEquivalenceResult.EquivalencePossibleIgnoringAlphabets);
    }
}
