package de.tudortmund.cs.iltis.folalib.expressions.regular;

import de.tudortmund.cs.iltis.utils.tree.TraversalStrategy;
import java.io.Serializable;
import java.util.List;

public abstract class RegularExpressionTraversal<Sym extends Serializable, O>
        implements TraversalStrategy<RegularExpression<Sym>, O> {
    @Override
    public O inspect(RegularExpression<Sym> item, List<O> childrenOutput) {
        if (item instanceof Alternative<?>)
            return inspectAlternative((Alternative<Sym>) item, childrenOutput);
        if (item instanceof Concatenation<?>)
            return inspectConcatenation((Concatenation<Sym>) item, childrenOutput);
        if (item instanceof EmptyLanguage<?>)
            return inspectEmptyLanguage((EmptyLanguage<Sym>) item);
        if (item instanceof EmptyWord<?>) return inspectEmptyWord((EmptyWord<Sym>) item);
        if (item instanceof KleenePlus<?>)
            return inspectKleenePlus((KleenePlus<Sym>) item, childrenOutput.get(0));
        if (item instanceof KleeneStar<?>)
            return inspectKleeneStar((KleeneStar<Sym>) item, childrenOutput.get(0));
        if (item instanceof Option<?>)
            return inspectOption((Option<Sym>) item, childrenOutput.get(0));
        if (item instanceof Repetition<?>) {
            Repetition<Sym> repetition = (Repetition<Sym>) item;
            return inspectRepetition(
                    (Repetition<Sym>) item,
                    childrenOutput.get(0),
                    repetition.getLower(),
                    repetition.getUpper());
        }
        if (item instanceof Range<?>) {
            Range<Sym> range = (Range<Sym>) item;
            return inspectRange((Range<Sym>) item, range.getLower(), range.getUpper());
        }
        if (item instanceof Symbol<?>)
            return inspectSymbol((Symbol<Sym>) item, ((Symbol<Sym>) item).getSymbol());

        throw new RuntimeException("Unreachable");
    }

    public abstract O inspectAlternative(Alternative<Sym> self, List<O> childrenOutput);

    public abstract O inspectConcatenation(Concatenation<Sym> self, List<O> childrenOutput);

    public abstract O inspectEmptyLanguage(EmptyLanguage<Sym> self);

    public abstract O inspectEmptyWord(EmptyWord<Sym> self);

    public abstract O inspectKleenePlus(KleenePlus<Sym> self, O innerOutput);

    public abstract O inspectKleeneStar(KleeneStar<Sym> self, O innerOutput);

    public abstract O inspectOption(Option<Sym> self, O innerOutput);

    public abstract O inspectRepetition(Repetition<Sym> self, O innerOutput, int lower, int upper);

    public abstract O inspectRange(Range<Sym> self, Sym lower, Sym upper);

    public abstract O inspectSymbol(Symbol<Sym> self, Sym symbol);
}
