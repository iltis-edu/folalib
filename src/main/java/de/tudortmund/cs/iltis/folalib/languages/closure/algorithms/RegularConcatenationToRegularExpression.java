package de.tudortmund.cs.iltis.folalib.languages.closure.algorithms;

import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import de.tudortmund.cs.iltis.folalib.languages.RegularLanguage;
import de.tudortmund.cs.iltis.folalib.languages.closure.Concatenation;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;

/**
 * An algorithm to compute a RegularExpression for all possible words uv, s.t. u in L1 and v in L2.
 *
 * @param <S> the type of symbols of the language
 */
public class RegularConcatenationToRegularExpression<S extends Serializable>
        implements SerializableFunction<
                Concatenation<RegularLanguage<S>, RegularLanguage<S>>, RegularExpression<S>> {

    @Override
    public final RegularExpression<S> apply(
            Concatenation<RegularLanguage<S>, RegularLanguage<S>> concatenation) {
        RegularExpression<S> regex1 = concatenation.getFirstLanguage().getRegularExpression();
        RegularExpression<S> regex2 = concatenation.getSecondLanguage().getRegularExpression();
        return regex1.concat(regex2);
    }
}
