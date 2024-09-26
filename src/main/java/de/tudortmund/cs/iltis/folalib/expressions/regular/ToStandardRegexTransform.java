package de.tudortmund.cs.iltis.folalib.expressions.regular;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <S> The type of the symbols used in the alphabet of the {@link RegularExpression}
 */
public class ToStandardRegexTransform<S extends Serializable>
        extends RegularExpressionTraversal<S, RegularExpression<S>> {

    /**
     * This transformation transforms the given regular expression to standard regex. This is done
     * by replacing non-standard regex-operands with standard regex-operands. The transformation
     * will be performed as follows:
     *
     * <p>{@link KleenePlus}: Replace "a⁺" with "aa*" {@link Option}: Replace "a?" with "a+ε" {@link
     * Repetition}: Replace "a{x, y}" with "a^x(a+ε)^(y-x)" where x and y are natural numbers (incl.
     * 0) and x <= y. An example: "a{2,4}" gets replaced with "aa(a+ε)(a+ε)" {@link Range}: Replace
     * "[a-c]" with "a+b+c" where a, b and c are {@link Symbol}s in a total order. If no symbols are
     * included in the given range it will be replaced by an {@link EmptyLanguage}. The order of
     * symbols is taken from the {@code Comparator} that was passed to {@code Range} during
     * construction (defaults to {@code Comparator.naturalOrdering()} if {@code S} implements {@code
     * Comparable}).
     *
     * <p>Please note: "a" is a placeholder for any regular expression (i.e. every subclass of
     * {@link RegularExpression}).
     *
     * @param regex The regex which shall be transformed to standard regex
     * @param <S> The type of the symbols used in this regular expression
     * @return A standardized regular expression
     */
    public static <S extends Serializable> RegularExpression<S> toStandard(
            RegularExpression<S> regex) {
        return regex.traverse(new ToStandardRegexTransform<>());
    }

    @Override
    public RegularExpression<S> inspectAlternative(
            Alternative<S> self, List<RegularExpression<S>> childrenOutput) {
        return new Alternative<>(childrenOutput);
    }

    @Override
    public RegularExpression<S> inspectConcatenation(
            Concatenation<S> self, List<RegularExpression<S>> childrenOutput) {
        return new Concatenation<>(childrenOutput);
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
        return innerOutput.concat(innerOutput.star());
    }

    @Override
    public RegularExpression<S> inspectKleeneStar(
            KleeneStar<S> self, RegularExpression<S> innerOutput) {
        return innerOutput.star();
    }

    @Override
    public RegularExpression<S> inspectOption(Option<S> self, RegularExpression<S> innerOutput) {
        return innerOutput.or(new EmptyWord<>(self.getAlphabet()));
    }

    @Override
    public RegularExpression<S> inspectRepetition(
            Repetition<S> self, RegularExpression<S> innerOutput, int lower, int upper) {
        // EDGE CASES:
        if (lower == 0 && upper == 0) return new EmptyWord<>(self.getAlphabet());
        if (lower == 1 && upper == 1) return innerOutput;

        // This is basically an Option
        RegularExpression<S> optionalOcc = inspectOption(innerOutput.optional(), innerOutput);

        if (lower == 0 && upper == 1) return optionalOcc;

        // NORMAL CASES:

        List<RegularExpression<S>> subexpressions =
                new ArrayList<>(
                        Collections.nCopies(
                                lower,
                                innerOutput)); // the collection returned by `.nCopies` is immutable

        if (self.upper - self.lower > 0) {
            subexpressions.addAll(Collections.nCopies(self.upper - self.lower, optionalOcc));
        }

        return new Concatenation<>(subexpressions);
    }

    @Override
    public RegularExpression<S> inspectRange(Range<S> self, S lower, S upper) {
        List<Symbol<S>> includedSymbols =
                self.getIncludedSymbols().stream()
                        .map(symbol -> new Symbol<>(self.getAlphabet(), symbol))
                        .collect(Collectors.toList());

        if (includedSymbols.isEmpty()) return new EmptyLanguage<>(self.getAlphabet());
        if (includedSymbols.size() == 1) return includedSymbols.get(0);

        return new Alternative<>(includedSymbols);
    }

    @Override
    public RegularExpression<S> inspectSymbol(Symbol<S> self, S symbol) {
        return self;
    }
}
