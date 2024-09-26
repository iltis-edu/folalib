package de.tudortmund.cs.iltis.folalib.grammar.builder;

import de.tudortmund.cs.iltis.folalib.grammar.construction.builder.C0GrammarBuilder;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.GrammarConstructionFaultReason;
import de.tudortmund.cs.iltis.folalib.grammar.construction.fault.SyntaxFault;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.folalib.languages.Alphabets;
import de.tudortmund.cs.iltis.utils.collections.Fault;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class C0GrammarBuilderTest {

    @Test
    public void testGrammarBuilderRejectsStartSymbolNotInAlphabet() {
        Alphabet<Character> terminals = Alphabets.characterAlphabet("ab");
        Alphabet<String> nonTerminals = new Alphabet<>("A", "B");

        C0GrammarBuilder<Character, String> builder =
                new C0GrammarBuilder<>(terminals, nonTerminals);
        builder.withStartSymbol("S"); // "S" is not a valid non terminal
        GrammarConstructionFaultCollection faultCollection = builder.validate();
        List<Fault<GrammarConstructionFaultReason>> faults = faultCollection.getFaults();

        Assert.assertEquals(1, faults.size());
        Assert.assertEquals(SyntaxFault.unknownNonTerminalSymbol("S"), faults.get(0));
    }
}
