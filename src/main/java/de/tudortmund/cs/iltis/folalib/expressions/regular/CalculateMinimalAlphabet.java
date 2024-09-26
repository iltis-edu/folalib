package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import java.io.Serializable;
import java.util.List;

/**
 * This calculation returns the minimal possible {@link Alphabet} for the given {@link
 * RegularExpression}, i.e. every symbol contained in the alphabet actually occurs in the regular
 * expression. The alphabet will not contain any "spare" symbols.
 */
public class CalculateMinimalAlphabet<S extends Serializable>
        extends RegularExpressionTraversal<S, Alphabet<S>> {

    /**
     * Returns the minimal possible {@link Alphabet} for the given {@link RegularExpression}, i.e.
     * every symbol contained in the alphabet actually occurs in the regular expression. The
     * alphabet will not contain any "spare" symbols.
     *
     * @param regex The regex whose minimal alphabet shall be calculated
     * @return The minimal possible alphabet
     * @param <S> The type of the symbols used in this regular expression
     */
    public static <S extends Serializable> Alphabet<S> calculateMinimalAlphabet(
            RegularExpression<S> regex) {
        return regex.traverse(new CalculateMinimalAlphabet<>());
    }

    @Override
    public Alphabet<S> inspectAlternative(Alternative<S> self, List<Alphabet<S>> childrenOutput) {
        return Alphabets.unionOf(childrenOutput);
    }

    @Override
    public Alphabet<S> inspectConcatenation(
            Concatenation<S> self, List<Alphabet<S>> childrenOutput) {
        return Alphabets.unionOf(childrenOutput);
    }

    @Override
    public Alphabet<S> inspectEmptyLanguage(EmptyLanguage<S> self) {
        return new Alphabet<>();
    }

    @Override
    public Alphabet<S> inspectEmptyWord(EmptyWord<S> self) {
        return new Alphabet<>();
    }

    @Override
    public Alphabet<S> inspectKleenePlus(KleenePlus<S> self, Alphabet<S> innerOutput) {
        return innerOutput;
    }

    @Override
    public Alphabet<S> inspectKleeneStar(KleeneStar<S> self, Alphabet<S> innerOutput) {
        return innerOutput;
    }

    @Override
    public Alphabet<S> inspectOption(Option<S> self, Alphabet<S> innerOutput) {
        return innerOutput;
    }

    @Override
    public Alphabet<S> inspectRepetition(
            Repetition<S> self, Alphabet<S> innerOutput, int lower, int upper) {
        return innerOutput;
    }

    @Override
    public Alphabet<S> inspectRange(Range<S> self, S lower, S upper) {
        return Alphabets.unionOf(self.getIncludedSymbols(), new Alphabet<>(lower, upper));
    }

    @Override
    public Alphabet<S> inspectSymbol(Symbol<S> self, S symbol) {
        return new Alphabet<>(symbol);
    }
}
