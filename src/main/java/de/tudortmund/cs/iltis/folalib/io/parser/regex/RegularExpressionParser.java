// Generated from
// ../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/regex/RegularExpressionParser.g4 by
// ANTLR 4.4
package de.tudortmund.cs.iltis.folalib.io.parser.regex;

import de.tudortmund.cs.iltis.utils.io.parser.general.AbstractParser;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RegularExpressionParser extends AbstractParser {
    static {
        RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int OPENING_PARENTHESIS = 1,
            CLOSING_PARENTHESIS = 2,
            OPENING_REPETITION = 3,
            CLOSING_REPETITION = 4,
            FIXED_REPETITION_OPENER = 5,
            OPENING_RANGE = 6,
            CLOSING_RANGE = 7,
            REPETITION_SEPARATOR = 8,
            EPSILON = 9,
            SYMBOL = 10,
            EMPTY_SET = 11,
            WORD = 12,
            REPETITION_OPERAND = 13,
            ALTERNATION = 14,
            KLEENE_STAR = 15,
            KLEENE_PLUS = 16,
            OPTIONAL = 17,
            RANGE_SEPARATOR = 18,
            WHITESPACE = 19;
    public static final String[] tokenNames = {
        "<INVALID>",
        "OPENING_PARENTHESIS",
        "CLOSING_PARENTHESIS",
        "OPENING_REPETITION",
        "CLOSING_REPETITION",
        "FIXED_REPETITION_OPENER",
        "OPENING_RANGE",
        "CLOSING_RANGE",
        "REPETITION_SEPARATOR",
        "EPSILON",
        "SYMBOL",
        "EMPTY_SET",
        "WORD",
        "REPETITION_OPERAND",
        "ALTERNATION",
        "KLEENE_STAR",
        "KLEENE_PLUS",
        "OPTIONAL",
        "RANGE_SEPARATOR",
        "WHITESPACE"
    };
    public static final int RULE_onlyRegex = 0,
            RULE_regex = 1,
            RULE_alternationContinuation = 2,
            RULE_concatenation = 3,
            RULE_subexpressionWrap = 4,
            RULE_repetition = 5,
            RULE_subexpression = 6,
            RULE_range = 7,
            RULE_symbol = 8;
    public static final String[] ruleNames = {
        "onlyRegex",
        "regex",
        "alternationContinuation",
        "concatenation",
        "subexpressionWrap",
        "repetition",
        "subexpression",
        "range",
        "symbol"
    };

    @Override
    public String getGrammarFileName() {
        return "RegularExpressionParser.g4";
    }

    @Override
    public String[] getTokenNames() {
        return tokenNames;
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public RegularExpressionParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class OnlyRegexContext extends ParserRuleContext {
        public OnlyRegexContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_onlyRegex;
        }

        public OnlyRegexContext() {}

        public void copyFrom(OnlyRegexContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class EmptyRegexERRORContext extends OnlyRegexContext {
        public TerminalNode EOF() {
            return getToken(RegularExpressionParser.EOF, 0);
        }

        public EmptyRegexERRORContext(OnlyRegexContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitEmptyRegexERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CorrectOnlyRegexContext extends OnlyRegexContext {
        public TerminalNode EOF() {
            return getToken(RegularExpressionParser.EOF, 0);
        }

        public RegexContext regex() {
            return getRuleContext(RegexContext.class, 0);
        }

        public CorrectOnlyRegexContext(OnlyRegexContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitCorrectOnlyRegex(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class StartWithCardinalityOperatorERRORContext extends OnlyRegexContext {
        public TerminalNode KLEENE_STAR(int i) {
            return getToken(RegularExpressionParser.KLEENE_STAR, i);
        }

        public List<TerminalNode> KLEENE_STAR() {
            return getTokens(RegularExpressionParser.KLEENE_STAR);
        }

        public List<TerminalNode> KLEENE_PLUS() {
            return getTokens(RegularExpressionParser.KLEENE_PLUS);
        }

        public TerminalNode EOF() {
            return getToken(RegularExpressionParser.EOF, 0);
        }

        public TerminalNode KLEENE_PLUS(int i) {
            return getToken(RegularExpressionParser.KLEENE_PLUS, i);
        }

        public List<TerminalNode> OPTIONAL() {
            return getTokens(RegularExpressionParser.OPTIONAL);
        }

        public RegexContext regex() {
            return getRuleContext(RegexContext.class, 0);
        }

        public TerminalNode OPTIONAL(int i) {
            return getToken(RegularExpressionParser.OPTIONAL, i);
        }

        public RepetitionContext repetition(int i) {
            return getRuleContext(RepetitionContext.class, i);
        }

        public List<RepetitionContext> repetition() {
            return getRuleContexts(RepetitionContext.class);
        }

        public StartWithCardinalityOperatorERRORContext(OnlyRegexContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitStartWithCardinalityOperatorERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class StartWithAlternationERRORContext extends OnlyRegexContext {
        public TerminalNode EOF() {
            return getToken(RegularExpressionParser.EOF, 0);
        }

        public TerminalNode ALTERNATION(int i) {
            return getToken(RegularExpressionParser.ALTERNATION, i);
        }

        public RegexContext regex() {
            return getRuleContext(RegexContext.class, 0);
        }

        public List<TerminalNode> ALTERNATION() {
            return getTokens(RegularExpressionParser.ALTERNATION);
        }

        public StartWithAlternationERRORContext(OnlyRegexContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitStartWithAlternationERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public final OnlyRegexContext onlyRegex() throws RecognitionException {
        OnlyRegexContext _localctx = new OnlyRegexContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_onlyRegex);
        int _la;
        try {
            setState(43);
            switch (_input.LA(1)) {
                case OPENING_PARENTHESIS:
                case OPENING_RANGE:
                case EPSILON:
                case SYMBOL:
                case EMPTY_SET:
                    _localctx = new CorrectOnlyRegexContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(18);
                        regex();
                        setState(19);
                        match(EOF);
                    }
                    break;
                case ALTERNATION:
                    _localctx = new StartWithAlternationERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(22);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(21);
                                    match(ALTERNATION);
                                }
                            }
                            setState(24);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == ALTERNATION);
                        setState(27);
                        _la = _input.LA(1);
                        if ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << OPENING_PARENTHESIS)
                                                        | (1L << OPENING_RANGE)
                                                        | (1L << EPSILON)
                                                        | (1L << SYMBOL)
                                                        | (1L << EMPTY_SET)))
                                        != 0)) {
                            {
                                setState(26);
                                regex();
                            }
                        }

                        setState(29);
                        match(EOF);
                    }
                    break;
                case OPENING_REPETITION:
                case FIXED_REPETITION_OPENER:
                case KLEENE_STAR:
                case KLEENE_PLUS:
                case OPTIONAL:
                    _localctx = new StartWithCardinalityOperatorERRORContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(34);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                setState(34);
                                switch (_input.LA(1)) {
                                    case KLEENE_STAR:
                                        {
                                            setState(30);
                                            match(KLEENE_STAR);
                                        }
                                        break;
                                    case KLEENE_PLUS:
                                        {
                                            setState(31);
                                            match(KLEENE_PLUS);
                                        }
                                        break;
                                    case OPTIONAL:
                                        {
                                            setState(32);
                                            match(OPTIONAL);
                                        }
                                        break;
                                    case OPENING_REPETITION:
                                    case FIXED_REPETITION_OPENER:
                                        {
                                            setState(33);
                                            repetition();
                                        }
                                        break;
                                    default:
                                        throw new NoViableAltException(this);
                                }
                            }
                            setState(36);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << OPENING_REPETITION)
                                                        | (1L << FIXED_REPETITION_OPENER)
                                                        | (1L << KLEENE_STAR)
                                                        | (1L << KLEENE_PLUS)
                                                        | (1L << OPTIONAL)))
                                        != 0));
                        setState(39);
                        _la = _input.LA(1);
                        if ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << OPENING_PARENTHESIS)
                                                        | (1L << OPENING_RANGE)
                                                        | (1L << EPSILON)
                                                        | (1L << SYMBOL)
                                                        | (1L << EMPTY_SET)))
                                        != 0)) {
                            {
                                setState(38);
                                regex();
                            }
                        }

                        setState(41);
                        match(EOF);
                    }
                    break;
                case EOF:
                    _localctx = new EmptyRegexERRORContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(42);
                        match(EOF);
                    }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class RegexContext extends ParserRuleContext {
        public List<AlternationContinuationContext> alternationContinuation() {
            return getRuleContexts(AlternationContinuationContext.class);
        }

        public ConcatenationContext concatenation() {
            return getRuleContext(ConcatenationContext.class, 0);
        }

        public AlternationContinuationContext alternationContinuation(int i) {
            return getRuleContext(AlternationContinuationContext.class, i);
        }

        public RegexContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_regex;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitRegex(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RegexContext regex() throws RecognitionException {
        RegexContext _localctx = new RegexContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_regex);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(45);
                concatenation();
                setState(49);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == ALTERNATION) {
                    {
                        {
                            setState(46);
                            alternationContinuation();
                        }
                    }
                    setState(51);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class AlternationContinuationContext extends ParserRuleContext {
        public AlternationContinuationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_alternationContinuation;
        }

        public AlternationContinuationContext() {}

        public void copyFrom(AlternationContinuationContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class CorrectAlternationContext extends AlternationContinuationContext {
        public ConcatenationContext concatenation() {
            return getRuleContext(ConcatenationContext.class, 0);
        }

        public TerminalNode ALTERNATION() {
            return getToken(RegularExpressionParser.ALTERNATION, 0);
        }

        public CorrectAlternationContext(AlternationContinuationContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitCorrectAlternation(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class MissingAlternationOperandERRORContext
            extends AlternationContinuationContext {
        public TerminalNode ALTERNATION() {
            return getToken(RegularExpressionParser.ALTERNATION, 0);
        }

        public MissingAlternationOperandERRORContext(AlternationContinuationContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitMissingAlternationOperandERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CardinalityOperatorAfterAlternationERRORContext
            extends AlternationContinuationContext {
        public TerminalNode KLEENE_STAR(int i) {
            return getToken(RegularExpressionParser.KLEENE_STAR, i);
        }

        public List<TerminalNode> KLEENE_STAR() {
            return getTokens(RegularExpressionParser.KLEENE_STAR);
        }

        public List<TerminalNode> KLEENE_PLUS() {
            return getTokens(RegularExpressionParser.KLEENE_PLUS);
        }

        public TerminalNode KLEENE_PLUS(int i) {
            return getToken(RegularExpressionParser.KLEENE_PLUS, i);
        }

        public List<TerminalNode> OPTIONAL() {
            return getTokens(RegularExpressionParser.OPTIONAL);
        }

        public ConcatenationContext concatenation() {
            return getRuleContext(ConcatenationContext.class, 0);
        }

        public TerminalNode OPTIONAL(int i) {
            return getToken(RegularExpressionParser.OPTIONAL, i);
        }

        public TerminalNode ALTERNATION() {
            return getToken(RegularExpressionParser.ALTERNATION, 0);
        }

        public RepetitionContext repetition(int i) {
            return getRuleContext(RepetitionContext.class, i);
        }

        public List<RepetitionContext> repetition() {
            return getRuleContexts(RepetitionContext.class);
        }

        public CardinalityOperatorAfterAlternationERRORContext(AlternationContinuationContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitCardinalityOperatorAfterAlternationERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public final AlternationContinuationContext alternationContinuation()
            throws RecognitionException {
        AlternationContinuationContext _localctx =
                new AlternationContinuationContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_alternationContinuation);
        int _la;
        try {
            setState(67);
            switch (getInterpreter().adaptivePredict(_input, 10, _ctx)) {
                case 1:
                    _localctx = new CorrectAlternationContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(52);
                        match(ALTERNATION);
                        setState(53);
                        concatenation();
                    }
                    break;
                case 2:
                    _localctx = new MissingAlternationOperandERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(54);
                        match(ALTERNATION);
                    }
                    break;
                case 3:
                    _localctx = new CardinalityOperatorAfterAlternationERRORContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(55);
                        match(ALTERNATION);
                        setState(60);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                setState(60);
                                switch (_input.LA(1)) {
                                    case KLEENE_STAR:
                                        {
                                            setState(56);
                                            match(KLEENE_STAR);
                                        }
                                        break;
                                    case KLEENE_PLUS:
                                        {
                                            setState(57);
                                            match(KLEENE_PLUS);
                                        }
                                        break;
                                    case OPTIONAL:
                                        {
                                            setState(58);
                                            match(OPTIONAL);
                                        }
                                        break;
                                    case OPENING_REPETITION:
                                    case FIXED_REPETITION_OPENER:
                                        {
                                            setState(59);
                                            repetition();
                                        }
                                        break;
                                    default:
                                        throw new NoViableAltException(this);
                                }
                            }
                            setState(62);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << OPENING_REPETITION)
                                                        | (1L << FIXED_REPETITION_OPENER)
                                                        | (1L << KLEENE_STAR)
                                                        | (1L << KLEENE_PLUS)
                                                        | (1L << OPTIONAL)))
                                        != 0));
                        setState(65);
                        _la = _input.LA(1);
                        if ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << OPENING_PARENTHESIS)
                                                        | (1L << OPENING_RANGE)
                                                        | (1L << EPSILON)
                                                        | (1L << SYMBOL)
                                                        | (1L << EMPTY_SET)))
                                        != 0)) {
                            {
                                setState(64);
                                concatenation();
                            }
                        }
                    }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class ConcatenationContext extends ParserRuleContext {
        public SubexpressionWrapContext subexpressionWrap(int i) {
            return getRuleContext(SubexpressionWrapContext.class, i);
        }

        public List<SubexpressionWrapContext> subexpressionWrap() {
            return getRuleContexts(SubexpressionWrapContext.class);
        }

        public ConcatenationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_concatenation;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitConcatenation(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ConcatenationContext concatenation() throws RecognitionException {
        ConcatenationContext _localctx = new ConcatenationContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_concatenation);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(70);
                _errHandler.sync(this);
                _la = _input.LA(1);
                do {
                    {
                        {
                            setState(69);
                            subexpressionWrap(0);
                        }
                    }
                    setState(72);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                } while ((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << OPENING_PARENTHESIS)
                                                | (1L << OPENING_RANGE)
                                                | (1L << EPSILON)
                                                | (1L << SYMBOL)
                                                | (1L << EMPTY_SET)))
                                != 0));
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SubexpressionWrapContext extends ParserRuleContext {
        public SubexpressionWrapContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subexpressionWrap;
        }

        public SubexpressionWrapContext() {}

        public void copyFrom(SubexpressionWrapContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class KleeneStarContext extends SubexpressionWrapContext {
        public TerminalNode KLEENE_STAR() {
            return getToken(RegularExpressionParser.KLEENE_STAR, 0);
        }

        public SubexpressionWrapContext subexpressionWrap() {
            return getRuleContext(SubexpressionWrapContext.class, 0);
        }

        public KleeneStarContext(SubexpressionWrapContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitKleeneStar(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class SubexpressionLINKContext extends SubexpressionWrapContext {
        public SubexpressionContext subexpression() {
            return getRuleContext(SubexpressionContext.class, 0);
        }

        public SubexpressionLINKContext(SubexpressionWrapContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitSubexpressionLINK(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class OptionalContext extends SubexpressionWrapContext {
        public TerminalNode OPTIONAL() {
            return getToken(RegularExpressionParser.OPTIONAL, 0);
        }

        public SubexpressionWrapContext subexpressionWrap() {
            return getRuleContext(SubexpressionWrapContext.class, 0);
        }

        public OptionalContext(SubexpressionWrapContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitOptional(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class KleenePlusContext extends SubexpressionWrapContext {
        public TerminalNode KLEENE_PLUS() {
            return getToken(RegularExpressionParser.KLEENE_PLUS, 0);
        }

        public SubexpressionWrapContext subexpressionWrap() {
            return getRuleContext(SubexpressionWrapContext.class, 0);
        }

        public KleenePlusContext(SubexpressionWrapContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitKleenePlus(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RepetitionWithSubexpressionContext extends SubexpressionWrapContext {
        public SubexpressionWrapContext subexpressionWrap() {
            return getRuleContext(SubexpressionWrapContext.class, 0);
        }

        public RepetitionContext repetition() {
            return getRuleContext(RepetitionContext.class, 0);
        }

        public RepetitionWithSubexpressionContext(SubexpressionWrapContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitRepetitionWithSubexpression(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SubexpressionWrapContext subexpressionWrap() throws RecognitionException {
        return subexpressionWrap(0);
    }

    private SubexpressionWrapContext subexpressionWrap(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = _ctx;
        int _parentState = getState();
        SubexpressionWrapContext _localctx = new SubexpressionWrapContext(_ctx, _parentState);
        SubexpressionWrapContext _prevctx = _localctx;
        int _startState = 8;
        enterRecursionRule(_localctx, 8, RULE_subexpressionWrap, _p);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                {
                    _localctx = new SubexpressionLINKContext(_localctx);
                    _ctx = _localctx;
                    _prevctx = _localctx;

                    setState(75);
                    subexpression();
                }
                _ctx.stop = _input.LT(-1);
                setState(87);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(85);
                            switch (getInterpreter().adaptivePredict(_input, 12, _ctx)) {
                                case 1:
                                    {
                                        _localctx =
                                                new KleeneStarContext(
                                                        new SubexpressionWrapContext(
                                                                _parentctx, _parentState));
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_subexpressionWrap);
                                        setState(77);
                                        if (!(precpred(_ctx, 5)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 5)");
                                        setState(78);
                                        match(KLEENE_STAR);
                                    }
                                    break;
                                case 2:
                                    {
                                        _localctx =
                                                new KleenePlusContext(
                                                        new SubexpressionWrapContext(
                                                                _parentctx, _parentState));
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_subexpressionWrap);
                                        setState(79);
                                        if (!(precpred(_ctx, 4)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 4)");
                                        setState(80);
                                        match(KLEENE_PLUS);
                                    }
                                    break;
                                case 3:
                                    {
                                        _localctx =
                                                new OptionalContext(
                                                        new SubexpressionWrapContext(
                                                                _parentctx, _parentState));
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_subexpressionWrap);
                                        setState(81);
                                        if (!(precpred(_ctx, 3)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 3)");
                                        setState(82);
                                        match(OPTIONAL);
                                    }
                                    break;
                                case 4:
                                    {
                                        _localctx =
                                                new RepetitionWithSubexpressionContext(
                                                        new SubexpressionWrapContext(
                                                                _parentctx, _parentState));
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_subexpressionWrap);
                                        setState(83);
                                        if (!(precpred(_ctx, 2)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 2)");
                                        setState(84);
                                        repetition();
                                    }
                                    break;
                            }
                        }
                    }
                    setState(89);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 13, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public static class RepetitionContext extends ParserRuleContext {
        public RepetitionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_repetition;
        }

        public RepetitionContext() {}

        public void copyFrom(RepetitionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class RepetitionRangeContext extends RepetitionContext {
        public Token lower;
        public Token upper;

        public TerminalNode CLOSING_REPETITION() {
            return getToken(RegularExpressionParser.CLOSING_REPETITION, 0);
        }

        public TerminalNode REPETITION_SEPARATOR() {
            return getToken(RegularExpressionParser.REPETITION_SEPARATOR, 0);
        }

        public TerminalNode OPENING_REPETITION() {
            return getToken(RegularExpressionParser.OPENING_REPETITION, 0);
        }

        public List<TerminalNode> REPETITION_OPERAND() {
            return getTokens(RegularExpressionParser.REPETITION_OPERAND);
        }

        public TerminalNode REPETITION_OPERAND(int i) {
            return getToken(RegularExpressionParser.REPETITION_OPERAND, i);
        }

        public RepetitionRangeContext(RepetitionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitRepetitionRange(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class FixedRepetitionContext extends RepetitionContext {
        public Token numberOfRepetitions;

        public TerminalNode FIXED_REPETITION_OPENER() {
            return getToken(RegularExpressionParser.FIXED_REPETITION_OPENER, 0);
        }

        public TerminalNode REPETITION_OPERAND() {
            return getToken(RegularExpressionParser.REPETITION_OPERAND, 0);
        }

        public FixedRepetitionContext(RepetitionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitFixedRepetition(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RepetitionAmbiguousERRORContext extends RepetitionContext {
        public Token lower;
        public Token upper;

        public TerminalNode CLOSING_REPETITION() {
            return getToken(RegularExpressionParser.CLOSING_REPETITION, 0);
        }

        public TerminalNode OPENING_REPETITION() {
            return getToken(RegularExpressionParser.OPENING_REPETITION, 0);
        }

        public List<TerminalNode> REPETITION_OPERAND() {
            return getTokens(RegularExpressionParser.REPETITION_OPERAND);
        }

        public TerminalNode REPETITION_OPERAND(int i) {
            return getToken(RegularExpressionParser.REPETITION_OPERAND, i);
        }

        public RepetitionAmbiguousERRORContext(RepetitionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitRepetitionAmbiguousERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RepetitionAmountContext extends RepetitionContext {
        public Token numberOfRepetitions;

        public TerminalNode CLOSING_REPETITION() {
            return getToken(RegularExpressionParser.CLOSING_REPETITION, 0);
        }

        public TerminalNode OPENING_REPETITION() {
            return getToken(RegularExpressionParser.OPENING_REPETITION, 0);
        }

        public TerminalNode REPETITION_OPERAND() {
            return getToken(RegularExpressionParser.REPETITION_OPERAND, 0);
        }

        public RepetitionAmountContext(RepetitionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitRepetitionAmount(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RepetitionContext repetition() throws RecognitionException {
        RepetitionContext _localctx = new RepetitionContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_repetition);
        try {
            setState(104);
            switch (getInterpreter().adaptivePredict(_input, 14, _ctx)) {
                case 1:
                    _localctx = new FixedRepetitionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(90);
                        match(FIXED_REPETITION_OPENER);
                        setState(91);
                        ((FixedRepetitionContext) _localctx).numberOfRepetitions =
                                match(REPETITION_OPERAND);
                    }
                    break;
                case 2:
                    _localctx = new RepetitionAmountContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(92);
                        match(OPENING_REPETITION);
                        setState(93);
                        ((RepetitionAmountContext) _localctx).numberOfRepetitions =
                                match(REPETITION_OPERAND);
                        setState(94);
                        match(CLOSING_REPETITION);
                    }
                    break;
                case 3:
                    _localctx = new RepetitionRangeContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(95);
                        match(OPENING_REPETITION);
                        setState(96);
                        ((RepetitionRangeContext) _localctx).lower = match(REPETITION_OPERAND);
                        setState(97);
                        match(REPETITION_SEPARATOR);
                        setState(98);
                        ((RepetitionRangeContext) _localctx).upper = match(REPETITION_OPERAND);
                        setState(99);
                        match(CLOSING_REPETITION);
                    }
                    break;
                case 4:
                    _localctx = new RepetitionAmbiguousERRORContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(100);
                        match(OPENING_REPETITION);
                        setState(101);
                        ((RepetitionAmbiguousERRORContext) _localctx).lower =
                                match(REPETITION_OPERAND);
                        setState(102);
                        ((RepetitionAmbiguousERRORContext) _localctx).upper =
                                match(REPETITION_OPERAND);
                        setState(103);
                        match(CLOSING_REPETITION);
                    }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SubexpressionContext extends ParserRuleContext {
        public SubexpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_subexpression;
        }

        public SubexpressionContext() {}

        public void copyFrom(SubexpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class NestingContext extends SubexpressionContext {
        public RegexContext inner;

        public TerminalNode OPENING_PARENTHESIS() {
            return getToken(RegularExpressionParser.OPENING_PARENTHESIS, 0);
        }

        public TerminalNode CLOSING_PARENTHESIS() {
            return getToken(RegularExpressionParser.CLOSING_PARENTHESIS, 0);
        }

        public RegexContext regex() {
            return getRuleContext(RegexContext.class, 0);
        }

        public NestingContext(SubexpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitNesting(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RangeLINKContext extends SubexpressionContext {
        public RangeContext range() {
            return getRuleContext(RangeContext.class, 0);
        }

        public RangeLINKContext(SubexpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitRangeLINK(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class EmptySetContext extends SubexpressionContext {
        public TerminalNode EMPTY_SET() {
            return getToken(RegularExpressionParser.EMPTY_SET, 0);
        }

        public EmptySetContext(SubexpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitEmptySet(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class EpsilonContext extends SubexpressionContext {
        public TerminalNode EPSILON() {
            return getToken(RegularExpressionParser.EPSILON, 0);
        }

        public EpsilonContext(SubexpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitEpsilon(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class SymbolLINKContext extends SubexpressionContext {
        public SymbolContext symbol() {
            return getRuleContext(SymbolContext.class, 0);
        }

        public SymbolLINKContext(SubexpressionContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor)
                        .visitSymbolLINK(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SubexpressionContext subexpression() throws RecognitionException {
        SubexpressionContext _localctx = new SubexpressionContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_subexpression);
        try {
            setState(114);
            switch (_input.LA(1)) {
                case OPENING_PARENTHESIS:
                    _localctx = new NestingContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(106);
                        match(OPENING_PARENTHESIS);
                        setState(107);
                        ((NestingContext) _localctx).inner = regex();
                        setState(108);
                        match(CLOSING_PARENTHESIS);
                    }
                    break;
                case EPSILON:
                    _localctx = new EpsilonContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(110);
                        match(EPSILON);
                    }
                    break;
                case SYMBOL:
                    _localctx = new SymbolLINKContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(111);
                        symbol();
                    }
                    break;
                case OPENING_RANGE:
                    _localctx = new RangeLINKContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(112);
                        range();
                    }
                    break;
                case EMPTY_SET:
                    _localctx = new EmptySetContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                    {
                        setState(113);
                        match(EMPTY_SET);
                    }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class RangeContext extends ParserRuleContext {
        public SymbolContext lower;
        public SymbolContext upper;

        public SymbolContext symbol(int i) {
            return getRuleContext(SymbolContext.class, i);
        }

        public List<SymbolContext> symbol() {
            return getRuleContexts(SymbolContext.class);
        }

        public TerminalNode RANGE_SEPARATOR() {
            return getToken(RegularExpressionParser.RANGE_SEPARATOR, 0);
        }

        public TerminalNode CLOSING_RANGE() {
            return getToken(RegularExpressionParser.CLOSING_RANGE, 0);
        }

        public TerminalNode OPENING_RANGE() {
            return getToken(RegularExpressionParser.OPENING_RANGE, 0);
        }

        public RangeContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_range;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitRange(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RangeContext range() throws RecognitionException {
        RangeContext _localctx = new RangeContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_range);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(116);
                match(OPENING_RANGE);
                setState(117);
                ((RangeContext) _localctx).lower = symbol();
                setState(118);
                match(RANGE_SEPARATOR);
                setState(119);
                ((RangeContext) _localctx).upper = symbol();
                setState(120);
                match(CLOSING_RANGE);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public static class SymbolContext extends ParserRuleContext {
        public TerminalNode SYMBOL() {
            return getToken(RegularExpressionParser.SYMBOL, 0);
        }

        public SymbolContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_symbol;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof RegularExpressionParserVisitor)
                return ((RegularExpressionParserVisitor<? extends T>) visitor).visitSymbol(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SymbolContext symbol() throws RecognitionException {
        SymbolContext _localctx = new SymbolContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_symbol);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(122);
                match(SYMBOL);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 4:
                return subexpressionWrap_sempred((SubexpressionWrapContext) _localctx, predIndex);
        }
        return true;
    }

    private boolean subexpressionWrap_sempred(SubexpressionWrapContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(_ctx, 5);
            case 1:
                return precpred(_ctx, 4);
            case 2:
                return precpred(_ctx, 3);
            case 3:
                return precpred(_ctx, 2);
        }
        return true;
    }

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\25\177\4\2\t\2\4"
                    + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"
                    + "\3\2\3\2\6\2\31\n\2\r\2\16\2\32\3\2\5\2\36\n\2\3\2\3\2\3\2\3\2\3\2\6\2"
                    + "%\n\2\r\2\16\2&\3\2\5\2*\n\2\3\2\3\2\5\2.\n\2\3\3\3\3\7\3\62\n\3\f\3\16"
                    + "\3\65\13\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\6\4?\n\4\r\4\16\4@\3\4\5\4"
                    + "D\n\4\5\4F\n\4\3\5\6\5I\n\5\r\5\16\5J\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"
                    + "\3\6\3\6\3\6\7\6X\n\6\f\6\16\6[\13\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3"
                    + "\7\3\7\3\7\3\7\3\7\3\7\5\7k\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bu\n"
                    + "\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\2\3\n\13\2\4\6\b\n\f\16\20\22\2"
                    + "\2\u0093\2-\3\2\2\2\4/\3\2\2\2\6E\3\2\2\2\bH\3\2\2\2\nL\3\2\2\2\fj\3\2"
                    + "\2\2\16t\3\2\2\2\20v\3\2\2\2\22|\3\2\2\2\24\25\5\4\3\2\25\26\7\2\2\3\26"
                    + ".\3\2\2\2\27\31\7\20\2\2\30\27\3\2\2\2\31\32\3\2\2\2\32\30\3\2\2\2\32"
                    + "\33\3\2\2\2\33\35\3\2\2\2\34\36\5\4\3\2\35\34\3\2\2\2\35\36\3\2\2\2\36"
                    + "\37\3\2\2\2\37.\7\2\2\3 %\7\21\2\2!%\7\22\2\2\"%\7\23\2\2#%\5\f\7\2$ "
                    + "\3\2\2\2$!\3\2\2\2$\"\3\2\2\2$#\3\2\2\2%&\3\2\2\2&$\3\2\2\2&\'\3\2\2\2"
                    + "\')\3\2\2\2(*\5\4\3\2)(\3\2\2\2)*\3\2\2\2*+\3\2\2\2+.\7\2\2\3,.\7\2\2"
                    + "\3-\24\3\2\2\2-\30\3\2\2\2-$\3\2\2\2-,\3\2\2\2.\3\3\2\2\2/\63\5\b\5\2"
                    + "\60\62\5\6\4\2\61\60\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2"
                    + "\64\5\3\2\2\2\65\63\3\2\2\2\66\67\7\20\2\2\67F\5\b\5\28F\7\20\2\29>\7"
                    + "\20\2\2:?\7\21\2\2;?\7\22\2\2<?\7\23\2\2=?\5\f\7\2>:\3\2\2\2>;\3\2\2\2"
                    + "><\3\2\2\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2@A\3\2\2\2AC\3\2\2\2BD\5\b\5\2"
                    + "CB\3\2\2\2CD\3\2\2\2DF\3\2\2\2E\66\3\2\2\2E8\3\2\2\2E9\3\2\2\2F\7\3\2"
                    + "\2\2GI\5\n\6\2HG\3\2\2\2IJ\3\2\2\2JH\3\2\2\2JK\3\2\2\2K\t\3\2\2\2LM\b"
                    + "\6\1\2MN\5\16\b\2NY\3\2\2\2OP\f\7\2\2PX\7\21\2\2QR\f\6\2\2RX\7\22\2\2"
                    + "ST\f\5\2\2TX\7\23\2\2UV\f\4\2\2VX\5\f\7\2WO\3\2\2\2WQ\3\2\2\2WS\3\2\2"
                    + "\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\13\3\2\2\2[Y\3\2\2\2\\]\7"
                    + "\7\2\2]k\7\17\2\2^_\7\5\2\2_`\7\17\2\2`k\7\6\2\2ab\7\5\2\2bc\7\17\2\2"
                    + "cd\7\n\2\2de\7\17\2\2ek\7\6\2\2fg\7\5\2\2gh\7\17\2\2hi\7\17\2\2ik\7\6"
                    + "\2\2j\\\3\2\2\2j^\3\2\2\2ja\3\2\2\2jf\3\2\2\2k\r\3\2\2\2lm\7\3\2\2mn\5"
                    + "\4\3\2no\7\4\2\2ou\3\2\2\2pu\7\13\2\2qu\5\22\n\2ru\5\20\t\2su\7\r\2\2"
                    + "tl\3\2\2\2tp\3\2\2\2tq\3\2\2\2tr\3\2\2\2ts\3\2\2\2u\17\3\2\2\2vw\7\b\2"
                    + "\2wx\5\22\n\2xy\7\24\2\2yz\5\22\n\2z{\7\t\2\2{\21\3\2\2\2|}\7\f\2\2}\23"
                    + "\3\2\2\2\22\32\35$&)-\63>@CEJWYjt";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
