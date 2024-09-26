package de.tudortmund.cs.iltis.folalib.grammar;

import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.reader.grammar.C0GrammarReader;
import de.tudortmund.cs.iltis.folalib.io.reader.grammar.GrammarReaderProperties;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.util.CachedSerializableFunction;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import de.tudortmund.cs.iltis.utils.io.parser.error.IncorrectParseInputException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Grammar<T extends Serializable, N extends Serializable, Prod extends Production<T, N>>
        implements Iterable<Prod>, Serializable {
    private N startSymbol;

    private Alphabet<T> terminals;
    private Alphabet<N> nonTerminals;

    private Set<Prod> productions;

    public static Grammar<IndexedSymbol, IndexedSymbol, Production<IndexedSymbol, IndexedSymbol>>
            fromString(String s, GrammarReaderProperties props)
                    throws IncorrectParseInputException {

        return new C0GrammarReader(props).read(s);
    }

    /* For serialization */
    @SuppressWarnings("unused")
    protected Grammar() {}

    public Grammar(
            Alphabet<T> terminals,
            Alphabet<N> nonTerminals,
            N startSymbol,
            Collection<? extends Prod> productions) {
        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
        this.startSymbol = startSymbol;
        this.productions = new LinkedHashSet<>();
        this.productions.addAll(productions);
    }

    public Grammar(
            Collection<? extends T> terminals,
            Collection<? extends N> nonTerminals,
            N startSymbol,
            Collection<? extends Prod> productions) {
        this(new Alphabet<>(terminals), new Alphabet<>(nonTerminals), startSymbol, productions);
    }

    /**
     * Computes the set of productions that can be applied to derive {@code form}.
     *
     * <p>Whenever a production is applicable, the index at which the productions LHS is matched is
     * returned as well. If a production matches multiple times (or even overlapping), it is
     * reported multiple times.
     *
     * <p>For example, consider the grammar {@code G} with productions
     *
     * <pre>
     * ABA -> aBA
     * BA -> C
     * C -> c
     * </pre>
     *
     * and the sentential form {@code ABABACC}. The call {@code G.getApplicableProductions(ABABACC)}
     * would return the set {@code {(ABA -> aBA, 0), (ABA -> aBA, 2}, (BA -> C, 1), (BA -> C, 4), (C
     * -> c, 5), (C -> c, 6)}.
     *
     * @param form The {@link SententialForm} to determine all possible derivation steps for
     * @return A set of applicable productions, together with indices into {@code form} indicating
     *     where that production can be applied.
     */
    // TODO: test case
    public Set<Pair<Prod, Integer>> getApplicableProductions(SententialForm<T, N> form) {
        Set<Pair<Prod, Integer>> applicableProductions = new HashSet<>();

        for (int i = 0; i < form.size(); ++i) {
            Word<GrammarSymbol<T, N>> subform = form.drop(i);

            for (Prod production : productions) {
                if (production.getLhs().isPrefixOf(subform))
                    applicableProductions.add(new Pair<>(production, i));
            }
        }

        return applicableProductions;
    }

    public N getStartSymbol() {
        return startSymbol;
    }

    public Alphabet<T> getTerminals() {
        return terminals;
    }

    public Alphabet<N> getNonTerminals() {
        return nonTerminals;
    }

    public Set<Prod> getProductions() {
        return Collections.unmodifiableSet(productions);
    }

    @Override
    public Iterator<Prod> iterator() {
        return getProductions().iterator();
    }

    /**
     * Map functions `terminalMap` and `nonTerminalMap` over the terminals and non-terminals of this
     * grammar
     *
     * @param terminalMap the function to apply to each terminal
     * @param nonTerminalMap the function to apply to each non-terminal
     * @param <S> the type of the terminals in the new grammar
     * @param <M> the type of the non-terminals in the new grammar
     * @return a new, isomorphic grammar with mapped terminals and non-terminals
     */
    public <S extends Serializable, M extends Serializable>
            Grammar<S, M, ? extends Production<S, M>> map(
                    SerializableFunction<T, S> terminalMap,
                    SerializableFunction<N, M> nonTerminalMap) {
        SerializableFunction<T, S> cachedTerminalsMapping =
                new CachedSerializableFunction<>(terminalMap);
        SerializableFunction<N, M> cachedNonTerminalsMapping =
                new CachedSerializableFunction<>(nonTerminalMap);
        Alphabet<S> mappedTerminals = getTerminals().map(cachedTerminalsMapping);
        Alphabet<M> mappedNonTerminals = getNonTerminals().map(cachedNonTerminalsMapping);
        M mappedStartSymbol = cachedNonTerminalsMapping.apply(getStartSymbol());
        Set<Production<S, M>> mappedProductions =
                getProductions().stream()
                        .map(p -> p.mapNonTerminals(cachedNonTerminalsMapping))
                        .map(p -> p.mapTerminals(cachedTerminalsMapping))
                        .collect(Collectors.toSet());
        return new Grammar<>(
                mappedTerminals, mappedNonTerminals, mappedStartSymbol, mappedProductions);
    }

    public <M extends Serializable> Grammar<T, M, ? extends Production<T, M>> mapNonTerminals(
            SerializableFunction<N, M> f) {
        return map(t -> t, f);
    }

    public <S extends Serializable> Grammar<S, N, ? extends Production<S, N>> mapTerminals(
            SerializableFunction<T, S> f) {
        return map(f, n -> n);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Grammar<?, ?, ? extends Production<?, ?>> grammar =
                (Grammar<?, ?, ? extends Production<?, ?>>) obj;
        if (!grammar.getTerminals().equals(this.terminals)) return false;
        if (!grammar.getNonTerminals().equals(this.nonTerminals)) return false;
        if (!grammar.getStartSymbol().equals(this.startSymbol)) return false;

        return grammar.getProductions().equals(this.productions);
    }
}
