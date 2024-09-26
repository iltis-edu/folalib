package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.automata.StateSupplier;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFA;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReader;
import de.tudortmund.cs.iltis.folalib.io.reader.regex.RegularExpressionReaderProperties;
import de.tudortmund.cs.iltis.folalib.io.writer.regex.RegularExpressionWriter;
import de.tudortmund.cs.iltis.folalib.io.writer.regex.RegularExpressionWriterProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.general.Data;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import de.tudortmund.cs.iltis.utils.term.Term;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class RegularExpression<S extends Serializable>
        extends Term<RegularExpression<S>, String> {
    public final transient RegularExpressionWriter<S> toStringWriterText =
            new RegularExpressionWriter<>(
                    RegularExpressionWriterProperties.defaultTextProperties());

    public static RegularExpression<IndexedSymbol> fromString(
            String s, RegularExpressionReaderProperties props) throws IncorrectParseInputException {
        return new RegularExpressionReader(props).read(s);
    }

    private Alphabet<S> alphabet;

    public Alphabet<S> getAlphabet() {
        return alphabet;
    }

    // For GWT serialization
    protected RegularExpression() {}

    /**
     * Build a new RegularExpression object over the given alphabet with the specified
     * subexpressions.
     *
     * @param alphabet the alphabet, over which this expression is defined
     * @param arityFixed whether the arity is fixed
     * @param subexpressions the (possibly empty) list of subexpressions
     * @throws IllegalArgumentException if the given Alphabet is empty
     */
    protected RegularExpression(
            Alphabet<S> alphabet,
            boolean arityFixed,
            Iterable<? extends RegularExpression<S>> subexpressions) {
        super(arityFixed, subexpressions);
        this.alphabet = alphabet;
    }

    /**
     * Build a new RegularExpression object over the given alphabet with the specified
     * subexpressions.
     *
     * @param alphabet the alphabet, over which this expression is defined
     * @param arityFixed whether the arity is fixed
     * @param subexpressions the (possibly empty) array of subexpressions
     * @throws IllegalArgumentException if the given Alphabet is empty
     */
    @SafeVarargs
    protected RegularExpression(
            Alphabet<S> alphabet, boolean arityFixed, RegularExpression<S>... subexpressions) {
        this(alphabet, arityFixed, Arrays.asList(subexpressions));
    }

    protected static <S extends Serializable> Alphabet<S> inferAlphabetFromChildren(
            Iterable<? extends RegularExpression<S>> subexpressions) {
        Alphabet<S> alphabet = null;
        for (RegularExpression<S> regex : subexpressions) {
            if (alphabet == null) {
                alphabet = regex.getAlphabet();
            } else if (!alphabet.equals(regex.getAlphabet())) {
                throw new IllegalArgumentException(
                        "Alphabet of all children must be identical to be inferred.");
            }
        }
        if (alphabet == null) {
            throw new IllegalArgumentException(
                    "At least one child required to infer alphabet from subexpressions.");
        } else {
            return alphabet;
        }
    }

    /**
     * Tries to set the given alphabet for this regular expression.
     *
     * @throws IllegalArgumentException if the given alphabet cannot be used as an alphabet for this
     *     regular expression
     * @param alphabet the alphabet to use
     * @return a new regular expression with the new alphabet
     */
    public abstract RegularExpression<S> withAlphabet(Alphabet<S> alphabet);

    public final RegularExpression<S> extendAlphabet(Alphabet<S> alphabet) {
        return withAlphabet(Alphabets.unionOf(getAlphabet(), alphabet));
    }

    public final RegularExpression<S> reduceAlphabet(Alphabet<S> alphabet) {
        return withAlphabet(Alphabets.intersectionOf(getAlphabet(), alphabet));
    }

    /**
     * Transforms this regular expression into a regular expression with minimal alphabet, i.e.
     * every symbol contained in the alphabet actually occurs in the regular expression.
     *
     * @return A new regular expression with minimal alphabet.
     */
    public RegularExpression<S> withMinimalAlphabet() {
        return this.withAlphabet(getMinimalAlphabet());
    }

    /**
     * Calculates the minimal possible {@link Alphabet} for this regular expression, i.e. every
     * symbol contained in the alphabet actually occurs in the regular expression. The alphabet will
     * not contain any "spare" symbols.
     *
     * @return The minimal possible alphabet for this regular expression.
     */
    public Alphabet<S> getMinimalAlphabet() {
        return CalculateMinimalAlphabet.calculateMinimalAlphabet(this);
    }

    private static <S extends Serializable> Alphabet<S> unionAlphabet(
            Iterable<? extends RegularExpression<S>> expressions) {
        return Alphabets.unionOf(
                StreamSupport.stream(expressions.spliterator(), false)
                        .map(RegularExpression::getAlphabet)
                        .collect(Collectors.toSet()));
    }

    @SafeVarargs
    private static <S extends Serializable> Alphabet<S> unionAlphabet(
            RegularExpression<S>... expressions) {
        return unionAlphabet(Arrays.asList(expressions));
    }

    /**
     * Creates a concatenation of {@code this} with all given subexpressions.
     *
     * <p>The alphabet will be the union of the alphabet of this regex and the union of all
     * alphabets of all given subexpressions.
     *
     * @param subexpressions the subexpressions to concatenate to {@code this}
     * @return a new concatenation of {@code this} and all {@code subexpressions}
     */
    @SafeVarargs
    public final Concatenation<S> concat(RegularExpression<S>... subexpressions) {
        return concat(Arrays.asList(subexpressions));
    }

    /**
     * Creates a concatenation of {@code this} with all given subexpressions.
     *
     * <p>The alphabet will be the union of the alphabet of this regex and the union of all
     * alphabets of all given subexpressions.
     *
     * @param subexpressions the subexpressions to concatenate to {@code this}
     * @return a new concatenation of {@code this} and all {@code subexpressions}
     */
    public final Concatenation<S> concat(Iterable<? extends RegularExpression<S>> subexpressions) {
        Alphabet<S> unionAlphabet = Alphabets.unionOf(getAlphabet(), unionAlphabet(subexpressions));
        List<RegularExpression<S>> extendedSubExpressions =
                Data.map(Data.newArrayList(subexpressions), r -> r.extendAlphabet(unionAlphabet));
        return new Concatenation<>(
                Data.newArrayList1(extendAlphabet(unionAlphabet), extendedSubExpressions));
    }

    /**
     * Creates a alternative of {@code this} with all given subexpressions.
     *
     * <p>The alphabet will be the union of the alphabet of this regex and the union of all
     * alphabets of all given subexpressions.
     *
     * @param subexpressions the subexpressions which are an alternative to {@code this}
     * @return a new alternative of {@code this} and all {@code subexpressions}
     */
    @SafeVarargs
    public final Alternative<S> or(RegularExpression<S>... subexpressions) {
        return or(Arrays.asList(subexpressions));
    }

    /**
     * Creates a alternative of {@code this} with all given subexpressions.
     *
     * <p>The alphabet will be the union of the alphabet of this regex and the union of all
     * alphabets of all given subexpressions.
     *
     * @param subexpressions the subexpressions which are an alternative to {@code this}
     * @return a new alternative of {@code this} and all {@code subexpressions}
     */
    public final Alternative<S> or(Iterable<? extends RegularExpression<S>> subexpressions) {
        Alphabet<S> unionAlphabet = Alphabets.unionOf(getAlphabet(), unionAlphabet(subexpressions));
        List<RegularExpression<S>> extendedSubExpressions =
                Data.map(Data.newArrayList(subexpressions), r -> r.extendAlphabet(unionAlphabet));
        return new Alternative<>(
                Data.newArrayList1(extendAlphabet(unionAlphabet), extendedSubExpressions));
    }

    /**
     * Embeds {@code this} within a KleeneStar.
     *
     * <p>The alphabet will be the alphabet of this regex.
     *
     * @return a new KleeneStar of {@code this}
     */
    public KleeneStar<S> star() {
        return new KleeneStar<>(this);
    }

    /**
     * Embeds {@code this} within an Option.
     *
     * <p>The alphabet will be the alphabet of this regex.
     *
     * @return a new Option of {@code this}
     */
    public Option<S> optional() {
        return new Option<>(this);
    }

    /**
     * Embeds {@code this} within a KleenePlus.
     *
     * <p>The alphabet will be the alphabet of this regex.
     *
     * @return a new KleenePlus of {@code this}
     */
    public KleenePlus<S> plus() {
        return new KleenePlus<>(this);
    }

    /**
     * Embeds {@code this} within a Repetition.
     *
     * <p>The alphabet will be the alphabet of this regex.
     *
     * @param numberOfRepetitions the exact number of times {@code this} must occur
     * @return a new Repetition of {@code this}
     */
    public Repetition<S> repetition(int numberOfRepetitions) {
        return new Repetition<>(this, numberOfRepetitions);
    }

    /**
     * Embeds {@code this} within a Repetition.
     *
     * <p>The alphabet will be the alphabet of this regex.
     *
     * @param lower the minimum number of times {@code this} must occur
     * @param upper the maximum number of times {@code this} must occur
     * @return a new Repetition of {@code this}
     */
    public Repetition<S> repetition(int lower, int upper) {
        return new Repetition<>(this, lower, upper);
    }

    public <T extends Serializable> NFA<T, S> toNFA(StateSupplier<T> stateSupplier) {
        return traverse(new ToNFATransform<>(stateSupplier))
                .buildAndReset()
                .unwrap()
                .totalify(stateSupplier);
    }

    @Override
    public String toString() {
        return toStringWriterText.write(this);
    }

    public static <Z extends Serializable> RegularExpression<Z> fromWord(Word<Z> word) {
        Alphabet<Z> alphabet = new Alphabet<>(word.toUnmodifiableList());
        return new Concatenation<>(word.map(s -> new Symbol<>(alphabet, s)).toUnmodifiableList());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && alphabet.equals(((RegularExpression<?>) obj).alphabet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), alphabet);
    }
}
