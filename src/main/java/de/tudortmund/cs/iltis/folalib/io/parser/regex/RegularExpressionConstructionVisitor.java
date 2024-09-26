package de.tudortmund.cs.iltis.folalib.io.parser.regex;

import de.tudortmund.cs.iltis.folalib.expressions.regular.*;
import de.tudortmund.cs.iltis.folalib.io.parser.regex.fault.RegexParsingFaultReason;
import de.tudortmund.cs.iltis.folalib.languages.Alphabet;
import de.tudortmund.cs.iltis.utils.IndexedSymbol;
import de.tudortmund.cs.iltis.utils.io.parser.error.visitor.VisitorErrorHandler;
import de.tudortmund.cs.iltis.utils.io.parser.general.SymbolToken;
import java.util.Comparator;
import java.util.stream.Collectors;

public class RegularExpressionConstructionVisitor
        extends RegularExpressionParserBaseVisitor<RegularExpression<IndexedSymbol>> {

    private final Alphabet<IndexedSymbol> domainForRange;
    private final Comparator<IndexedSymbol> comparator;

    private final VisitorErrorHandler errorHandler;

    public RegularExpressionConstructionVisitor(
            Alphabet<IndexedSymbol> domainForRange,
            Comparator<IndexedSymbol> comparator,
            VisitorErrorHandler errorHandler) {
        this.domainForRange = domainForRange;
        this.comparator = comparator;
        this.errorHandler = errorHandler;
    }

    /*-----------------------------------------*\
     | Success-Rule-Handling                   |
    \*-----------------------------------------*/

    @Override
    public RegularExpression<IndexedSymbol> visitCorrectOnlyRegex(
            RegularExpressionParser.CorrectOnlyRegexContext ctx) {
        return visit(ctx.regex());
    }

    @Override
    public RegularExpression<IndexedSymbol> visitNesting(
            RegularExpressionParser.NestingContext ctx) {
        return visit(ctx.inner);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitFixedRepetition(
            RegularExpressionParser.FixedRepetitionContext ctx) {
        return repetitionAmountHelper((SymbolToken) ctx.numberOfRepetitions);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRepetitionAmount(
            RegularExpressionParser.RepetitionAmountContext ctx) {
        return repetitionAmountHelper((SymbolToken) ctx.numberOfRepetitions);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRepetitionRange(
            RegularExpressionParser.RepetitionRangeContext ctx) {
        return this.repetitionRangeHelper((SymbolToken) ctx.lower, (SymbolToken) ctx.upper);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRepetitionWithSubexpression(
            RegularExpressionParser.RepetitionWithSubexpressionContext ctx) {
        // visit(ctx.repetition()) will return a dummy-Range-object which holds the correct lower
        // and upper bound.
        // We create a new Range-object with the actual content from visit(ctx.subexpressionWrap()).
        // We need to do
        // this because ANTLR only supports direct left-recursion. Using direct left-recursion in
        // the g4-file would
        // lead to much worse readability and redundancy, so I decided to solve it this way.

        RegularExpression<IndexedSymbol> sub = visit(ctx.subexpressionWrap());

        Repetition<IndexedSymbol> dummyRepetition =
                (Repetition<IndexedSymbol>) visit(ctx.repetition());

        return new Repetition<>(sub, dummyRepetition.getLower(), dummyRepetition.getUpper());
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRange(RegularExpressionParser.RangeContext ctx) {
        if (domainForRange.isEmpty()) {
            errorHandler.reportFault(
                    RegexParsingFaultReason.NO_DOMAIN_FOR_RANGE_DEFINED,
                    ctx.start,
                    "",
                    "No domain for the Range expression defined in the reader properties");
            return new EmptyLanguage<>();
        }

        RegularExpression<IndexedSymbol> lower = visit(ctx.lower);
        RegularExpression<IndexedSymbol> upper = visit(ctx.upper);

        Symbol<IndexedSymbol> lowerSymbol = (Symbol<IndexedSymbol>) lower;
        Symbol<IndexedSymbol> upperSymbol = (Symbol<IndexedSymbol>) upper;

        return Range.from(
                domainForRange, lowerSymbol.getSymbol(), upperSymbol.getSymbol(), comparator);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitEmptySet(
            RegularExpressionParser.EmptySetContext ctx) {
        return new EmptyLanguage<>();
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRegex(RegularExpressionParser.RegexContext ctx) {
        if (ctx.alternationContinuation().isEmpty()) return visit(ctx.concatenation());

        // IMPORTANT: We must use the convenience-method ".concat()" here instead of using the
        // constructor to ensure
        // that the union of all children alphabets gets used. The constructor would simply throw an
        // error in that case.
        return visit(ctx.concatenation())
                .or(
                        ctx.alternationContinuation().stream()
                                .map(this::visit)
                                .collect(Collectors.toList()));
    }

    @Override
    public RegularExpression<IndexedSymbol> visitSymbol(RegularExpressionParser.SymbolContext ctx) {
        IndexedSymbol symbol = ((SymbolToken) ctx.SYMBOL().getSymbol()).getSymbol();
        return new Symbol<>(symbol);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitConcatenation(
            RegularExpressionParser.ConcatenationContext ctx) {
        if (ctx.children.size() == 1) return visit(ctx.children.get(0));

        // IMPORTANT: We must use the convenience-method ".concat()" here instead of using the
        // constructor to ensure
        // that the union of all children alphabets gets used. The constructor would simply throw an
        // error in that case.
        RegularExpression<IndexedSymbol> result = visit(ctx.children.get(0));
        ctx.children.remove(0);
        return result.concat(ctx.children.stream().map(this::visit).collect(Collectors.toList()));
    }

    @Override
    public RegularExpression<IndexedSymbol> visitOptional(
            RegularExpressionParser.OptionalContext ctx) {
        RegularExpression<IndexedSymbol> sub = visit(ctx.subexpressionWrap());

        return new Option<>(sub);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitKleenePlus(
            RegularExpressionParser.KleenePlusContext ctx) {
        RegularExpression<IndexedSymbol> sub = visit(ctx.subexpressionWrap());

        return new KleenePlus<>(sub);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitKleeneStar(
            RegularExpressionParser.KleeneStarContext ctx) {
        RegularExpression<IndexedSymbol> sub = visit(ctx.subexpressionWrap());

        return new KleeneStar<>(sub);
    }

    @Override
    public RegularExpression<IndexedSymbol> visitEpsilon(
            RegularExpressionParser.EpsilonContext ctx) {
        return new EmptyWord<>();
    }

    // The Alternative object is constructed in visitRegex!
    @Override
    public RegularExpression<IndexedSymbol> visitCorrectAlternation(
            RegularExpressionParser.CorrectAlternationContext ctx) {
        return visit(ctx.concatenation());
    }

    /*-----------------------------------------*\
     | Error-Rule-Handling                     |
    \*-----------------------------------------*/

    @Override
    public RegularExpression<IndexedSymbol> visitStartWithAlternationERROR(
            RegularExpressionParser.StartWithAlternationERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.NO_ALTERNATIVE,
                ctx.ALTERNATION(0).getSymbol(),
                "",
                "Missing operand to alternative");

        // Something like "||a b". We will just ignore the leading alternation symbols.
        if (ctx.regex() == null) {
            return new EmptyLanguage<>(); // If there are only alternation symbols.
        }
        return visit(ctx.regex());
    }

    @Override
    public RegularExpression<IndexedSymbol> visitEmptyRegexERROR(
            RegularExpressionParser.EmptyRegexERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.EMPTY_REGEX,
                ctx.EOF().getSymbol(),
                "",
                "Empty regular expression");

        // There is no input. We will return an empty set.
        return new EmptyLanguage<>();
    }

    @Override
    public RegularExpression<IndexedSymbol> visitStartWithCardinalityOperatorERROR(
            RegularExpressionParser.StartWithCardinalityOperatorERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.START_WITH_CARDINALITY_OPERATOR,
                ctx.start,
                "",
                "Start with cardinality operator");

        // Something like "?+{3}a b". We will just ignore the leading cardinality operators.
        if (ctx.regex() == null) {
            return new EmptyLanguage<>(); // If there are only cardinality operators.
        }
        return visit(ctx.regex());
    }

    @Override
    public RegularExpression<IndexedSymbol> visitCardinalityOperatorAfterAlternationERROR(
            RegularExpressionParser.CardinalityOperatorAfterAlternationERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.CARDINALITY_OPERATOR_AFTER_ALTERNATION_SYMBOL,
                ctx.ALTERNATION().getSymbol(),
                "",
                "Cardinality operand after alternative operator");
        if (ctx.concatenation() == null)
            errorHandler.reportFault(
                    RegexParsingFaultReason.NO_ALTERNATIVE,
                    ctx.ALTERNATION().getSymbol(),
                    "",
                    "Missing operand to alternative");

        // Something like "a +* b". Which honestly makes no sense, but we remove the cardinality
        // operator.
        if (ctx.concatenation() != null) return visit(ctx.concatenation());
        else return new EmptyLanguage<>(); // Really going on a limb here
    }

    @Override
    public RegularExpression<IndexedSymbol> visitMissingAlternationOperandERROR(
            RegularExpressionParser.MissingAlternationOperandERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.NO_ALTERNATIVE,
                ctx.ALTERNATION().getSymbol(),
                "",
                "Missing operand to alternative");

        // Something like "a++b". We insert an epsilon in the middle.
        return new EmptyWord<>();
    }

    @Override
    public RegularExpression<IndexedSymbol> visitRepetitionAmbiguousERROR(
            RegularExpressionParser.RepetitionAmbiguousERRORContext ctx) {
        errorHandler.reportFault(
                RegexParsingFaultReason.AMBIGUOUS_REPETITION_DEFINITION,
                ctx.lower,
                "",
                "The given repetition definition is ambiguous due to separation symbol missing");

        return this.repetitionRangeHelper((SymbolToken) ctx.lower, (SymbolToken) ctx.upper);
    }

    /*-----------------------------------------*\
     | Private helper methods                  |
    \*-----------------------------------------*/

    private RegularExpression<IndexedSymbol> repetitionAmountHelper(
            SymbolToken numberOfRepetitions) {
        String tokenOutput = numberOfRepetitions.getSymbol().toString();

        // Create a dummy object because a new one will be created anyway in
        // visitRepetitionWithSubexpression
        // Integer.parseInt cannot fail here because tokenOutput is always numeric. This was already
        // checked in the
        // post-lexer (in the regex reader).
        return new Repetition<>(new EmptyLanguage<>(), Integer.parseInt(tokenOutput));
    }

    private RegularExpression<IndexedSymbol> repetitionRangeHelper(
            SymbolToken lower, SymbolToken upper) {
        String tokenOutputLower = lower.getSymbol().toString();
        String tokenOutputUpper = upper.getSymbol().toString();

        // Create a dummy object because a new one will be created anyway in
        // visitRepetitionWithSubexpression
        // Integer.parseInt cannot fail here because tokenOutput is always numeric. This was already
        // checked in the
        // post-lexer (in the regex reader).
        return new Repetition<>(
                new EmptyLanguage<>(),
                Integer.parseInt(tokenOutputLower),
                Integer.parseInt(tokenOutputUpper));
    }
}
