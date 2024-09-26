package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.expressions.regular.Concatenation;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Reversal;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An algorithm to compute a RegularExpression for all reversed words of a language.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularReversalToRegularExpression<S extends Serializable>
        implements SerializableFunction<Reversal<RegularLanguage<S>>, RegularExpression<S>> {

    @Override
    public RegularExpression<S> apply(Reversal<RegularLanguage<S>> reversal) {
        return reversal.getLanguage()
                .getRegularExpression()
                .traverse(
                        new RegularExpressionTraversal<S, RegularExpression<S>>() {
                            @Override
                            public RegularExpression<S> inspectAlternative(
                                    Alternative<S> self,
                                    List<RegularExpression<S>> childrenOutput) {
                                return new Alternative<>(childrenOutput);
                            }

                            @Override
                            public RegularExpression<S> inspectConcatenation(
                                    Concatenation<S> self,
                                    List<RegularExpression<S>> childrenOutput) {
                                List<RegularExpression<S>> children =
                                        new LinkedList<>(childrenOutput);
                                Collections.reverse(children);
                                return new Concatenation<>(children);
                            }

                            @Override
                            public RegularExpression<S> inspectEmptyLanguage(
                                    EmptyLanguage<S> self) {
                                return new EmptyLanguage<>(self.getAlphabet());
                            }

                            @Override
                            public RegularExpression<S> inspectEmptyWord(EmptyWord<S> self) {
                                return new EmptyWord<>(self.getAlphabet());
                            }

                            @Override
                            public RegularExpression<S> inspectKleenePlus(
                                    KleenePlus<S> self, RegularExpression<S> innerOutput) {
                                return innerOutput.plus();
                            }

                            @Override
                            public RegularExpression<S> inspectKleeneStar(
                                    KleeneStar<S> self, RegularExpression<S> innerOutput) {
                                return innerOutput.star();
                            }

                            @Override
                            public RegularExpression<S> inspectOption(
                                    Option<S> self, RegularExpression<S> innerOutput) {
                                return innerOutput.optional();
                            }

                            @Override
                            public RegularExpression<S> inspectRepetition(
                                    Repetition<S> self,
                                    RegularExpression<S> innerOutput,
                                    int lower,
                                    int upper) {
                                return innerOutput.repetition(lower, upper);
                            }

                            @Override
                            public RegularExpression<S> inspectRange(
                                    Range<S> self, S lower, S upper) {
                                return self;
                            }

                            @Override
                            public RegularExpression<S> inspectSymbol(Symbol<S> self, S symbol) {
                                return new Symbol<>(self.getAlphabet(), symbol);
                            }
                        });
    }
}
