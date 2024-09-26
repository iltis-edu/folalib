package de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.standard;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputConversionException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;

/** A strategy that simply uses two pre-defined alphabets */
public class GivenAlphabetsInputConverter implements InputToGrammarSymbolConverter {

    Alphabet<IndexedSymbol> terminals;
    Alphabet<IndexedSymbol> nonTerminals;

    public GivenAlphabetsInputConverter(
            Alphabet<IndexedSymbol> terminals, Alphabet<IndexedSymbol> nonTerminals) {
        // Intersection of alphabets must be empty to avoid ambiguities
        if (!Alphabets.intersectionOf(terminals, nonTerminals).isEmpty())
            throw new IllegalArgumentException("Alphabets of terminals and nonTerminals intersect");

        this.terminals = terminals;
        this.nonTerminals = nonTerminals;
    }

    /**
     * @throws InputConversionException if neither alphabet contains {@param input}
     */
    @Override
    public GrammarSymbol<IndexedSymbol, IndexedSymbol> convertInput(IndexedSymbol input)
            throws InputConversionException {
        if (terminals.contains(input)) return new GrammarSymbol.Terminal<>(input);
        else if (nonTerminals.contains(input)) return new GrammarSymbol.NonTerminal<>(input);

        throw new InputConversionException(input, "Alphabets do not contain the symbol " + input);
    }

    /** For serialization */
    @SuppressWarnings("unused")
    private GivenAlphabetsInputConverter() {}
}
