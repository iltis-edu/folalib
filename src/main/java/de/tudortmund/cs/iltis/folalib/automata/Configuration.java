package de.tudortmund.cs.iltis.folalib.automata;

import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.Objects;

public class Configuration<T extends Serializable, S extends Serializable> {
    protected T state;
    protected Word<S> word;
    protected int position;

    public Configuration(T state, Word<S> word) {
        this(state, word, 0);
    }

    public Configuration(T state, Word<S> word, int position) {
        this.state = state;
        this.word = word;
        this.position = position;
    }

    public T getState() {
        return this.state;
    }

    public boolean hasSymbol() {
        return this.position < this.word.size();
    }

    public S getCurrentSymbol() {
        return this.word.get(this.position);
    }

    public int getPosition() {
        return this.position;
    }

    public Word<S> getWord() {
        return this.word;
    }

    @Override
    public String toString() {
        return "(" + this.state + "," + this.word.drop(this.position) + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Configuration)) return false;
        Configuration<?, ?> other = (Configuration<?, ?>) o;
        return this.position == other.position
                && this.word.equals(other.word)
                && this.state.equals(other.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, word, position);
    }
}
