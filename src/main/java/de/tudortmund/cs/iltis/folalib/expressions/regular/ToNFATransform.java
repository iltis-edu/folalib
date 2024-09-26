package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.folalib.automata.StateSupplier;
import de.tudortmund.cs.iltis.folalib.automata.finite.NFABuilder;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ToNFATransform<State extends Serializable, Sym extends Serializable>
        extends RegularExpressionTraversal<Sym, NFABuilder<State, Sym>> implements Serializable {
    private StateSupplier<State> stateSupplier;

    /* For serialization */
    @SuppressWarnings("unused")
    private ToNFATransform() {}

    public ToNFATransform(StateSupplier<State> stateSupplier) {
        this.stateSupplier = stateSupplier;
    }

    @Override
    public NFABuilder<State, Sym> inspectAlternative(
            Alternative<Sym> self, List<NFABuilder<State, Sym>> childrenOutput) {
        State initial = stateSupplier.get();
        State accepting = stateSupplier.get();

        NFABuilder<State, Sym> alternative =
                new NFABuilder<State, Sym>(self.getAlphabet()).withInitial(initial);

        for (NFABuilder<State, Sym> builder : childrenOutput) {
            // Set to false because it worked before merging initial states was possible
            alternative.mergeWith(false, builder);

            // Iterate over every initial state and create an epsilon transition to each of them
            for (State startState : builder.getInitial()) {
                alternative.withEpsilonTransition(initial, startState);
            }
        }

        for (State accept : alternative.getAccepting()) {
            alternative.withEpsilonTransition(accept, accepting);
        }

        return alternative.overrideAccepting(accepting);
    }

    @Override
    public NFABuilder<State, Sym> inspectConcatenation(
            Concatenation<Sym> self, List<NFABuilder<State, Sym>> childrenOutput) {
        NFABuilder<State, Sym> concatenation =
                new NFABuilder<State, Sym>(self.getAlphabet())
                        .withInitial(childrenOutput.get(0).getInitial());

        Set<State> lastAccepting = new LinkedHashSet<>();

        for (NFABuilder<State, Sym> builder : childrenOutput) {
            // Set to false because it worked before merging initial states was possible
            concatenation.mergeWith(false, builder);

            for (State accepting : lastAccepting) {
                concatenation.withEpsilonTransition(accepting, builder.getInitial());
            }

            lastAccepting = builder.getAccepting();
        }
        concatenation.overrideAccepting(lastAccepting);
        return concatenation;
    }

    // empty set
    @Override
    public NFABuilder<State, Sym> inspectEmptyLanguage(EmptyLanguage<Sym> self) {
        return new NFABuilder<State, Sym>(self.getAlphabet()).withInitial(stateSupplier.get());
    }

    @Override
    public NFABuilder<State, Sym> inspectEmptyWord(EmptyWord<Sym> self) {
        State initial = stateSupplier.get();
        State accepting = stateSupplier.get();

        return new NFABuilder<State, Sym>(self.getAlphabet())
                .withInitial(initial)
                .withAccepting(accepting)
                .withEpsilonTransition(initial, accepting);
    }

    @Override
    public NFABuilder<State, Sym> inspectKleenePlus(
            KleenePlus<Sym> self, NFABuilder<State, Sym> innerOutput) {
        throw new UnsupportedOperationException("Cannot convert non-standard regex into NFA");
    }

    @Override
    public NFABuilder<State, Sym> inspectKleeneStar(
            KleeneStar<Sym> self, NFABuilder<State, Sym> innerOutput) {
        State initial = stateSupplier.get();
        State accepting = stateSupplier.get();

        NFABuilder<State, Sym> builder =
                new NFABuilder<State, Sym>(self.getAlphabet())
                        .mergeWith(
                                false, innerOutput) // Set to false because it worked before merging
                        // initial states was possible
                        .overrideInitial(initial)
                        .overrideAccepting(accepting)
                        .withEpsilonTransition(initial, innerOutput.getInitial())
                        .withEpsilonTransition(initial, accepting);
        innerOutput
                .getAccepting()
                .forEach(state -> builder.withEpsilonTransition(state, accepting));
        innerOutput
                .getAccepting()
                .forEach(state -> builder.withEpsilonTransition(state, innerOutput.getInitial()));

        return builder;
    }

    @Override
    public NFABuilder<State, Sym> inspectOption(
            Option<Sym> self, NFABuilder<State, Sym> innerOutput) {
        throw new UnsupportedOperationException("Cannot convert non-standard regex into NFA");
    }

    @Override
    public NFABuilder<State, Sym> inspectRepetition(
            Repetition<Sym> self, NFABuilder<State, Sym> innerOutput, int lower, int upper) {
        throw new UnsupportedOperationException("Cannot convert non-standard regex into NFA");
    }

    @Override
    public NFABuilder<State, Sym> inspectRange(Range<Sym> self, Sym lower, Sym upper) {
        throw new UnsupportedOperationException("Cannot convert non-standard regex into NFA");
    }

    @Override
    public NFABuilder<State, Sym> inspectSymbol(Symbol<Sym> self, Sym symbol) {
        State initial = stateSupplier.get();
        State accepting = stateSupplier.get();

        return new NFABuilder<State, Sym>(self.getAlphabet())
                .withInitial(initial)
                .withAccepting(accepting)
                .withTransition(initial, symbol, accepting);
    }
}
