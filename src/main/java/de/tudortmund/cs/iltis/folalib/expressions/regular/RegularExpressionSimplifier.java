package de.tudortmund.cs.iltis.folalib.expressions.regular;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An instance of a {@link RegularExpressionTraversal} which simplifies regular expressions
 *
 * <pre>
 * Simplifications of the following patterns are implemented:
 * 1) Alternative:
 *    a) nested Alternatives are unnested, since `a + (b + c)` is the same as `a + b + c`
 *    b) Alternatives with single children are "lifted", that is the Alternative is removed and the child is returned
 *    c) EmptyLanguages are removed from Alternative because `a + ∅` is the same as `a`.
 * 2) Concatenation:
 *   a) nested Concatenations are unnested, since `a(bc)` is the same as `abc`
 *   b) Concatenations with single children are "lifted", that is the Concatenation is removed and the child is returned
 *   c) EmptyWords in Concatenations are dropped, because `aε` is just `a`
 *   d) any Concatenation with EmptyLanguage is simplified to just EmptyLanguage
 * 3) KleeneStar:
 *   a) KleeneStar of either EmptyWord or EmptyLanguage is simplified to just EmptyWord
 * 4) KleenePlus:
 *   a) KleenePlus of either EmptyWord or EmptyLanguage is simplified to just EmptyWord
 * 5) Option, Repetition, Range, Symbol, EmptyWord and EmptyLanguage are returned unsimplified
 *
 * Combinations and nestings of the aforementioned simplifications are also implemented, e.g., `∅ + (∅ + a)`
 * becomes simply `a`.
 * </pre>
 *
 * @param <S> the type of symbol in the regular expressions
 */
public class RegularExpressionSimplifier<S extends Serializable>
        extends RegularExpressionTraversal<S, RegularExpression<S>> {

    /**
     * Simplify a regular expression based on semantic identities, such `a + ∅` = `a`.
     *
     * <p>For more details see documentation of {@link RegularExpressionSimplifier}
     *
     * @param regex the regular expression to simplify
     * @param <S> the type of symbols in the regular expression
     * @return a new, simplified expression
     */
    public static <S extends Serializable> RegularExpression<S> simplify(
            RegularExpression<S> regex) {
        return regex.traverse(new RegularExpressionSimplifier<>());
    }

    @Override
    public RegularExpression<S> inspectAlternative(
            Alternative<S> self, List<RegularExpression<S>> childrenOutput) {
        List<RegularExpression<S>> relevantChildren =
                childrenOutput.stream()
                        .flatMap(
                                child ->
                                        child instanceof Alternative
                                                ? child.getChildren().stream()
                                                : Stream.of(child)) // unnnest nested alternatives
                        .filter(child -> !(child instanceof EmptyLanguage))
                        .collect(Collectors.toList());
        if (relevantChildren.isEmpty()) {
            return new EmptyLanguage<>(self.getAlphabet());
        } else if (relevantChildren.size() == 1) {
            return relevantChildren.get(0);
        } else {
            return new Alternative<>(relevantChildren);
        }
    }

    @Override
    public RegularExpression<S> inspectConcatenation(
            Concatenation<S> self, List<RegularExpression<S>> childrenOutput) {
        if (childrenOutput.stream().anyMatch(child -> child instanceof EmptyLanguage)) {
            return new EmptyLanguage<>(self.getAlphabet());
        } else {
            List<RegularExpression<S>> relevantChildren =
                    childrenOutput.stream()
                            .flatMap(
                                    child ->
                                            child instanceof Concatenation
                                                    ? child.getChildren().stream()
                                                    : Stream.of(
                                                            child)) // unnest nested concatenations
                            .filter(child -> !(child instanceof EmptyWord))
                            .collect(Collectors.toList());
            if (relevantChildren.isEmpty()) {
                return new EmptyWord<>(self.getAlphabet());
            } else if (relevantChildren.size() == 1) {
                return relevantChildren.get(0);
            } else {
                return new Concatenation<>(relevantChildren);
            }
        }
    }

    @Override
    public RegularExpression<S> inspectEmptyLanguage(EmptyLanguage<S> self) {
        return self;
    }

    @Override
    public RegularExpression<S> inspectEmptyWord(EmptyWord<S> self) {
        return self;
    }

    @Override
    public RegularExpression<S> inspectKleenePlus(
            KleenePlus<S> self, RegularExpression<S> innerOutput) {
        if (innerOutput instanceof EmptyWord || innerOutput instanceof EmptyLanguage) {
            return new EmptyWord<>(self.getAlphabet());
        } else {
            return new KleenePlus<>(innerOutput);
        }
    }

    @Override
    public RegularExpression<S> inspectKleeneStar(
            KleeneStar<S> self, RegularExpression<S> innerOutput) {
        if (innerOutput instanceof EmptyWord || innerOutput instanceof EmptyLanguage) {
            return new EmptyWord<>(self.getAlphabet());
        } else {
            return new KleeneStar<>(innerOutput);
        }
    }

    @Override
    public RegularExpression<S> inspectOption(Option<S> self, RegularExpression<S> innerOutput) {
        return new Option<>(innerOutput);
    }

    @Override
    public RegularExpression<S> inspectRepetition(
            Repetition<S> self, RegularExpression<S> innerOutput, int lower, int upper) {
        return new Repetition<>(innerOutput, lower, upper);
    }

    @Override
    public RegularExpression<S> inspectRange(Range<S> self, S lower, S upper) {
        return self;
    }

    @Override
    public RegularExpression<S> inspectSymbol(Symbol<S> self, S symbol) {
        return self;
    }
}
