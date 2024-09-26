package de.tudortmund.cs.iltis.folalib.languages;

import java.io.Serializable;

/**
 * An ABC to indicate whether two language are possibly equivalent or definitely not equivalent
 *
 * @param <S> the type of the symbols of the language
 */
public abstract class HeuristicEquivalenceResult<S extends Serializable> implements Serializable {

    /**
     * Instances of this class indicate that two languages <b>may</b> be equivalent, i.e. that no
     * counterexample has been found.
     */
    static final class EquivalencePossible<S extends Serializable>
            extends HeuristicEquivalenceResult<S> {}

    /**
     * Instances of this class indicate that two languages <b>may</b> be equivalent regarding the
     * words they accept. The alphabets of the languages differ.
     *
     * <p>Equivalence in this context means that no counterexample has been found.
     */
    static final class EquivalencePossibleIgnoringAlphabets<S extends Serializable>
            extends HeuristicEquivalenceResult<S> {}

    /**
     * Instance of this class indicate that two languages are definitely <b>not</b> equivalent
     * because a concrete counterexample has been found.
     */
    static final class EquivalenceDisproved<S extends Serializable>
            extends HeuristicEquivalenceResult<S> {
        private Word<S> counterExample;

        private EquivalenceDisproved(Word<S> counterExample) {
            this.counterExample = counterExample;
        }

        /* For serialization */
        @SuppressWarnings("unused")
        private EquivalenceDisproved() {}

        public Word<S> getCounterExample() {
            return counterExample;
        }
    }

    public static <S extends Serializable> HeuristicEquivalenceResult<S> possible() {
        return new EquivalencePossible<>();
    }

    public static <S extends Serializable>
            HeuristicEquivalenceResult<S> possibleIgnoringAlphabets() {
        return new EquivalencePossibleIgnoringAlphabets<>();
    }

    public static <S extends Serializable> HeuristicEquivalenceResult<S> disproved(
            Word<S> counterExample) {
        return new EquivalenceDisproved<>(counterExample);
    }
}
