package de.tudortmund.cs.iltis.folalib.automata.pushdown;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Models the last component of a PDATransition, i.e. a PDAStackWord represents the sequence of
 * symbols that replace the topmost symbol of the PDA stack when a transition is triggered.
 *
 * @param <K> The type of the stack alphabet symbols
 */
public class PDAStackWord<K extends Serializable> extends Word<K> {

    public PDAStackWord() {}

    @SafeVarargs
    public PDAStackWord(K... ks) {
        super(Arrays.asList(ks));
    }

    public PDAStackWord(Collection<? extends K> collection) {
        super(collection);
    }

    /** Checks whether this PDAStackWord contains any wildcards */
    public boolean containsWildcard() {
        return contains(null);
    }

    /**
     * Substitutes all wildcards (if any) in this stack word with the given symbol
     *
     * @param symbol The symbol to replace the wildcards with
     * @return a new PDAStackWord
     */
    public PDAStackWord<K> substituteWildcards(K symbol) {
        return map(k -> k == null ? symbol : k);
    }

    @Override
    public <T extends Serializable> PDAStackWord<T> map(Function<? super K, T> f) {
        return shareConstructInto(PDAStackWord::new, super.map(f));
    }

    @Override
    public PDAStackWord<K> filter(Predicate<? super K> p) {
        return shareConstructInto(PDAStackWord::new, super.filter(p));
    }

    @Override
    public PDAStackWord<K> drop(int prefixSize) {
        return shareConstructInto(PDAStackWord::new, super.drop(prefixSize));
    }

    @Override
    public PDAStackWord<K> take(int prefixSize) {
        return shareConstructInto(PDAStackWord::new, super.take(prefixSize));
    }

    /**
     * Reverses the order of this PDAStackWord
     *
     * <p>This is a utility method, because this class is used in combination with stacks and
     * pushing the elements of a sequence to a stack inadvertently reverses the order. With this
     * method we can mitigate this issue by reversing this collection itself before pushing all
     * elements sequentially.
     *
     * @return a new PDAStackWord with its elements in reverse order
     */
    public PDAStackWord<K> reverse() {
        ArrayList<K> reversedSymbols = new ArrayList<>();
        forEach(reversedSymbols::add);
        Collections.reverse(reversedSymbols);
        return new PDAStackWord<>(reversedSymbols);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (K k : this) {
            result.append(k == null ? "τ" : k);
        }
        return result.length() == 0 ? "ε" : result.toString();
    }
}
