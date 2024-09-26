package de.tudortmund.cs.iltis.folalib.languages;

import de.tudortmund.cs.iltis.folalib.expressions.regular.Alternative;
import de.tudortmund.cs.iltis.folalib.expressions.regular.RegularExpression;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Stream;

@Deprecated
public class FiniteLanguage<S extends Serializable> extends TreeSet<Word<S>>
        implements Language<S> {
    public boolean contains(Word<S> word) {
        return super.contains(word);
    }

    public RegularExpression<S> regularExpression() {
        List<RegularExpression<S>> words = new ArrayList<>();
        for (Word<S> word : this) {
            words.add(RegularExpression.fromWord(word));
        }
        return new Alternative<>(words.toArray(new RegularExpression[0]));
    }

    @Override
    public Alphabet<S> getAlphabet() {
        List<S> symbols = new ArrayList<>();
        for (Word<S> word : this) {
            symbols.addAll(word.toUnmodifiableList());
        }
        return new Alphabet<>(symbols);
    }

    public Stream<Word<S>> allWords() {
        return this.stream();
    }
}
