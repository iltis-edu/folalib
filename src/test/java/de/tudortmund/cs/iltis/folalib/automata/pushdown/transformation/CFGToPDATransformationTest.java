package de.tudortmund.cs.iltis.folalib.automata.pushdown.transformation;

import static org.junit.Assert.*;

import de.tudortmund.cs.iltis.folalib.automata.pushdown.*;
import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.ContextFreeGrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.transform.MaybeGenerated;
import java.util.Set;
import org.junit.Test;

public class CFGToPDATransformationTest extends Utils {

    private final Alphabet<Character> terminals =
            new Alphabet<>('a', 'b', '0', '1', '(', ')', '+', '*');
    private final Alphabet<String> nonTerminals = new Alphabet<>("A", "T", "F", "B");

    // The grammar and test case is taken from GTI lecture, slide 353
    private final ContextFreeGrammarBuilder<Character, String> builder =
            new ContextFreeGrammarBuilder<>(terminals, nonTerminals);

    private final ContextFreeGrammar<Character, String, ContextFreeProduction<Character, String>>
            grammar =
                    builder.withStartSymbol("A")
                            .withProduction("A")
                            .nt("A")
                            .t('+')
                            .nt("T")
                            .finish()
                            .withProduction("A")
                            .nt("T")
                            .finish()
                            .withProduction("T")
                            .nt("T")
                            .t('*')
                            .nt("F")
                            .finish()
                            .withProduction("T")
                            .nt("F")
                            .finish()
                            .withProduction("F")
                            .t('(')
                            .nt("A")
                            .t(')')
                            .finish()
                            .withProduction("F")
                            .nt("B")
                            .finish()
                            .withProduction("B")
                            .t('a')
                            .finish()
                            .withProduction("B")
                            .t('b')
                            .finish()
                            .withProduction("B")
                            .nt("B")
                            .t('a')
                            .finish()
                            .withProduction("B")
                            .nt("B")
                            .t('b')
                            .finish()
                            .withProduction("B")
                            .nt("B")
                            .t('0')
                            .finish()
                            .withProduction("B")
                            .nt("B")
                            .t('1')
                            .finish()
                            .build()
                            .unwrap();

    @Test
    public void testTransform() {
        CFGToPDATransformation<Character, String> transformation = new CFGToPDATransformation<>();

        PDA<String, Character, MaybeGenerated<Character, String>> pda =
                transformation.transform(grammar);
        String initialState =
                pda.getInitialStates().stream()
                        .findFirst()
                        .get(); // Generated PDA should only contain one initial state, so this is
        // fine.
        Set<PDATransition<String, Character, MaybeGenerated<Character, String>>> transitions =
                pda.getTransitions().in(initialState);

        PDATransition<String, Character, MaybeGenerated<Character, String>> trans0 =
                new PDATransition<>(initialState, 'a', exactlyIn('a'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans1 =
                new PDATransition<>(initialState, 'b', exactlyIn('b'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans2 =
                new PDATransition<>(initialState, '0', exactlyIn('0'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans3 =
                new PDATransition<>(initialState, '1', exactlyIn('1'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans4 =
                new PDATransition<>(initialState, '+', exactlyIn('+'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans5 =
                new PDATransition<>(initialState, '*', exactlyIn('*'), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans6 =
                new PDATransition<>(initialState, '(', exactlyIn('('), new PDAStackWord<>());
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans7 =
                new PDATransition<>(initialState, ')', exactlyIn(')'), new PDAStackWord<>());

        PDATransition<String, Character, MaybeGenerated<Character, String>> trans8 =
                new PDATransition<>(
                        initialState,
                        exactlyGen("A"),
                        new PDAStackWord<>(gen("A"), in('+'), gen("T")));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans9 =
                new PDATransition<>(initialState, exactlyGen("A"), new PDAStackWord<>(gen("T")));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans10 =
                new PDATransition<>(
                        initialState,
                        exactlyGen("T"),
                        new PDAStackWord<>(gen("T"), in('*'), gen("F")));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans11 =
                new PDATransition<>(initialState, exactlyGen("T"), new PDAStackWord<>(gen("F")));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans12 =
                new PDATransition<>(
                        initialState,
                        exactlyGen("F"),
                        new PDAStackWord<>(in('('), gen("A"), in(')')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans13 =
                new PDATransition<>(initialState, exactlyGen("F"), new PDAStackWord<>(gen("B")));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans14 =
                new PDATransition<>(initialState, exactlyGen("B"), new PDAStackWord<>(in('a')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans15 =
                new PDATransition<>(initialState, exactlyGen("B"), new PDAStackWord<>(in('b')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans16 =
                new PDATransition<>(
                        initialState, exactlyGen("B"), new PDAStackWord<>(gen("B"), in('a')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans17 =
                new PDATransition<>(
                        initialState, exactlyGen("B"), new PDAStackWord<>(gen("B"), in('b')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans18 =
                new PDATransition<>(
                        initialState, exactlyGen("B"), new PDAStackWord<>(gen("B"), in('0')));
        PDATransition<String, Character, MaybeGenerated<Character, String>> trans19 =
                new PDATransition<>(
                        initialState, exactlyGen("B"), new PDAStackWord<>(gen("B"), in('1')));

        assertEquals(1, pda.getStates().size()); // we construct only one state
        assertEquals(PDAAcceptanceStrategy.EMPTY_STACK, pda.getAcceptanceStrategy());
        assertEquals(terminals, pda.getAlphabet()); // the input alphabet must be identical
        assertEquals(
                12, pda.getStackAlphabet().size()); // 12 = 8 + 4 = |terminals| + |non-terminals|
        assertEquals(20, pda.getTransitions().in(initialState).size()); // by construction

        assertContains(transitions, trans0);
        assertContains(transitions, trans1);
        assertContains(transitions, trans2);
        assertContains(transitions, trans3);
        assertContains(transitions, trans4);
        assertContains(transitions, trans5);
        assertContains(transitions, trans6);
        assertContains(transitions, trans7);
        assertContains(transitions, trans8);
        assertContains(transitions, trans9);
        assertContains(transitions, trans10);
        assertContains(transitions, trans11);
        assertContains(transitions, trans12);
        assertContains(transitions, trans13);
        assertContains(transitions, trans14);
        assertContains(transitions, trans15);
        assertContains(transitions, trans16);
        assertContains(transitions, trans17);
        assertContains(transitions, trans18);
        assertContains(transitions, trans19);
    }

    // Private helper methods to construct PDAStackSymbols instances more easily
    private PDAStackSymbol<MaybeGenerated<Character, String>> exactlyIn(Character c) {
        return PDAStackSymbol.exactly(in(c));
    }

    private PDAStackSymbol<MaybeGenerated<Character, String>> exactlyGen(String s) {
        return PDAStackSymbol.exactly(gen(s));
    }

    private MaybeGenerated<Character, String> in(Character c) {
        return new MaybeGenerated.Input<>(c);
    }

    private MaybeGenerated<Character, String> gen(String s) {
        return new MaybeGenerated.Generated<>(s);
    }
}
