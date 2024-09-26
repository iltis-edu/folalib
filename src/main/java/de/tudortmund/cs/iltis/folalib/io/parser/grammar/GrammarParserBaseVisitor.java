// Generated from ../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/grammar/GrammarParser.g4
// by ANTLR 4.4
package de.tudortmund.cs.iltis.folalib.io.parser.grammar;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link GrammarParserVisitor}, which can be
 * extended to create a visitor which only needs to handle a subset of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public class GrammarParserBaseVisitor<T> extends AbstractParseTreeVisitor<T>
        implements GrammarParserVisitor<T> {
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitAbundantRightSideSeparatorsERROR(
            @NotNull GrammarParser.AbundantRightSideSeparatorsERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitCorrectProduction(@NotNull GrammarParser.CorrectProductionContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitRightSideSeparatorOnLhsERROR(
            @NotNull GrammarParser.RightSideSeparatorOnLhsERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitProductionWithSeparatorBothERROR(
            @NotNull GrammarParser.ProductionWithSeparatorBothERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitMultipleProductionArrowsERROR(
            @NotNull GrammarParser.MultipleProductionArrowsERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitAbundantEpsilonERROR(@NotNull GrammarParser.AbundantEpsilonERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitRhsWithMultipleSeparatorsCheck(
            @NotNull GrammarParser.RhsWithMultipleSeparatorsCheckContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitRightSideMissingERROR(@NotNull GrammarParser.RightSideMissingERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitCorrectGrammar(@NotNull GrammarParser.CorrectGrammarContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitSymSepEps(@NotNull GrammarParser.SymSepEpsContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitCorrectSententialForm(@NotNull GrammarParser.CorrectSententialFormContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitProductionWithSeparatorRightERROR(
            @NotNull GrammarParser.ProductionWithSeparatorRightERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitCorrectRightSideSeparator(
            @NotNull GrammarParser.CorrectRightSideSeparatorContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitNoProductionArrowERROR(@NotNull GrammarParser.NoProductionArrowERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitEntry(@NotNull GrammarParser.EntryContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitLeftSideMissingERROR(@NotNull GrammarParser.LeftSideMissingERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitProductionWithSeparatorLeftERROR(
            @NotNull GrammarParser.ProductionWithSeparatorLeftERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitEmptyGrammarERROR(@NotNull GrammarParser.EmptyGrammarERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitSymbolEpsilonMixERROR(@NotNull GrammarParser.SymbolEpsilonMixERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitSententialFormLINK(@NotNull GrammarParser.SententialFormLINKContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitEpsilon(@NotNull GrammarParser.EpsilonContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitBothSidesMissingERROR(@NotNull GrammarParser.BothSidesMissingERRORContext ctx) {
        return visitChildren(ctx);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling {@link #visitChildren} on {@code
     * ctx}.
     */
    @Override
    public T visitEmptySententialFormERROR(
            @NotNull GrammarParser.EmptySententialFormERRORContext ctx) {
        return visitChildren(ctx);
    }
}
