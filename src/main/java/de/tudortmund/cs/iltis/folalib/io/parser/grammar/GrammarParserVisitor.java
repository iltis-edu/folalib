// Generated from ../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/grammar/GrammarParser.g4
// by ANTLR 4.4
package de.tudortmund.cs.iltis.folalib.io.parser.grammar;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * GrammarParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface GrammarParserVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by the {@code AbundantRightSideSeparatorsERROR} labeled
     * alternative in {@link GrammarParser#rhsSeparators}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAbundantRightSideSeparatorsERROR(
            @NotNull GrammarParser.AbundantRightSideSeparatorsERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectProduction} labeled alternative in {@link
     * GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectProduction(@NotNull GrammarParser.CorrectProductionContext ctx);

    /**
     * Visit a parse tree produced by the {@code RightSideSeparatorOnLhsERROR} labeled alternative
     * in {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRightSideSeparatorOnLhsERROR(
            @NotNull GrammarParser.RightSideSeparatorOnLhsERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code ProductionWithSeparatorBothERROR} labeled
     * alternative in {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProductionWithSeparatorBothERROR(
            @NotNull GrammarParser.ProductionWithSeparatorBothERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code MultipleProductionArrowsERROR} labeled alternative
     * in {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMultipleProductionArrowsERROR(
            @NotNull GrammarParser.MultipleProductionArrowsERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code AbundantEpsilonERROR} labeled alternative in {@link
     * GrammarParser#sententialForm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAbundantEpsilonERROR(@NotNull GrammarParser.AbundantEpsilonERRORContext ctx);

    /**
     * Visit a parse tree produced by {@link GrammarParser#rhsWithMultipleSeparatorsCheck}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRhsWithMultipleSeparatorsCheck(
            @NotNull GrammarParser.RhsWithMultipleSeparatorsCheckContext ctx);

    /**
     * Visit a parse tree produced by the {@code RightSideMissingERROR} labeled alternative in
     * {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRightSideMissingERROR(@NotNull GrammarParser.RightSideMissingERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectGrammar} labeled alternative in {@link
     * GrammarParser#initGrammar}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectGrammar(@NotNull GrammarParser.CorrectGrammarContext ctx);

    /**
     * Visit a parse tree produced by {@link GrammarParser#symSepEps}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymSepEps(@NotNull GrammarParser.SymSepEpsContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectSententialForm} labeled alternative in
     * {@link GrammarParser#sententialForm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectSententialForm(@NotNull GrammarParser.CorrectSententialFormContext ctx);

    /**
     * Visit a parse tree produced by the {@code ProductionWithSeparatorRightERROR} labeled
     * alternative in {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProductionWithSeparatorRightERROR(
            @NotNull GrammarParser.ProductionWithSeparatorRightERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectRightSideSeparator} labeled alternative in
     * {@link GrammarParser#rhsSeparators}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectRightSideSeparator(@NotNull GrammarParser.CorrectRightSideSeparatorContext ctx);

    /**
     * Visit a parse tree produced by the {@code NoProductionArrowERROR} labeled alternative in
     * {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNoProductionArrowERROR(@NotNull GrammarParser.NoProductionArrowERRORContext ctx);

    /**
     * Visit a parse tree produced by {@link GrammarParser#entry}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntry(@NotNull GrammarParser.EntryContext ctx);

    /**
     * Visit a parse tree produced by the {@code LeftSideMissingERROR} labeled alternative in {@link
     * GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLeftSideMissingERROR(@NotNull GrammarParser.LeftSideMissingERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code ProductionWithSeparatorLeftERROR} labeled
     * alternative in {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitProductionWithSeparatorLeftERROR(
            @NotNull GrammarParser.ProductionWithSeparatorLeftERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code EmptyGrammarERROR} labeled alternative in {@link
     * GrammarParser#initGrammar}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptyGrammarERROR(@NotNull GrammarParser.EmptyGrammarERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code SymbolEpsilonMixERROR} labeled alternative in
     * {@link GrammarParser#sententialForm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymbolEpsilonMixERROR(@NotNull GrammarParser.SymbolEpsilonMixERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code SententialFormLINK} labeled alternative in {@link
     * GrammarParser#sententialFormOrEmtpy}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSententialFormLINK(@NotNull GrammarParser.SententialFormLINKContext ctx);

    /**
     * Visit a parse tree produced by the {@code Epsilon} labeled alternative in {@link
     * GrammarParser#sententialForm}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEpsilon(@NotNull GrammarParser.EpsilonContext ctx);

    /**
     * Visit a parse tree produced by the {@code BothSidesMissingERROR} labeled alternative in
     * {@link GrammarParser#productionLine}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBothSidesMissingERROR(@NotNull GrammarParser.BothSidesMissingERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code EmptySententialFormERROR} labeled alternative in
     * {@link GrammarParser#sententialFormOrEmtpy}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptySententialFormERROR(@NotNull GrammarParser.EmptySententialFormERRORContext ctx);
}
