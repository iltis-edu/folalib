package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.DerivationTree;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CorrectCYKTableau<T extends Serializable, N extends Serializable>
        extends CYKTableau<T, N, InternalCYKTableauEntry<T, N>> {
    public static <T extends Serializable, N extends Serializable> CorrectCYKTableau<T, N> compute(
            ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>> grammar, Word<T> toCheck) {
        CorrectCYKTableau<T, N> tableau = new CorrectCYKTableau<>(grammar, toCheck);

        for (int l = 0; l < toCheck.size(); ++l) {
            for (int i = 0; i < toCheck.size() - l; ++i) {
                tableau.set(i, i + l, tableau.computeCellWithCyk(i, i + l));
            }
        }

        return tableau;
    }

    public Set<DerivationTree<T, N>> computeDerivationTrees() {
        if (!accepts()) return new HashSet<>();
        return computeDerivationTrees(0, toCheck.size() - 1, grammar.getStartSymbol());
    }

    private Set<DerivationTree<T, N>> computeDerivationTrees(int i, int j, N nonTerminal) {
        Set<DerivationTree<T, N>> trees = new HashSet<>();

        for (InternalCYKTableauEntry<T, N> entry : get(i, j)) {
            if (entry.getNonTerminal().equals(nonTerminal)) {
                ChomskyNormalformProduction<T, N> production = entry.getProduction();

                production.consumeRhs(
                        t -> {
                            trees.add(new DerivationTree<>(nonTerminal, new DerivationTree<>(t)));
                        },
                        (n1, n2) -> {
                            int k = entry.getK();

                            for (DerivationTree<T, N> leftTree : computeDerivationTrees(i, k, n1)) {
                                for (DerivationTree<T, N> rightTree :
                                        computeDerivationTrees(k + 1, j, n2)) {
                                    trees.add(
                                            new DerivationTree<>(nonTerminal, leftTree, rightTree));
                                }
                            }
                        });
            }
        }

        return trees;
    }

    private CorrectCYKTableau(
            ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>> grammar, Word<T> toCheck) {
        super(grammar, toCheck);
    }
}
