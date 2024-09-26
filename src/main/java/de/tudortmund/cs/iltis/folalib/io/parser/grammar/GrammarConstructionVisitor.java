package de.tudortmund.cs.iltis.folalib.io.parser.grammar;

import de.tudortmund.cs.iltis.folalib.grammar.GrammarSymbol;
import de.tudortmund.cs.iltis.folalib.grammar.SententialForm;
import de.tudortmund.cs.iltis.folalib.grammar.production.Production;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.fault.GrammarParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputConversionException;
import de.tudortmund.cs.iltis.folalib.io.parser.grammar.input.converter.InputToGrammarSymbolConverter;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.RegularExpressionParser;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.general.Data;
import de.tudortmund.cs.iltis.utils.io.parser.error.visitor.VisitorErrorHandler;
import de.tudortmund.cs.iltis.utils.io.parser.general.SymbolToken;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.Token;

/**
 * This implementation differs from e.g. the implementation of the {@link RegularExpressionParser}
 * because this uses different return values for every method. The idea is from <a
 * href="https://jakubdziworski.github.io/java/2016/04/01/antlr_visitor_vs_listener.html">here</a>,
 * section "Parsing using Visitor"
 *
 * <p>Every inner class represents one rule in the parser.
 *
 * <p><b>Please note: The return type of this class must be a {@link List} instead of a {@link
 * java.util.Set} because the order of the productions is important.</b>
 */
public class GrammarConstructionVisitor
        extends GrammarParserBaseVisitor<List<Production<IndexedSymbol, IndexedSymbol>>> {

    private final InputToGrammarSymbolConverter inputToGrammarSymbolConverter;
    private final VisitorErrorHandler errorHandler;

    public GrammarConstructionVisitor(
            InputToGrammarSymbolConverter inputToGrammarSymbolConverter,
            VisitorErrorHandler errorHandler) {
        this.inputToGrammarSymbolConverter = inputToGrammarSymbolConverter;
        this.errorHandler = errorHandler;
    }

    @Override
    public List<Production<IndexedSymbol, IndexedSymbol>> visitEntry(
            GrammarParser.EntryContext ctx) {
        return new InitGrammarVisitor().visit(ctx);
    }

    private class InitGrammarVisitor
            extends GrammarParserBaseVisitor<List<Production<IndexedSymbol, IndexedSymbol>>> {
        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitCorrectGrammar(
                GrammarParser.CorrectGrammarContext ctx) {

            // Extracts all productions of all production lines of the input and returns all of them
            // in a single list
            return ctx.lines.stream()
                    .map(productionLine -> new ProductionLineVisitor().visit(productionLine))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        // ERROR RULES:

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitEmptyGrammarERROR(
                GrammarParser.EmptyGrammarERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.EMPTY_GRAMMAR,
                    ctx.EOF().getSymbol(),
                    "",
                    "No productions found");

            // There are no productions
            return new LinkedList<>();
        }
    }

    private class ProductionLineVisitor
            extends GrammarParserBaseVisitor<List<Production<IndexedSymbol, IndexedSymbol>>> {
        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitCorrectProduction(
                GrammarParser.CorrectProductionContext ctx) {
            return productionLineHelper(ctx.lhs, ctx.rhsWithMultipleSeparatorsCheck());
        }

        // ERROR RULES:

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>>
                visitProductionWithSeparatorRightERROR(
                        GrammarParser.ProductionWithSeparatorRightERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR,
                    ctx.stop,
                    "",
                    "Production line ends with right side separator");

            new RhsSeparatorsVisitor().visit(ctx.rhsSeparators());
            return productionLineHelper(ctx.lhs, ctx.rhsWithMultipleSeparatorsCheck());
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitProductionWithSeparatorLeftERROR(
                GrammarParser.ProductionWithSeparatorLeftERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR,
                    ctx.stop,
                    "",
                    "Production line starts with right side separator");

            new RhsSeparatorsVisitor().visit(ctx.rhsSeparators());
            return productionLineHelper(ctx.lhs, ctx.rhsWithMultipleSeparatorsCheck());
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitProductionWithSeparatorBothERROR(
                GrammarParser.ProductionWithSeparatorBothERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.LINE_STARTS_WITH_RIGHT_SIDE_SEPARATOR,
                    ctx.stop,
                    "",
                    "Production line starts with right side separator");
            errorHandler.reportFault(
                    GrammarParsingFaultReason.LINE_ENDS_WITH_RIGHT_SIDE_SEPARATOR,
                    ctx.stop,
                    "",
                    "Production line ends with right side separator");

            RhsSeparatorsVisitor rhsSeparatorsVisitor = new RhsSeparatorsVisitor();
            ctx.rhsSeparators().forEach(rhsSeparatorsVisitor::visit);

            return productionLineHelper(ctx.lhs, ctx.rhsWithMultipleSeparatorsCheck());
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitLeftSideMissingERROR(
                GrammarParser.LeftSideMissingERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.INCOMPLETE_PRODUCTION,
                    ctx.arrow,
                    "",
                    "Left side of production is missing. Use 'ɛ' to specify an empty sentential form");

            if (ctx.rhsSeparators(0) != null)
                new RhsSeparatorsVisitor().visit(ctx.rhsSeparators(0));
            if (ctx.rhsSeparators(1) != null)
                new RhsSeparatorsVisitor().visit(ctx.rhsSeparators(1));

            // We assume the user meant to use epsilon if the LHS is empty
            return new CorrectRhsVisitor()
                    .visit(ctx.rhsWithMultipleSeparatorsCheck()).stream()
                            .map(rhs -> new Production<>(new SententialForm<>(), rhs))
                            .collect(Collectors.toList());
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitRightSideMissingERROR(
                GrammarParser.RightSideMissingERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.INCOMPLETE_PRODUCTION,
                    ctx.arrow,
                    "",
                    "Right side of production is missing. Use 'ɛ' to specify an empty sentential form");

            if (ctx.rhsSeparators() != null) new RhsSeparatorsVisitor().visit(ctx.rhsSeparators());

            // We assume the user meant to use epsilon if the RHS is empty
            return Data.newArrayList(
                    new Production<>(
                            new SententialFormVisitor(inputToGrammarSymbolConverter, errorHandler)
                                    .visit(ctx.lhs),
                            new SententialForm<>()));
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitBothSidesMissingERROR(
                GrammarParser.BothSidesMissingERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.INCOMPLETE_PRODUCTION,
                    ctx.arrow,
                    "",
                    "Right side and left side of production are missing. Use 'ɛ' to specify an empty sentential form");

            if (ctx.rhsSeparators() != null) new RhsSeparatorsVisitor().visit(ctx.rhsSeparators());

            // We assume the user meant to use epsilon if the RHS and the LHS are empty
            return Data.newArrayList(
                    new Production<>(new SententialForm<>(), new SententialForm<>()));
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitRightSideSeparatorOnLhsERROR(
                GrammarParser.RightSideSeparatorOnLhsERRORContext ctx) {
            // Having the RHS-separator on a lhs is so terribly wrong that we bail out here
            errorHandler.reportFaultAndAlwaysBailOut(
                    GrammarParsingFaultReason.RIGHT_SIDE_SEPARATOR_ON_LHS,
                    ctx.errorSeparator,
                    "",
                    "Right side separator occurs on lhs of production");

            return null;
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitMultipleProductionArrowsERROR(
                GrammarParser.MultipleProductionArrowsERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.MULTIPLE_PRODUCTION_ARROWS,
                    ctx.second,
                    "",
                    "Multiple production arrows in production line");

            // We do not add this production to the grammar
            return new LinkedList<>();
        }

        @Override
        public List<Production<IndexedSymbol, IndexedSymbol>> visitNoProductionArrowERROR(
                GrammarParser.NoProductionArrowERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.MISSING_PRODUCTION_ARROW,
                    ctx.arbitrarySymbols.start,
                    "",
                    "Production line is missing a production arrow");

            // We do not add this production to the grammar
            return new LinkedList<>();
        }

        // HELPER:

        private List<Production<IndexedSymbol, IndexedSymbol>> productionLineHelper(
                GrammarParser.SententialFormContext lhsCtx,
                GrammarParser.RhsWithMultipleSeparatorsCheckContext rhssCtx) {

            SententialForm<IndexedSymbol, IndexedSymbol> lhs =
                    new SententialFormVisitor(inputToGrammarSymbolConverter, errorHandler)
                            .visit(lhsCtx);

            return new CorrectRhsVisitor()
                    .visit(rhssCtx).stream()
                            .map(rhs -> new Production<>(lhs, rhs))
                            .collect(Collectors.toList());
        }
    }

    private class CorrectRhsVisitor
            extends GrammarParserBaseVisitor<List<SententialForm<IndexedSymbol, IndexedSymbol>>> {
        @Override
        public List<SententialForm<IndexedSymbol, IndexedSymbol>>
                visitRhsWithMultipleSeparatorsCheck(
                        GrammarParser.RhsWithMultipleSeparatorsCheckContext ctx) {
            RhsSeparatorsVisitor rhsSeparatorsVisitor = new RhsSeparatorsVisitor();
            ctx.rhsSeparators().forEach(rhsSeparatorsVisitor::visit);

            return ctx.rhs.stream()
                    .map(
                            rhs ->
                                    new SententialFormVisitor(
                                                    inputToGrammarSymbolConverter, errorHandler)
                                            .visit(rhs))
                    .collect(Collectors.toList());
        }
    }

    private class RhsSeparatorsVisitor extends GrammarParserBaseVisitor<Void> {
        // ERROR RULES:

        @Override
        public Void visitAbundantRightSideSeparatorsERROR(
                GrammarParser.AbundantRightSideSeparatorsERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.ABUNDANT_RIGHT_SIDE_SEPARATORS,
                    ctx.second,
                    "",
                    "Multiple consecutive right side separators");

            return null;
        }
    }

    // public static to allow the SententialFormReader to use this visitor
    public static class SententialFormVisitor
            extends GrammarParserBaseVisitor<SententialForm<IndexedSymbol, IndexedSymbol>> {

        private InputToGrammarSymbolConverter inputToGrammarSymbolConverter;
        private VisitorErrorHandler errorHandler;

        public SententialFormVisitor(
                InputToGrammarSymbolConverter inputToGrammarSymbolConverter,
                VisitorErrorHandler errorHandler) {
            this.inputToGrammarSymbolConverter = inputToGrammarSymbolConverter;
            this.errorHandler = errorHandler;
        }

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitCorrectSententialForm(
                GrammarParser.CorrectSententialFormContext ctx) {
            return sententialFormBuilderHelper(ctx.symbols);
        }

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitEpsilon(
                GrammarParser.EpsilonContext ctx) {
            return new SententialForm<>();
        }

        // ERROR RULES:

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitAbundantEpsilonERROR(
                GrammarParser.AbundantEpsilonERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.ABUNDANT_EPSILONS,
                    ctx.second,
                    "",
                    "Multiple consecutive epsilons");

            return new SententialForm<>();
        }

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitSymbolEpsilonMixERROR(
                GrammarParser.SymbolEpsilonMixERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.SYMBOL_EPSILON_MIX,
                    ctx.start,
                    "",
                    "Sentential form consists of both symbol(s) and epsilon(s)");

            // We simply remove all epsilons from the sentential form and try to parse all symbols
            return sententialFormBuilderHelper(ctx.symbols);
        }

        // HELPER:

        private SententialForm<IndexedSymbol, IndexedSymbol> sententialFormBuilderHelper(
                List<Token> tokens) {
            List<GrammarSymbol<IndexedSymbol, IndexedSymbol>> convertedSymbols = new LinkedList<>();

            for (Token token : tokens) {
                // We know that those must be SymbolTokens and therefore IndexedSymbols
                IndexedSymbol symbol = ((SymbolToken) token).getSymbol();

                try {
                    convertedSymbols.add(inputToGrammarSymbolConverter.convertInput(symbol));
                } catch (InputConversionException e) {
                    errorHandler.reportFault(
                            GrammarParsingFaultReason.AMBIGUOUS_SYMBOL, token, "", e.getMessage());
                }
            }
            // The mappedSymbols list may be empty if only AMBIGUOUS_SYMBOLS were read. We cannot
            // return an empty
            // sentential form here because that would be equal to epsilon. This is not allowed
            // here. Thus, end parsing.
            if (convertedSymbols.isEmpty())
                errorHandler.reportFaultAndAlwaysBailOut(
                        GrammarParsingFaultReason.UNKNOWN_SENTENTIAL_FORM,
                        tokens.get(0),
                        "",
                        "Sentential form only consists of ambiguous symbols");

            return new SententialForm<>(convertedSymbols);
        }

        // FOR SententialFormReader:

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitSententialFormLINK(
                GrammarParser.SententialFormLINKContext ctx) {
            return visit(ctx.sententialForm());
        }

        @Override
        public SententialForm<IndexedSymbol, IndexedSymbol> visitEmptySententialFormERROR(
                GrammarParser.EmptySententialFormERRORContext ctx) {
            errorHandler.reportFault(
                    GrammarParsingFaultReason.BLANK_INPUT,
                    ctx.start,
                    "",
                    "No symbols found. Use 'ɛ' to specify an empty sentential form");

            return new SententialForm<>();
        }
    }
}
