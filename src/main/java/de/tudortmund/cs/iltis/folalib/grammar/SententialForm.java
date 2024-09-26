package de.tudortmund.cs.iltis.folalib.grammar;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.utils.function.SerializableFunction;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SententialForm<T extends Serializable, N extends Serializable>
        extends Word<GrammarSymbol<T, N>> {
    public SententialForm() {
        super();
    }

    @SafeVarargs
    public SententialForm(GrammarSymbol<T, N>... symbols) {
        super(symbols);
    }

    public SententialForm(Collection<GrammarSymbol<T, N>> symbols) {
        super(symbols);
    }

    public boolean containsAnyNonTerminal() {
        return stream().anyMatch(GrammarSymbol::isNonTerminal);
    }

    public boolean containsAnyTerminal() {
        return stream().anyMatch(GrammarSymbol::isTerminal);
    }

    public boolean containsNonTerminal(N nonTerminal) {
        return contains(new GrammarSymbol.NonTerminal<>(nonTerminal));
    }

    public boolean containsTerminal(T terminal) {
        return contains(new GrammarSymbol.Terminal<>(terminal));
    }

    public boolean isOnlyNonTerminal(N nonTerminal) {
        return size() == 1 && containsNonTerminal(nonTerminal);
    }

    public boolean isOnlyTerminal(T Terminal) {
        return size() == 1 && containsTerminal(Terminal);
    }

    public <S extends Serializable, M extends Serializable> SententialForm<S, M> map(
            Function<T, GrammarSymbol<S, M>> terminalMap,
            Function<N, GrammarSymbol<S, M>> nonTerminalMap) {
        return new SententialForm<>(
                stream()
                        .map(symbol -> symbol.match(terminalMap, nonTerminalMap))
                        .collect(Collectors.toList()));
    }

    public <S extends Serializable> SententialForm<S, N> mapTerminals(
            SerializableFunction<T, S> f) {
        return map(t -> new GrammarSymbol.Terminal<>(f.apply(t)), GrammarSymbol.NonTerminal::new);
    }

    public <M extends Serializable> SententialForm<T, M> mapNonTerminals(
            SerializableFunction<N, M> f) {
        return map(GrammarSymbol.Terminal::new, n -> new GrammarSymbol.NonTerminal<>(f.apply(n)));
    }

    public SententialForm<T, N> replaceTerminal(T terminal, GrammarSymbol<T, N> replacement) {
        return map(
                t -> {
                    if (t.equals(terminal)) {
                        return replacement;
                    } else {
                        return new GrammarSymbol.Terminal<>(t);
                    }
                },
                GrammarSymbol.NonTerminal::new);
    }

    public SententialForm<T, N> replaceNonTerminal(N nonTerminal, GrammarSymbol<T, N> replacement) {
        return map(
                GrammarSymbol.Terminal::new,
                n -> {
                    if (n.equals(nonTerminal)) {
                        return replacement;
                    } else {
                        return new GrammarSymbol.NonTerminal<>(n);
                    }
                });
    }

    @Override
    public SententialForm<T, N> drop(int prefixSize) {
        return shareConstructInto(SententialForm::new, super.drop(prefixSize));
    }

    @Override
    public SententialForm<T, N> take(int prefixSize) {
        return shareConstructInto(SententialForm::new, super.take(prefixSize));
    }

    public Set<T> getTerminals() {
        return stream()
                .filter(GrammarSymbol::isTerminal)
                .map(GrammarSymbol::unwrapTerminal)
                .collect(Collectors.toSet());
    }

    public Set<N> getNonTerminals() {
        return stream()
                .filter(GrammarSymbol::isNonTerminal)
                .map(GrammarSymbol::unwrapNonTerminal)
                .collect(Collectors.toSet());
    }
}
