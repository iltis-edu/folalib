package de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk;

import de.tudortmund.cs.iltis.folalib.grammar.contextfree.ContextFreeGrammar;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault.CYKEntryFault;
import de.tudortmund.cs.iltis.folalib.grammar.contextfree.cyk.fault.CYKEntryFaultCollection;
import de.tudortmund.cs.iltis.folalib.grammar.production.ChomskyNormalformProduction;
import de.tudortmund.cs.iltis.folalib.languages.Word;
import de.tudortmund.cs.iltis.folalib.util.Result;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import java.io.Serializable;
import java.util.*;

public class CYKTableau<
        T extends Serializable, N extends Serializable, Entry extends ICYKTableauEntry<N>> {
    protected final ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>> grammar;
    protected final Word<T> toCheck;

    private Map<Pair<Integer, Integer>, Set<Entry>> tableau;

    public CYKTableau(
            ContextFreeGrammar<T, N, ChomskyNormalformProduction<T, N>> grammar, Word<T> toCheck) {
        this.grammar = grammar;
        this.toCheck = toCheck;

        this.tableau = new HashMap<>();
    }

    public Set<Entry> get(int i, int j) {
        if (i < 0 || i >= toCheck.size())
            throw new IndexOutOfBoundsException("Index out of range: " + i);
        if (j < 0 || j >= toCheck.size())
            throw new IndexOutOfBoundsException("Index out of range: " + j);
        if (j < i) throw new IndexOutOfBoundsException("j < i");

        Pair<Integer, Integer> key = new Pair<>(i, j);

        if (!tableau.containsKey(key)) tableau.put(key, new LinkedHashSet<>());

        return tableau.get(key);
    }

    public void add(int i, int j, Entry entry) {
        get(i, j).add(entry);
    }

    protected void set(int i, int j, Set<Entry> entries) {
        tableau.put(new Pair<>(i, j), entries);
    }

    public boolean accepts() {
        if (toCheck.isEmpty()) return false; // grammars in CNF do not allow derivation of epsilon

        return get(0, toCheck.size() - 1).stream()
                .anyMatch(p -> p.getNonTerminal().equals(grammar.getStartSymbol()));
    }

    public Result<
                    CorrectCYKTableau<T, N>,
                    Map<Pair<Integer, Integer>, CYKEntryFaultCollection<T, N, Entry>>>
            validate() {
        CorrectCYKTableau<T, N> validateAgainst = CorrectCYKTableau.compute(grammar, toCheck);
        Map<Pair<Integer, Integer>, CYKEntryFaultCollection<T, N, Entry>> faultMap =
                new HashMap<>();

        for (int l = 0; l < toCheck.size(); ++l) {
            for (int i = 0; i < toCheck.size() - l; ++i) {
                List<CYKEntryFault<T, N, Entry>> iilFaults = new ArrayList<>();

                // What the cell (i, i + l) actually contains
                Set<Entry> actual = get(i, i + l);

                // What the cell (i, i + l) would contain if the current step was executed
                // correctly, based on the the other, previously computed, fields in the CYKTableau
                // (local correctness).
                Set<InternalCYKTableauEntry<T, N>> expectedLocal = computeCellWithCyk(i, i + l);

                // What the cell (i, i + l) would contain if it was computed by a correct
                // CYK-implementation (global correctness)
                Set<InternalCYKTableauEntry<T, N>> expectedGlobal = validateAgainst.get(i, i + l);

                //   i) Symmetric difference of `expectedLocal` and `actual` yields all mistakes
                // done in the current step.
                //  ii) Symmetric difference of (`expectedLocal` intersection `actual) and
                // `expectedGlobal` yields all errors that are aftereffects of errors made in
                // previous steps
                // iii) The set ((`expectedLocal` minus `actual`) minus `expectedGlobal`) union
                // ((`actual` minus `expectedLocal`) intersection `expectedGlobal`) yields all
                // "ghost errors": Local errors which actually "fix" a previous mistake by
                // accidentally resulting in an entry (or absence thereof) which is actually part of
                // the correct global solution. Note that this set is a subset of i).

                // MISSING_ENTRY, MISSING_ENTRY_GHOST and ABUNDANT_ENTRY_AFTEREFFECT detection:
                outer:
                for (InternalCYKTableauEntry<T, N> el : expectedLocal) {
                    boolean isExpectedGlobal =
                            expectedGlobal.stream()
                                    .anyMatch(e -> e.getNonTerminal().equals(el.getNonTerminal()));

                    for (Entry entry : actual) {
                        if (entry.getNonTerminal().equals(el.getNonTerminal())) {
                            if (!isExpectedGlobal) {
                                // abundant entry aftereffect!
                                HashMap<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>>
                                        causeMap = new HashMap<>();
                                // This cast is safe, as on the diagonal of the tableau the sets
                                // `expectedLocal` and `expectedGlobal` are always the same
                                ChomskyNormalformProduction.TwoNonTerminalsProduction<T, N>
                                        production =
                                                (ChomskyNormalformProduction
                                                                        .TwoNonTerminalsProduction<
                                                                T, N>)
                                                        el.getProduction();

                                List<CYKEntryFault<T, N, Entry>> faults1 =
                                        faultMap.get(new Pair<>(i, el.getK()))
                                                .getAbundantFaultsFor(
                                                        production.getFirstNonTerminal());
                                List<CYKEntryFault<T, N, Entry>> faults2 =
                                        faultMap.get(new Pair<>(el.getK() + 1, i + l))
                                                .getAbundantFaultsFor(
                                                        production.getSecondNonTerminal());

                                if (!faults1.isEmpty())
                                    causeMap.put(new Pair<>(i, el.getK()), faults1);
                                if (!faults2.isEmpty())
                                    causeMap.put(new Pair<>(el.getK() + 1, i + l), faults2);

                                iilFaults.add(new CYKEntryFault<>(entry, causeMap));
                            }

                            continue outer;
                        }
                    }

                    // (local) missing entry detected.

                    iilFaults.add(new CYKEntryFault<>(el, !isExpectedGlobal));
                }

                // ABUNDANT_ENTRY and ABUNDANT_ENTRY_GHOST detection
                outer:
                for (Entry entry : actual) {
                    for (InternalCYKTableauEntry<T, N> el : expectedLocal) {
                        if (entry.getNonTerminal().equals(el.getNonTerminal())) continue outer;
                    }

                    // abundant (local) entry detected
                    boolean isExpectedGlobal =
                            expectedGlobal.stream()
                                    .anyMatch(
                                            e -> e.getNonTerminal().equals(entry.getNonTerminal()));

                    iilFaults.add(new CYKEntryFault<>(entry, isExpectedGlobal));
                }

                // MISSING_ENTRY_AFTEREFFECT detection
                for (InternalCYKTableauEntry<T, N> eg : expectedGlobal) {
                    if (expectedLocal.stream()
                                    .anyMatch(e -> e.getNonTerminal().equals(eg.getNonTerminal()))
                            || actual.stream()
                                    .anyMatch(e -> e.getNonTerminal().equals(eg.getNonTerminal())))
                        continue;

                    // after effect of missing entry fault detected
                    HashMap<Pair<Integer, Integer>, List<CYKEntryFault<T, N, Entry>>> causeMap =
                            new HashMap<>();
                    // This cast is safe, as on the diagonal of the tableau the sets `expectedLocal`
                    // and `expectedGlobal` are always the same
                    ChomskyNormalformProduction.TwoNonTerminalsProduction<T, N> production =
                            (ChomskyNormalformProduction.TwoNonTerminalsProduction<T, N>)
                                    eg.getProduction();

                    List<CYKEntryFault<T, N, Entry>> faults1 =
                            faultMap.get(new Pair<>(i, eg.getK()))
                                    .getAbundantFaultsFor(production.getFirstNonTerminal());
                    List<CYKEntryFault<T, N, Entry>> faults2 =
                            faultMap.get(new Pair<>(eg.getK() + 1, i + l))
                                    .getAbundantFaultsFor(production.getSecondNonTerminal());

                    if (!faults1.isEmpty()) causeMap.put(new Pair<>(i, eg.getK()), faults1);
                    if (!faults2.isEmpty()) causeMap.put(new Pair<>(eg.getK() + 1, i + l), faults2);

                    iilFaults.add(new CYKEntryFault<>(eg, causeMap));
                }

                // TODO: WRONG_K_VALUE detection

                if (!iilFaults.isEmpty())
                    faultMap.put(new Pair<>(i, i + l), new CYKEntryFaultCollection<>(iilFaults));
            }
        }

        if (faultMap.isEmpty()) return new Result.Ok<>(validateAgainst);
        else return new Result.Err<>(faultMap);
    }

    protected Set<InternalCYKTableauEntry<T, N>> computeCellWithCyk(int i, int j) {
        assert (j >= i);

        Set<InternalCYKTableauEntry<T, N>> entries = new LinkedHashSet<>();

        if (i == j) {
            for (ChomskyNormalformProduction<T, N> cnfProduction : grammar) {
                cnfProduction.consumeRhs(
                        t -> {
                            if (t.equals(toCheck.get(i))) {
                                entries.add(new InternalCYKTableauEntry<>(cnfProduction, i));
                            }
                        },
                        (b, c) -> {});
            }
        } else {
            for (int k = i; k < j; ++k) {
                final int finalK = k; // Java restriction about effectively final variables.

                for (ChomskyNormalformProduction<T, N> cnfProduction : grammar) {
                    cnfProduction.consumeRhs(
                            t -> {},
                            (b, c) -> {
                                if (tableau.get(new Pair<>(i, finalK)).stream()
                                                .anyMatch(p -> p.getNonTerminal().equals(b))
                                        && tableau.get(new Pair<>(finalK + 1, j)).stream()
                                                .anyMatch(p -> p.getNonTerminal().equals(c))) {
                                    entries.add(
                                            new InternalCYKTableauEntry<>(cnfProduction, finalK));
                                }
                            });
                }
            }
        }

        return entries;
    }
}
