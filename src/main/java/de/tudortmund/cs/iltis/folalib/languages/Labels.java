package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.automata.pushdown.PDA;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.grammar.Grammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.ContextFreeProduction;
import de.tudortmund.cs.iltis.folalib.grammar.production.RightRegularProduction;
import de.tudortmund.cs.iltis.folalib.languages.closure.*;
import de.tudortmund.cs.iltis.folalib.transform.Label;
import java.io.Serializable;

public final class Labels {
    private Labels() {
        throw new AssertionError();
    }

    /* Regular Language Representations */

    public static final class EpsilonNFALabel<Symbol extends Serializable>
            extends Label<NFA<? extends Serializable, Symbol>> {}

    public static final class DeterministicNFALabel<Symbol extends Serializable>
            extends Label<NFA<? extends Serializable, Symbol>> {}

    public static final class StandardRegularExpressionLabel<Symbol extends Serializable>
            extends Label<RegularExpression<Symbol>> {}

    public static final class RegularExpressionLabel<Symbol extends Serializable>
            extends Label<RegularExpression<Symbol>> {}

    public static final class RightRegularGrammarLabel<Symbol extends Serializable>
            extends Label<Grammar<Symbol, ?, RightRegularProduction<Symbol, ?>>> {}

    public static final class RegularComplementLabel<Symbol extends Serializable>
            extends Label<Complement<RegularLanguage<Symbol>>> {}

    public static final class RegularConcatenation<Symbol extends Serializable>
            extends Label<Concatenation<RegularLanguage<Symbol>, RegularLanguage<Symbol>>> {}

    public static final class RegularDifferenceLabel<Symbol extends Serializable>
            extends Label<Difference<RegularLanguage<Symbol>, RegularLanguage<Symbol>>> {}

    public static final class RegularHomomorphismLabel<
                    Symbol extends Serializable, T extends Serializable>
            extends Label<Homomorphism<T, Symbol, RegularLanguage<T>>> {}

    public static final class RegularIntersectionLabel<Symbol extends Serializable>
            extends Label<Intersection<RegularLanguage<Symbol>, RegularLanguage<Symbol>>> {}

    public static final class RegularInverseHomomorphismLabel<
                    Symbol extends Serializable, T extends Serializable>
            extends Label<InverseHomomorphism<Symbol, T, RegularLanguage<T>>> {}

    public static final class RegularKleenePlusLabel<Symbol extends Serializable>
            extends Label<KleenePlus<RegularLanguage<Symbol>>> {}

    public static final class RegularKleeneStarLabel<Symbol extends Serializable>
            extends Label<KleeneStar<RegularLanguage<Symbol>>> {}

    public static final class RegularReversalLabel<Symbol extends Serializable>
            extends Label<Reversal<RegularLanguage<Symbol>>> {}

    public static final class RegularUnionLabel<Symbol extends Serializable>
            extends Label<Union<RegularLanguage<Symbol>, RegularLanguage<Symbol>>> {}

    public static final class RegularSymmetricDifferenceLabel<Symbol extends Serializable>
            extends Label<SymmetricDifference<RegularLanguage<Symbol>, RegularLanguage<Symbol>>> {}

    /* Context-free Language Representations */

    public static final class PDALabel<
                    T extends Serializable, Symbol extends Serializable, K extends Serializable>
            extends Label<PDA<T, Symbol, K>> {}

    public static final class CFGLabel<Symbol extends Serializable, N extends Serializable>
            extends Label<
                    ContextFreeGrammar<Symbol, N, ? extends ContextFreeProduction<Symbol, N>>> {}

    public static final class CNFLabel<Symbol extends Serializable, N extends Serializable>
            extends Label<
                    ContextFreeGrammar<
                            Symbol, N, ? extends ChomskyNormalformProduction<Symbol, N>>> {}
}
