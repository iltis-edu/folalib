package de.tudortmund.cs.iltis.folalib.io.writer.regex;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.utils.collections.Pair;
import de.tudortmund.cs.iltis.utils.io.writer.general.DefaultWriter;
import de.tudortmund.cs.iltis.utils.io.writer.general.Writer;
import de.tudortmund.cs.iltis.utils.tree.TraversalStrategy;
import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RegularExpressionWriter<S extends Serializable>
        implements TraversalStrategy<
                        RegularExpression<S>,
                        Pair<StringBuilder, RegularExpressionWriter.TopLevelOperator>>,
                Writer<RegularExpression<S>> {
    private RegularExpressionWriterProperties properties;

    private Writer<S> symbolToString;

    public RegularExpressionWriter(RegularExpressionWriterProperties properties) {
        this(properties, new DefaultWriter<>());
    }

    public RegularExpressionWriter(
            RegularExpressionWriterProperties properties, Writer<S> symbolToString) {
        this.properties = properties;
        this.symbolToString = symbolToString;
    }

    protected StringBuilder writeEmptyLanguage() {
        return new StringBuilder(properties.getEmptySet());
    }

    protected StringBuilder writeEmptyWord() {
        return new StringBuilder(properties.getEpsilon());
    }

    protected StringBuilder writeConcatenation(
            List<Pair<StringBuilder, TopLevelOperator>> subterms) {
        return new StringBuilder(
                subterms.stream()
                        .map(parenthesiser(TopLevelOperator.Concatenation))
                        .collect(Collectors.joining(properties.getConcatenation())));
    }

    protected StringBuilder writeAlternation(List<Pair<StringBuilder, TopLevelOperator>> subterms) {
        return new StringBuilder(
                subterms.stream()
                        .map(parenthesiser(TopLevelOperator.Alternation))
                        .collect(Collectors.joining(properties.getAlternation())));
    }

    protected StringBuilder writeKleeneStar(Pair<StringBuilder, TopLevelOperator> subTerm) {
        return parenthesiser(TopLevelOperator.Postfix)
                .apply(subTerm)
                .append(properties.getKleeneStar());
    }

    protected StringBuilder writeKleenePlus(Pair<StringBuilder, TopLevelOperator> subTerm) {
        return parenthesiser(TopLevelOperator.Postfix)
                .apply(subTerm)
                .append(properties.getKleenePlus());
    }

    protected StringBuilder writeOption(Pair<StringBuilder, TopLevelOperator> subTerm) {
        return parenthesiser(TopLevelOperator.Postfix)
                .apply(subTerm)
                .append(properties.getOption());
    }

    protected StringBuilder writeRepetition(
            Pair<StringBuilder, TopLevelOperator> subTerm, int lower, int upper) {
        if (upper > lower)
            return parenthesiser(TopLevelOperator.Postfix)
                    .apply(subTerm)
                    .append(properties.getRepetitionStart())
                    .append(lower)
                    .append(properties.getRepetitionSeparator())
                    .append(upper)
                    .append(properties.getRepetitionEnd());

        return parenthesiser(TopLevelOperator.Postfix)
                .apply(subTerm)
                .append(properties.getRepetitionStart())
                .append(lower)
                .append(properties.getRepetitionEnd());
    }

    protected StringBuilder writeRange(S lower, S upper) {
        return new StringBuilder(properties.getRangeStart())
                .append(symbolToString.write(lower))
                .append(properties.getRangeSeparator())
                .append(symbolToString.write(upper))
                .append(properties.getRangeEnd());
    }

    protected StringBuilder writeSymbol(Symbol<S> symbol) {
        return new StringBuilder(symbolToString.write(symbol.getSymbol()));
    }

    /**
     * Helper method checking if a subterm that has top level operator {@code subterm} would have to
     * be parenthesised when used as an operand to an operator of type {@code newTopLevel}.
     *
     * <p>For example, if we have the subterm {@code a + b}, the top level operator would be {@link
     * TopLevelOperator#Alternation}. If we wanted to write it as a subterm of a concatenation, e.g.
     * {@code a(a+b)}, we'd have to surround it by parenthesis, as concatenation has higher
     * precedence as alternation
     *
     * @param subterm The top level operator of the subterm
     * @param newTopLevel The top level operator of the term {@code subterm} is to be used as an
     *     operand for
     * @return {@code true} iff {@code newTopLevel} has higher precedence as {@code subterm}
     */
    private static boolean requiresParenthesis(
            TopLevelOperator subterm, TopLevelOperator newTopLevel) {
        switch (subterm) {
            case None:
                return false;
            case Postfix:
                return newTopLevel == TopLevelOperator.Postfix;
            case Concatenation:
                return newTopLevel == TopLevelOperator.Postfix
                        || newTopLevel == TopLevelOperator.Concatenation;
            case Alternation:
                return true;
        }
        throw new RuntimeException(
                "Java's inability to recognize exhaustive switches strikes again");
    }

    private Function<Pair<StringBuilder, TopLevelOperator>, StringBuilder> parenthesiser(
            TopLevelOperator newTopLevel) {
        return p -> {
            if (requiresParenthesis(p.second(), newTopLevel)) {
                p.first().insert(0, properties.getOpeningParenthesis());
                p.first().append(properties.getClosingParenthesis());
            }
            return p.first();
        };
    }

    @Override
    public Pair<StringBuilder, TopLevelOperator> inspect(
            RegularExpression<S> subterm,
            List<Pair<StringBuilder, TopLevelOperator>> childrenOutput) {
        if (subterm instanceof EmptyLanguage)
            return new Pair<>(writeEmptyLanguage(), TopLevelOperator.None);
        else if (subterm instanceof EmptyWord)
            return new Pair<>(writeEmptyWord(), TopLevelOperator.None);
        else if (subterm instanceof Symbol)
            return new Pair<>(writeSymbol((Symbol<S>) subterm), TopLevelOperator.None);
        else if (subterm instanceof Concatenation)
            return new Pair<>(writeConcatenation(childrenOutput), TopLevelOperator.Concatenation);
        else if (subterm instanceof Alternative)
            return new Pair<>(writeAlternation(childrenOutput), TopLevelOperator.Alternation);
        else if (subterm instanceof KleeneStar)
            return new Pair<>(writeKleeneStar(childrenOutput.get(0)), TopLevelOperator.Postfix);
        else if (subterm instanceof Option)
            return new Pair<>(writeOption(childrenOutput.get(0)), TopLevelOperator.Postfix);
        else if (subterm instanceof KleenePlus)
            return new Pair<>(writeKleenePlus(childrenOutput.get(0)), TopLevelOperator.Postfix);
        else if (subterm instanceof Repetition)
            return new Pair<>(
                    writeRepetition(
                            childrenOutput.get(0),
                            ((Repetition<S>) subterm).getLower(),
                            ((Repetition<S>) subterm).getUpper()),
                    TopLevelOperator.Postfix);
        else if (subterm instanceof Range)
            return new Pair<>(
                    writeRange(((Range<S>) subterm).getLower(), ((Range<S>) subterm).getUpper()),
                    TopLevelOperator.None);

        throw new RuntimeException("above if-chain is exhaustive");
    }

    @Override
    public String write(RegularExpression<S> object) {
        return object.traverse(this).first().toString();
    }

    enum TopLevelOperator {
        None,
        Postfix,
        Concatenation,
        Alternation
    }
}
