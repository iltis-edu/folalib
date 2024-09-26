// Generated from
// ../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/regex/RegularExpressionParser.g4 by
// ANTLR 4.4
package de.tudortmund.cs.iltis.folalib.io.parser.regex;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced by {@link
 * RegularExpressionParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for operations with no return
 *     type.
 */
public interface RegularExpressionParserVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link RegularExpressionParser#symbol}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymbol(@NotNull RegularExpressionParser.SymbolContext ctx);

    /**
     * Visit a parse tree produced by the {@code EmptySet} labeled alternative in {@link
     * RegularExpressionParser#subexpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptySet(@NotNull RegularExpressionParser.EmptySetContext ctx);

    /**
     * Visit a parse tree produced by {@link RegularExpressionParser#range}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRange(@NotNull RegularExpressionParser.RangeContext ctx);

    /**
     * Visit a parse tree produced by the {@code MissingAlternationOperandERROR} labeled alternative
     * in {@link RegularExpressionParser#alternationContinuation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMissingAlternationOperandERROR(
            @NotNull RegularExpressionParser.MissingAlternationOperandERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code SubexpressionLINK} labeled alternative in {@link
     * RegularExpressionParser#subexpressionWrap()}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSubexpressionLINK(@NotNull RegularExpressionParser.SubexpressionLINKContext ctx);

    /**
     * Visit a parse tree produced by the {@code SymbolLINK} labeled alternative in {@link
     * RegularExpressionParser#subexpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSymbolLINK(@NotNull RegularExpressionParser.SymbolLINKContext ctx);

    /**
     * Visit a parse tree produced by the {@code RepetitionAmount} labeled alternative in {@link
     * RegularExpressionParser#repetition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRepetitionAmount(@NotNull RegularExpressionParser.RepetitionAmountContext ctx);

    /**
     * Visit a parse tree produced by the {@code CardinalityOperatorAfterAlternationERROR} labeled
     * alternative in {@link RegularExpressionParser#alternationContinuation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCardinalityOperatorAfterAlternationERROR(
            @NotNull RegularExpressionParser.CardinalityOperatorAfterAlternationERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code Nesting} labeled alternative in {@link
     * RegularExpressionParser#subexpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNesting(@NotNull RegularExpressionParser.NestingContext ctx);

    /**
     * Visit a parse tree produced by the {@code Optional} labeled alternative in {@link
     * RegularExpressionParser#subexpressionWrap()}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOptional(@NotNull RegularExpressionParser.OptionalContext ctx);

    /**
     * Visit a parse tree produced by the {@code RepetitionRange} labeled alternative in {@link
     * RegularExpressionParser#repetition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRepetitionRange(@NotNull RegularExpressionParser.RepetitionRangeContext ctx);

    /**
     * Visit a parse tree produced by the {@code EmptyRegexERROR} labeled alternative in {@link
     * RegularExpressionParser#onlyRegex}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEmptyRegexERROR(@NotNull RegularExpressionParser.EmptyRegexERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code StartWithCardinalityOperatorERROR} labeled
     * alternative in {@link RegularExpressionParser#onlyRegex}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStartWithCardinalityOperatorERROR(
            @NotNull RegularExpressionParser.StartWithCardinalityOperatorERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectAlternation} labeled alternative in {@link
     * RegularExpressionParser#alternationContinuation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectAlternation(@NotNull RegularExpressionParser.CorrectAlternationContext ctx);

    /**
     * Visit a parse tree produced by the {@code KleenePlus} labeled alternative in {@link
     * RegularExpressionParser#subexpressionWrap()}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitKleenePlus(@NotNull RegularExpressionParser.KleenePlusContext ctx);

    /**
     * Visit a parse tree produced by the {@code RepetitionWithSubexpression} labeled alternative in
     * {@link RegularExpressionParser#subexpressionWrap()}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRepetitionWithSubexpression(
            @NotNull RegularExpressionParser.RepetitionWithSubexpressionContext ctx);

    /**
     * Visit a parse tree produced by the {@code StartWithAlternationERROR} labeled alternative in
     * {@link RegularExpressionParser#onlyRegex}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStartWithAlternationERROR(
            @NotNull RegularExpressionParser.StartWithAlternationERRORContext ctx);

    /**
     * Visit a parse tree produced by the {@code KleeneStar} labeled alternative in {@link
     * RegularExpressionParser#subexpressionWrap()}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitKleeneStar(@NotNull RegularExpressionParser.KleeneStarContext ctx);

    /**
     * Visit a parse tree produced by {@link RegularExpressionParser#regex}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRegex(@NotNull RegularExpressionParser.RegexContext ctx);

    /**
     * Visit a parse tree produced by {@link RegularExpressionParser#concatenation}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConcatenation(@NotNull RegularExpressionParser.ConcatenationContext ctx);

    /**
     * Visit a parse tree produced by the {@code RangeLINK} labeled alternative in {@link
     * RegularExpressionParser#subexpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRangeLINK(@NotNull RegularExpressionParser.RangeLINKContext ctx);

    /**
     * Visit a parse tree produced by the {@code CorrectOnlyRegex} labeled alternative in {@link
     * RegularExpressionParser#onlyRegex}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCorrectOnlyRegex(@NotNull RegularExpressionParser.CorrectOnlyRegexContext ctx);

    /**
     * Visit a parse tree produced by the {@code FixedRepetition} labeled alternative in {@link
     * RegularExpressionParser#repetition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFixedRepetition(@NotNull RegularExpressionParser.FixedRepetitionContext ctx);

    /**
     * Visit a parse tree produced by the {@code Epsilon} labeled alternative in {@link
     * RegularExpressionParser#subexpression}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEpsilon(@NotNull RegularExpressionParser.EpsilonContext ctx);

    /**
     * Visit a parse tree produced by the {@code RepetitionAmbiguousERROR} labeled alternative in
     * {@link RegularExpressionParser#repetition}.
     *
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitRepetitionAmbiguousERROR(
            @NotNull RegularExpressionParser.RepetitionAmbiguousERRORContext ctx);
}
