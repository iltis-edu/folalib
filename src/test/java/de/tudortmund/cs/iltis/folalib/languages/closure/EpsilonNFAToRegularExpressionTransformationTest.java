package de.tudortmund.cs.iltis.folalib.languages.closure;

import static org.junit.Assert.assertEquals;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import de.tudortmund.cs.iltis.folalib.automata.finite.transformation.EpsilonNFAToRegularExpressionTransformation;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import org.junit.Test;

public class EpsilonNFAToRegularExpressionTransformationTest {

    private final IndexedSymbol a = new IndexedSymbol("a");
    private final IndexedSymbol b = new IndexedSymbol("b");
    private final Alphabet<IndexedSymbol> alphabet = new Alphabet<>(a, b);

    @Test
    public void testSimpleNFA() {
        NFA<Integer, IndexedSymbol> nfa =
                new NFABuilder<Integer, IndexedSymbol>(alphabet)
                        .withInitial(1)
                        .withAccepting(2)
                        .withTransition(1, a, 2)
                        .withTransition(2, b, 1)
                        .withEpsilonTransition(1, 2)
                        .build()
                        .unwrap();

        EpsilonNFAToRegularExpressionTransformation<IndexedSymbol> transformation =
                new EpsilonNFAToRegularExpressionTransformation<>();
        RegularExpression<IndexedSymbol> regex = transformation.apply(nfa);
        assertEquals(
                RegularExpression.fromString(
                        "(a+eps)(b(a+eps))*",
                        RegularExpressionReaderProperties.createDefault(alphabet)),
                regex);
    }

    @Test
    public void testAnotherSimpleNFA() {
        NFA<Integer, IndexedSymbol> nfa =
                new NFABuilder<Integer, IndexedSymbol>(alphabet)
                        .withInitial(1)
                        .withAccepting(2)
                        .withTransition(1, a, 1)
                        .withTransition(1, b, 2)
                        .withTransition(2, a, 2)
                        .build()
                        .unwrap();

        EpsilonNFAToRegularExpressionTransformation<IndexedSymbol> transformation =
                new EpsilonNFAToRegularExpressionTransformation<>();
        RegularExpression<IndexedSymbol> regex = transformation.apply(nfa);
        assertEquals(
                RegularExpression.fromString(
                        "a*ba*", RegularExpressionReaderProperties.createDefault(alphabet)),
                regex);
    }

    @Test
    public void testNFAOfEmptyLanguage() {
        NFA<Integer, IndexedSymbol> nfa =
                new NFABuilder<Integer, IndexedSymbol>(alphabet)
                        .withInitial(1)
                        .withAccepting(2) // unreachable => language recognised is empty language
                        .withTransition(1, a, 1)
                        .withTransition(1, b, 1)
                        .build()
                        .unwrap();

        EpsilonNFAToRegularExpressionTransformation<IndexedSymbol> transformation =
                new EpsilonNFAToRegularExpressionTransformation<>();
        RegularExpression<IndexedSymbol> regex = transformation.apply(nfa);
        assertEquals(
                RegularExpression.fromString(
                        "∅", RegularExpressionReaderProperties.createDefault(alphabet)),
                regex);
    }

    @Test
    public void testNFAOfEmptyWordOnly() {
        NFA<Integer, IndexedSymbol> nfa =
                new NFABuilder<Integer, IndexedSymbol>(alphabet)
                        .withInitial(1)
                        .withAccepting(1)
                        .withStates(2)
                        .withTransition(1, a, 2)
                        .withTransition(1, b, 2)
                        .build()
                        .unwrap();

        EpsilonNFAToRegularExpressionTransformation<IndexedSymbol> transformation =
                new EpsilonNFAToRegularExpressionTransformation<>();
        RegularExpression<IndexedSymbol> regex = transformation.apply(nfa);
        assertEquals(
                RegularExpression.fromString(
                        "ε", RegularExpressionReaderProperties.createDefault(alphabet)),
                regex);
    }
}
