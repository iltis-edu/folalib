package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Concatenation;
import de.tudortmund.cs.iltis.folalib.expressions.regular.KleenePlus;
import de.tudortmund.cs.iltis.folalib.expressions.regular.KleeneStar;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.languages.closure.Homomorphism;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An algorithm to compute a RegularExpression for all words of a regular language after applying a
 * homomorphism.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularHomomorphismToRegularExpression<S extends Serializable, T extends Serializable>
        implements SerializableFunction<
                Homomorphism<S, T, RegularLanguage<S>>, RegularExpression<T>> {

    @Override
    public RegularExpression<T> apply(Homomorphism<S, T, RegularLanguage<S>> homomorphism) {
        RegularExpression<S> regex = homomorphism.getLanguage().getRegularExpression();
        SerializableFunction<S, Word<T>> homo = homomorphism.getHomomorphism();
        return regex.traverse(
                new RegularExpressionTraversal<S, RegularExpression<T>>() {
                    @Override
                    public RegularExpression<T> inspectAlternative(
                            Alternative<S> self, List<RegularExpression<T>> childrenOutput) {
                        return new Alternative<>(childrenOutput);
                    }

                    @Override
                    public RegularExpression<T> inspectConcatenation(
                            Concatenation<S> self, List<RegularExpression<T>> childrenOutput) {
                        return new Concatenation<>(childrenOutput);
                    }

                    @Override
                    public RegularExpression<T> inspectEmptyLanguage(EmptyLanguage<S> self) {
                        return new EmptyLanguage<>(homomorphism.getTargetAlphabet());
                    }

                    @Override
                    public RegularExpression<T> inspectEmptyWord(EmptyWord<S> self) {
                        return new EmptyWord<>(homomorphism.getTargetAlphabet());
                    }

                    @Override
                    public RegularExpression<T> inspectKleenePlus(
                            KleenePlus<S> self, RegularExpression<T> innerOutput) {
                        return new KleenePlus<>(innerOutput);
                    }

                    @Override
                    public RegularExpression<T> inspectKleeneStar(
                            KleeneStar<S> self, RegularExpression<T> innerOutput) {
                        return new KleeneStar<>(innerOutput);
                    }

                    @Override
                    public RegularExpression<T> inspectOption(
                            Option<S> self, RegularExpression<T> innerOutput) {
                        return innerOutput.optional();
                    }

                    @Override
                    public RegularExpression<T> inspectRepetition(
                            Repetition<S> self,
                            RegularExpression<T> innerOutput,
                            int lower,
                            int upper) {
                        return innerOutput.repetition(lower, upper);
                    }

                    @Override
                    public RegularExpression<T> inspectRange(Range<S> self, S lower, S upper) {
                        // Must be first converted to standard regex to map each symbol individually
                        RegularExpression<S> standardRegex =
                                ToStandardRegexTransform.toStandard(self);
                        Homomorphism<S, T, RegularLanguage<S>> tempHomomorphism =
                                new Homomorphism<>(new RegularLanguage<>(standardRegex), homo);
                        return new RegularHomomorphismToRegularExpression<S, T>()
                                .apply(tempHomomorphism);
                    }

                    @Override
                    public RegularExpression<T> inspectSymbol(Symbol<S> self, S symbol) {
                        Word<T> mapped = homo.apply(symbol);
                        if (mapped.isEmpty()) {
                            return new EmptyWord<>(homomorphism.getTargetAlphabet());
                        } else if (mapped.size() == 1) {
                            return new Symbol<>(homomorphism.getTargetAlphabet(), mapped.get(0));
                        } else {
                            return new Concatenation<>(
                                    mapped.stream()
                                            .map(
                                                    t ->
                                                            new Symbol<>(
                                                                    homomorphism
                                                                            .getTargetAlphabet(),
                                                                    t))
                                            .collect(Collectors.toList()));
                        }
                    }
                });
    }
}
