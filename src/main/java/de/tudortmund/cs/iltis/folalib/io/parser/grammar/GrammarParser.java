// Generated from ../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/grammar/GrammarParser.g4
// by ANTLR 4.4
package de.tudortmund.cs.iltis.folalib.io.parser.grammar;

import de.tudortmund.cs.iltis.utils.io.parser.general.AbstractParser;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class GrammarParser extends AbstractParser {
    static {
        RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int WHITESPACE = 1,
            SYMBOL_CONCATENATION = 2,
            SYMBOL = 3,
            PRODUCTION_ARROW = 4,
            LINE_SEPARATOR = 5,
            RIGHT_SIDE_SEPARATOR = 6,
            EPSILON = 7;
    public static final String[] tokenNames = {
        "<INVALID>",
        "WHITESPACE",
        "SYMBOL_CONCATENATION",
        "SYMBOL",
        "PRODUCTION_ARROW",
        "LINE_SEPARATOR",
        "RIGHT_SIDE_SEPARATOR",
        "EPSILON"
    };
    public static final int RULE_entry = 0,
            RULE_initGrammar = 1,
            RULE_productionLine = 2,
            RULE_rhsWithMultipleSeparatorsCheck = 3,
            RULE_rhsSeparators = 4,
            RULE_symSepEps = 5,
            RULE_sententialForm = 6,
            RULE_sententialFormOrEmtpy = 7;
    public static final String[] ruleNames = {
        "entry", "initGrammar", "productionLine", "rhsWithMultipleSeparatorsCheck",
        "rhsSeparators", "symSepEps", "sententialForm", "sententialFormOrEmtpy"
    };

    @Override
    public String getGrammarFileName() {
        return "GrammarParser.g4";
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

    public GrammarParser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    public static class EntryContext extends ParserRuleContext {
        public InitGrammarContext initGrammar() {
            return getRuleContext(InitGrammarContext.class, 0);
        }

        public EntryContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_entry;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitEntry(this);
            else return visitor.visitChildren(this);
        }
    }

    public final EntryContext entry() throws RecognitionException {
        EntryContext _localctx = new EntryContext(_ctx, getState());
        enterRule(_localctx, 0, RULE_entry);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(16);
                initGrammar();
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

    public static class InitGrammarContext extends ParserRuleContext {
        public InitGrammarContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_initGrammar;
        }

        public InitGrammarContext() {}

        public void copyFrom(InitGrammarContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class EmptyGrammarERRORContext extends InitGrammarContext {
        public TerminalNode LINE_SEPARATOR(int i) {
            return getToken(GrammarParser.LINE_SEPARATOR, i);
        }

        public TerminalNode EOF() {
            return getToken(GrammarParser.EOF, 0);
        }

        public List<TerminalNode> LINE_SEPARATOR() {
            return getTokens(GrammarParser.LINE_SEPARATOR);
        }

        public EmptyGrammarERRORContext(InitGrammarContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitEmptyGrammarERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CorrectGrammarContext extends InitGrammarContext {
        public ProductionLineContext productionLine;
        public List<ProductionLineContext> lines = new ArrayList<ProductionLineContext>();

        public TerminalNode LINE_SEPARATOR(int i) {
            return getToken(GrammarParser.LINE_SEPARATOR, i);
        }

        public TerminalNode EOF() {
            return getToken(GrammarParser.EOF, 0);
        }

        public List<ProductionLineContext> productionLine() {
            return getRuleContexts(ProductionLineContext.class);
        }

        public ProductionLineContext productionLine(int i) {
            return getRuleContext(ProductionLineContext.class, i);
        }

        public List<TerminalNode> LINE_SEPARATOR() {
            return getTokens(GrammarParser.LINE_SEPARATOR);
        }

        public CorrectGrammarContext(InitGrammarContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitCorrectGrammar(this);
            else return visitor.visitChildren(this);
        }
    }

    public final InitGrammarContext initGrammar() throws RecognitionException {
        InitGrammarContext _localctx = new InitGrammarContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_initGrammar);
        int _la;
        try {
            int _alt;
            setState(45);
            switch (_input.LA(1)) {
                case SYMBOL:
                case PRODUCTION_ARROW:
                case RIGHT_SIDE_SEPARATOR:
                case EPSILON:
                    _localctx = new CorrectGrammarContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(18);
                        ((CorrectGrammarContext) _localctx).productionLine = productionLine();
                        ((CorrectGrammarContext) _localctx)
                                .lines.add(((CorrectGrammarContext) _localctx).productionLine);
                        setState(27);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                        while (_alt != 2
                                && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    {
                                        setState(20);
                                        _errHandler.sync(this);
                                        _la = _input.LA(1);
                                        do {
                                            {
                                                {
                                                    setState(19);
                                                    match(LINE_SEPARATOR);
                                                }
                                            }
                                            setState(22);
                                            _errHandler.sync(this);
                                            _la = _input.LA(1);
                                        } while (_la == LINE_SEPARATOR);
                                        setState(24);
                                        ((CorrectGrammarContext) _localctx).productionLine =
                                                productionLine();
                                        ((CorrectGrammarContext) _localctx)
                                                .lines.add(
                                                        ((CorrectGrammarContext) _localctx)
                                                                .productionLine);
                                    }
                                }
                            }
                            setState(29);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 1, _ctx);
                        }
                        setState(33);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == LINE_SEPARATOR) {
                            {
                                {
                                    setState(30);
                                    match(LINE_SEPARATOR);
                                }
                            }
                            setState(35);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(36);
                        match(EOF);
                    }
                    break;
                case EOF:
                case LINE_SEPARATOR:
                    _localctx = new EmptyGrammarERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(41);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == LINE_SEPARATOR) {
                            {
                                {
                                    setState(38);
                                    match(LINE_SEPARATOR);
                                }
                            }
                            setState(43);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(44);
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

    public static class ProductionLineContext extends ParserRuleContext {
        public ProductionLineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_productionLine;
        }

        public ProductionLineContext() {}

        public void copyFrom(ProductionLineContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class NoProductionArrowERRORContext extends ProductionLineContext {
        public SymSepEpsContext arbitrarySymbols;

        public SymSepEpsContext symSepEps(int i) {
            return getRuleContext(SymSepEpsContext.class, i);
        }

        public List<SymSepEpsContext> symSepEps() {
            return getRuleContexts(SymSepEpsContext.class);
        }

        public NoProductionArrowERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitNoProductionArrowERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class LeftSideMissingERRORContext extends ProductionLineContext {
        public Token arrow;

        public List<RhsSeparatorsContext> rhsSeparators() {
            return getRuleContexts(RhsSeparatorsContext.class);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RhsSeparatorsContext rhsSeparators(int i) {
            return getRuleContext(RhsSeparatorsContext.class, i);
        }

        public RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck() {
            return getRuleContext(RhsWithMultipleSeparatorsCheckContext.class, 0);
        }

        public LeftSideMissingERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitLeftSideMissingERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CorrectProductionContext extends ProductionLineContext {
        public SententialFormContext lhs;

        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck() {
            return getRuleContext(RhsWithMultipleSeparatorsCheckContext.class, 0);
        }

        public CorrectProductionContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitCorrectProduction(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ProductionWithSeparatorLeftERRORContext extends ProductionLineContext {
        public SententialFormContext lhs;

        public RhsSeparatorsContext rhsSeparators() {
            return getRuleContext(RhsSeparatorsContext.class, 0);
        }

        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck() {
            return getRuleContext(RhsWithMultipleSeparatorsCheckContext.class, 0);
        }

        public ProductionWithSeparatorLeftERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitProductionWithSeparatorLeftERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RightSideSeparatorOnLhsERRORContext extends ProductionLineContext {
        public Token errorSeparator;

        public TerminalNode SYMBOL(int i) {
            return getToken(GrammarParser.SYMBOL, i);
        }

        public TerminalNode RIGHT_SIDE_SEPARATOR() {
            return getToken(GrammarParser.RIGHT_SIDE_SEPARATOR, 0);
        }

        public SymSepEpsContext symSepEps(int i) {
            return getRuleContext(SymSepEpsContext.class, i);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public List<TerminalNode> EPSILON() {
            return getTokens(GrammarParser.EPSILON);
        }

        public List<SymSepEpsContext> symSepEps() {
            return getRuleContexts(SymSepEpsContext.class);
        }

        public List<TerminalNode> SYMBOL() {
            return getTokens(GrammarParser.SYMBOL);
        }

        public TerminalNode EPSILON(int i) {
            return getToken(GrammarParser.EPSILON, i);
        }

        public RightSideSeparatorOnLhsERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitRightSideSeparatorOnLhsERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ProductionWithSeparatorBothERRORContext extends ProductionLineContext {
        public SententialFormContext lhs;

        public List<RhsSeparatorsContext> rhsSeparators() {
            return getRuleContexts(RhsSeparatorsContext.class);
        }

        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RhsSeparatorsContext rhsSeparators(int i) {
            return getRuleContext(RhsSeparatorsContext.class, i);
        }

        public RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck() {
            return getRuleContext(RhsWithMultipleSeparatorsCheckContext.class, 0);
        }

        public ProductionWithSeparatorBothERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitProductionWithSeparatorBothERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class MultipleProductionArrowsERRORContext extends ProductionLineContext {
        public Token second;

        public SymSepEpsContext symSepEps(int i) {
            return getRuleContext(SymSepEpsContext.class, i);
        }

        public TerminalNode PRODUCTION_ARROW(int i) {
            return getToken(GrammarParser.PRODUCTION_ARROW, i);
        }

        public List<TerminalNode> PRODUCTION_ARROW() {
            return getTokens(GrammarParser.PRODUCTION_ARROW);
        }

        public List<SymSepEpsContext> symSepEps() {
            return getRuleContexts(SymSepEpsContext.class);
        }

        public MultipleProductionArrowsERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitMultipleProductionArrowsERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class RightSideMissingERRORContext extends ProductionLineContext {
        public SententialFormContext lhs;
        public Token arrow;

        public RhsSeparatorsContext rhsSeparators() {
            return getRuleContext(RhsSeparatorsContext.class, 0);
        }

        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RightSideMissingERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitRightSideMissingERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class ProductionWithSeparatorRightERRORContext extends ProductionLineContext {
        public SententialFormContext lhs;

        public RhsSeparatorsContext rhsSeparators() {
            return getRuleContext(RhsSeparatorsContext.class, 0);
        }

        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck() {
            return getRuleContext(RhsWithMultipleSeparatorsCheckContext.class, 0);
        }

        public ProductionWithSeparatorRightERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitProductionWithSeparatorRightERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class BothSidesMissingERRORContext extends ProductionLineContext {
        public Token arrow;

        public RhsSeparatorsContext rhsSeparators() {
            return getRuleContext(RhsSeparatorsContext.class, 0);
        }

        public TerminalNode PRODUCTION_ARROW() {
            return getToken(GrammarParser.PRODUCTION_ARROW, 0);
        }

        public BothSidesMissingERRORContext(ProductionLineContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitBothSidesMissingERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ProductionLineContext productionLine() throws RecognitionException {
        ProductionLineContext _localctx = new ProductionLineContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_productionLine);
        int _la;
        try {
            setState(130);
            switch (getInterpreter().adaptivePredict(_input, 17, _ctx)) {
                case 1:
                    _localctx = new CorrectProductionContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(47);
                        ((CorrectProductionContext) _localctx).lhs = sententialForm();
                        setState(48);
                        match(PRODUCTION_ARROW);
                        setState(49);
                        rhsWithMultipleSeparatorsCheck();
                    }
                    break;
                case 2:
                    _localctx = new ProductionWithSeparatorRightERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(51);
                        ((ProductionWithSeparatorRightERRORContext) _localctx).lhs =
                                sententialForm();
                        setState(52);
                        match(PRODUCTION_ARROW);
                        setState(53);
                        rhsWithMultipleSeparatorsCheck();
                        setState(54);
                        rhsSeparators();
                    }
                    break;
                case 3:
                    _localctx = new ProductionWithSeparatorLeftERRORContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(56);
                        ((ProductionWithSeparatorLeftERRORContext) _localctx).lhs =
                                sententialForm();
                        setState(57);
                        match(PRODUCTION_ARROW);
                        setState(58);
                        rhsSeparators();
                        setState(59);
                        rhsWithMultipleSeparatorsCheck();
                    }
                    break;
                case 4:
                    _localctx = new ProductionWithSeparatorBothERRORContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(61);
                        ((ProductionWithSeparatorBothERRORContext) _localctx).lhs =
                                sententialForm();
                        setState(62);
                        match(PRODUCTION_ARROW);
                        setState(63);
                        rhsSeparators();
                        setState(64);
                        rhsWithMultipleSeparatorsCheck();
                        setState(65);
                        rhsSeparators();
                    }
                    break;
                case 5:
                    _localctx = new LeftSideMissingERRORContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                    {
                        setState(67);
                        ((LeftSideMissingERRORContext) _localctx).arrow = match(PRODUCTION_ARROW);
                        setState(69);
                        _la = _input.LA(1);
                        if (_la == RIGHT_SIDE_SEPARATOR) {
                            {
                                setState(68);
                                rhsSeparators();
                            }
                        }

                        setState(71);
                        rhsWithMultipleSeparatorsCheck();
                        setState(73);
                        _la = _input.LA(1);
                        if (_la == RIGHT_SIDE_SEPARATOR) {
                            {
                                setState(72);
                                rhsSeparators();
                            }
                        }
                    }
                    break;
                case 6:
                    _localctx = new RightSideMissingERRORContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                    {
                        setState(75);
                        ((RightSideMissingERRORContext) _localctx).lhs = sententialForm();
                        setState(76);
                        ((RightSideMissingERRORContext) _localctx).arrow = match(PRODUCTION_ARROW);
                        setState(78);
                        _la = _input.LA(1);
                        if (_la == RIGHT_SIDE_SEPARATOR) {
                            {
                                setState(77);
                                rhsSeparators();
                            }
                        }
                    }
                    break;
                case 7:
                    _localctx = new BothSidesMissingERRORContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                    {
                        setState(80);
                        ((BothSidesMissingERRORContext) _localctx).arrow = match(PRODUCTION_ARROW);
                        setState(82);
                        _la = _input.LA(1);
                        if (_la == RIGHT_SIDE_SEPARATOR) {
                            {
                                setState(81);
                                rhsSeparators();
                            }
                        }
                    }
                    break;
                case 8:
                    _localctx = new RightSideSeparatorOnLhsERRORContext(_localctx);
                    enterOuterAlt(_localctx, 8);
                    {
                        setState(87);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == SYMBOL || _la == EPSILON) {
                            {
                                {
                                    setState(84);
                                    _la = _input.LA(1);
                                    if (!(_la == SYMBOL || _la == EPSILON)) {
                                        _errHandler.recoverInline(this);
                                    }
                                    consume();
                                }
                            }
                            setState(89);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(90);
                        ((RightSideSeparatorOnLhsERRORContext) _localctx).errorSeparator =
                                match(RIGHT_SIDE_SEPARATOR);
                        setState(94);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0)) {
                            {
                                {
                                    setState(91);
                                    symSepEps();
                                }
                            }
                            setState(96);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(97);
                        match(PRODUCTION_ARROW);
                        setState(101);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0)) {
                            {
                                {
                                    setState(98);
                                    symSepEps();
                                }
                            }
                            setState(103);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                    break;
                case 9:
                    _localctx = new MultipleProductionArrowsERRORContext(_localctx);
                    enterOuterAlt(_localctx, 9);
                    {
                        setState(107);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0)) {
                            {
                                {
                                    setState(104);
                                    symSepEps();
                                }
                            }
                            setState(109);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(110);
                        match(PRODUCTION_ARROW);
                        setState(114);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0)) {
                            {
                                {
                                    setState(111);
                                    symSepEps();
                                }
                            }
                            setState(116);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                        setState(117);
                        ((MultipleProductionArrowsERRORContext) _localctx).second =
                                match(PRODUCTION_ARROW);
                        setState(122);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << PRODUCTION_ARROW)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0)) {
                            {
                                setState(120);
                                switch (_input.LA(1)) {
                                    case SYMBOL:
                                    case RIGHT_SIDE_SEPARATOR:
                                    case EPSILON:
                                        {
                                            setState(118);
                                            symSepEps();
                                        }
                                        break;
                                    case PRODUCTION_ARROW:
                                        {
                                            setState(119);
                                            match(PRODUCTION_ARROW);
                                        }
                                        break;
                                    default:
                                        throw new NoViableAltException(this);
                                }
                            }
                            setState(124);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        }
                    }
                    break;
                case 10:
                    _localctx = new NoProductionArrowERRORContext(_localctx);
                    enterOuterAlt(_localctx, 10);
                    {
                        setState(126);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(125);
                                    ((NoProductionArrowERRORContext) _localctx).arbitrarySymbols =
                                            symSepEps();
                                }
                            }
                            setState(128);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while ((((_la) & ~0x3f) == 0
                                && ((1L << _la)
                                                & ((1L << SYMBOL)
                                                        | (1L << RIGHT_SIDE_SEPARATOR)
                                                        | (1L << EPSILON)))
                                        != 0));
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

    public static class RhsWithMultipleSeparatorsCheckContext extends ParserRuleContext {
        public SententialFormContext sententialForm;
        public List<SententialFormContext> rhs = new ArrayList<SententialFormContext>();

        public List<RhsSeparatorsContext> rhsSeparators() {
            return getRuleContexts(RhsSeparatorsContext.class);
        }

        public List<SententialFormContext> sententialForm() {
            return getRuleContexts(SententialFormContext.class);
        }

        public RhsSeparatorsContext rhsSeparators(int i) {
            return getRuleContext(RhsSeparatorsContext.class, i);
        }

        public SententialFormContext sententialForm(int i) {
            return getRuleContext(SententialFormContext.class, i);
        }

        public RhsWithMultipleSeparatorsCheckContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rhsWithMultipleSeparatorsCheck;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitRhsWithMultipleSeparatorsCheck(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RhsWithMultipleSeparatorsCheckContext rhsWithMultipleSeparatorsCheck()
            throws RecognitionException {
        RhsWithMultipleSeparatorsCheckContext _localctx =
                new RhsWithMultipleSeparatorsCheckContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_rhsWithMultipleSeparatorsCheck);
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(137);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(132);
                                ((RhsWithMultipleSeparatorsCheckContext) _localctx).sententialForm =
                                        sententialForm();
                                ((RhsWithMultipleSeparatorsCheckContext) _localctx)
                                        .rhs.add(
                                                ((RhsWithMultipleSeparatorsCheckContext) _localctx)
                                                        .sententialForm);
                                setState(133);
                                rhsSeparators();
                            }
                        }
                    }
                    setState(139);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 18, _ctx);
                }
                setState(140);
                ((RhsWithMultipleSeparatorsCheckContext) _localctx).sententialForm =
                        sententialForm();
                ((RhsWithMultipleSeparatorsCheckContext) _localctx)
                        .rhs.add(
                                ((RhsWithMultipleSeparatorsCheckContext) _localctx).sententialForm);
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

    public static class RhsSeparatorsContext extends ParserRuleContext {
        public RhsSeparatorsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_rhsSeparators;
        }

        public RhsSeparatorsContext() {}

        public void copyFrom(RhsSeparatorsContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class AbundantRightSideSeparatorsERRORContext extends RhsSeparatorsContext {
        public Token second;

        public List<TerminalNode> RIGHT_SIDE_SEPARATOR() {
            return getTokens(GrammarParser.RIGHT_SIDE_SEPARATOR);
        }

        public TerminalNode RIGHT_SIDE_SEPARATOR(int i) {
            return getToken(GrammarParser.RIGHT_SIDE_SEPARATOR, i);
        }

        public AbundantRightSideSeparatorsERRORContext(RhsSeparatorsContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitAbundantRightSideSeparatorsERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CorrectRightSideSeparatorContext extends RhsSeparatorsContext {
        public TerminalNode RIGHT_SIDE_SEPARATOR() {
            return getToken(GrammarParser.RIGHT_SIDE_SEPARATOR, 0);
        }

        public CorrectRightSideSeparatorContext(RhsSeparatorsContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitCorrectRightSideSeparator(this);
            else return visitor.visitChildren(this);
        }
    }

    public final RhsSeparatorsContext rhsSeparators() throws RecognitionException {
        RhsSeparatorsContext _localctx = new RhsSeparatorsContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_rhsSeparators);
        int _la;
        try {
            setState(149);
            switch (getInterpreter().adaptivePredict(_input, 20, _ctx)) {
                case 1:
                    _localctx = new CorrectRightSideSeparatorContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(142);
                        match(RIGHT_SIDE_SEPARATOR);
                    }
                    break;
                case 2:
                    _localctx = new AbundantRightSideSeparatorsERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(143);
                        match(RIGHT_SIDE_SEPARATOR);
                        setState(145);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(144);
                                    ((AbundantRightSideSeparatorsERRORContext) _localctx).second =
                                            match(RIGHT_SIDE_SEPARATOR);
                                }
                            }
                            setState(147);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == RIGHT_SIDE_SEPARATOR);
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

    public static class SymSepEpsContext extends ParserRuleContext {
        public TerminalNode RIGHT_SIDE_SEPARATOR() {
            return getToken(GrammarParser.RIGHT_SIDE_SEPARATOR, 0);
        }

        public TerminalNode EPSILON() {
            return getToken(GrammarParser.EPSILON, 0);
        }

        public TerminalNode SYMBOL() {
            return getToken(GrammarParser.SYMBOL, 0);
        }

        public SymSepEpsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_symSepEps;
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitSymSepEps(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SymSepEpsContext symSepEps() throws RecognitionException {
        SymSepEpsContext _localctx = new SymSepEpsContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_symSepEps);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(151);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0
                        && ((1L << _la)
                                        & ((1L << SYMBOL)
                                                | (1L << RIGHT_SIDE_SEPARATOR)
                                                | (1L << EPSILON)))
                                != 0))) {
                    _errHandler.recoverInline(this);
                }
                consume();
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

    public static class SententialFormContext extends ParserRuleContext {
        public SententialFormContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sententialForm;
        }

        public SententialFormContext() {}

        public void copyFrom(SententialFormContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class SymbolEpsilonMixERRORContext extends SententialFormContext {
        public Token SYMBOL;
        public List<Token> symbols = new ArrayList<Token>();

        public TerminalNode SYMBOL(int i) {
            return getToken(GrammarParser.SYMBOL, i);
        }

        public List<TerminalNode> EPSILON() {
            return getTokens(GrammarParser.EPSILON);
        }

        public List<TerminalNode> SYMBOL() {
            return getTokens(GrammarParser.SYMBOL);
        }

        public TerminalNode EPSILON(int i) {
            return getToken(GrammarParser.EPSILON, i);
        }

        public SymbolEpsilonMixERRORContext(SententialFormContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitSymbolEpsilonMixERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class AbundantEpsilonERRORContext extends SententialFormContext {
        public Token second;

        public List<TerminalNode> EPSILON() {
            return getTokens(GrammarParser.EPSILON);
        }

        public TerminalNode EPSILON(int i) {
            return getToken(GrammarParser.EPSILON, i);
        }

        public AbundantEpsilonERRORContext(SententialFormContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitAbundantEpsilonERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class EpsilonContext extends SententialFormContext {
        public TerminalNode EPSILON() {
            return getToken(GrammarParser.EPSILON, 0);
        }

        public EpsilonContext(SententialFormContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitEpsilon(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class CorrectSententialFormContext extends SententialFormContext {
        public Token SYMBOL;
        public List<Token> symbols = new ArrayList<Token>();

        public TerminalNode SYMBOL(int i) {
            return getToken(GrammarParser.SYMBOL, i);
        }

        public List<TerminalNode> SYMBOL() {
            return getTokens(GrammarParser.SYMBOL);
        }

        public CorrectSententialFormContext(SententialFormContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitCorrectSententialForm(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SententialFormContext sententialForm() throws RecognitionException {
        SententialFormContext _localctx = new SententialFormContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_sententialForm);
        int _la;
        try {
            int _alt;
            setState(185);
            switch (getInterpreter().adaptivePredict(_input, 28, _ctx)) {
                case 1:
                    _localctx = new CorrectSententialFormContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(154);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(153);
                                    ((CorrectSententialFormContext) _localctx).SYMBOL =
                                            match(SYMBOL);
                                    ((CorrectSententialFormContext) _localctx)
                                            .symbols.add(
                                                    ((CorrectSententialFormContext) _localctx)
                                                            .SYMBOL);
                                }
                            }
                            setState(156);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == SYMBOL);
                    }
                    break;
                case 2:
                    _localctx = new EpsilonContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(158);
                        match(EPSILON);
                    }
                    break;
                case 3:
                    _localctx = new AbundantEpsilonERRORContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(159);
                        match(EPSILON);
                        setState(161);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        do {
                            {
                                {
                                    setState(160);
                                    ((AbundantEpsilonERRORContext) _localctx).second =
                                            match(EPSILON);
                                }
                            }
                            setState(163);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
                        } while (_la == EPSILON);
                    }
                    break;
                case 4:
                    _localctx = new SymbolEpsilonMixERRORContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(169);
                        _errHandler.sync(this);
                        _alt = getInterpreter().adaptivePredict(_input, 24, _ctx);
                        while (_alt != 2
                                && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                            if (_alt == 1) {
                                {
                                    setState(167);
                                    switch (_input.LA(1)) {
                                        case EPSILON:
                                            {
                                                setState(165);
                                                match(EPSILON);
                                            }
                                            break;
                                        case SYMBOL:
                                            {
                                                setState(166);
                                                ((SymbolEpsilonMixERRORContext) _localctx).SYMBOL =
                                                        match(SYMBOL);
                                                ((SymbolEpsilonMixERRORContext) _localctx)
                                                        .symbols.add(
                                                                ((SymbolEpsilonMixERRORContext)
                                                                                _localctx)
                                                                        .SYMBOL);
                                            }
                                            break;
                                        default:
                                            throw new NoViableAltException(this);
                                    }
                                }
                            }
                            setState(171);
                            _errHandler.sync(this);
                            _alt = getInterpreter().adaptivePredict(_input, 24, _ctx);
                        }
                        setState(176);
                        switch (_input.LA(1)) {
                            case EPSILON:
                                {
                                    setState(172);
                                    match(EPSILON);
                                    setState(173);
                                    ((SymbolEpsilonMixERRORContext) _localctx).SYMBOL =
                                            match(SYMBOL);
                                    ((SymbolEpsilonMixERRORContext) _localctx)
                                            .symbols.add(
                                                    ((SymbolEpsilonMixERRORContext) _localctx)
                                                            .SYMBOL);
                                }
                                break;
                            case SYMBOL:
                                {
                                    setState(174);
                                    ((SymbolEpsilonMixERRORContext) _localctx).SYMBOL =
                                            match(SYMBOL);
                                    ((SymbolEpsilonMixERRORContext) _localctx)
                                            .symbols.add(
                                                    ((SymbolEpsilonMixERRORContext) _localctx)
                                                            .SYMBOL);
                                    setState(175);
                                    match(EPSILON);
                                }
                                break;
                            default:
                                throw new NoViableAltException(this);
                        }
                        setState(182);
                        _errHandler.sync(this);
                        _la = _input.LA(1);
                        while (_la == SYMBOL || _la == EPSILON) {
                            {
                                setState(180);
                                switch (_input.LA(1)) {
                                    case EPSILON:
                                        {
                                            setState(178);
                                            match(EPSILON);
                                        }
                                        break;
                                    case SYMBOL:
                                        {
                                            setState(179);
                                            ((SymbolEpsilonMixERRORContext) _localctx).SYMBOL =
                                                    match(SYMBOL);
                                            ((SymbolEpsilonMixERRORContext) _localctx)
                                                    .symbols.add(
                                                            ((SymbolEpsilonMixERRORContext)
                                                                            _localctx)
                                                                    .SYMBOL);
                                        }
                                        break;
                                    default:
                                        throw new NoViableAltException(this);
                                }
                            }
                            setState(184);
                            _errHandler.sync(this);
                            _la = _input.LA(1);
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

    public static class SententialFormOrEmtpyContext extends ParserRuleContext {
        public SententialFormOrEmtpyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_sententialFormOrEmtpy;
        }

        public SententialFormOrEmtpyContext() {}

        public void copyFrom(SententialFormOrEmtpyContext ctx) {
            super.copyFrom(ctx);
        }
    }

    public static class SententialFormLINKContext extends SententialFormOrEmtpyContext {
        public SententialFormContext sententialForm() {
            return getRuleContext(SententialFormContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(GrammarParser.EOF, 0);
        }

        public SententialFormLINKContext(SententialFormOrEmtpyContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor).visitSententialFormLINK(this);
            else return visitor.visitChildren(this);
        }
    }

    public static class EmptySententialFormERRORContext extends SententialFormOrEmtpyContext {
        public TerminalNode EOF() {
            return getToken(GrammarParser.EOF, 0);
        }

        public EmptySententialFormERRORContext(SententialFormOrEmtpyContext ctx) {
            copyFrom(ctx);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof GrammarParserVisitor)
                return ((GrammarParserVisitor<? extends T>) visitor)
                        .visitEmptySententialFormERROR(this);
            else return visitor.visitChildren(this);
        }
    }

    public final SententialFormOrEmtpyContext sententialFormOrEmtpy() throws RecognitionException {
        SententialFormOrEmtpyContext _localctx = new SententialFormOrEmtpyContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_sententialFormOrEmtpy);
        try {
            setState(191);
            switch (_input.LA(1)) {
                case SYMBOL:
                case EPSILON:
                    _localctx = new SententialFormLINKContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(187);
                        sententialForm();
                        setState(188);
                        match(EOF);
                    }
                    break;
                case EOF:
                    _localctx = new EmptySententialFormERRORContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(190);
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

    public static final String _serializedATN =
            "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\t\u00c4\4\2\t\2\4"
                    + "\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\3\3\3"
                    + "\6\3\27\n\3\r\3\16\3\30\3\3\7\3\34\n\3\f\3\16\3\37\13\3\3\3\7\3\"\n\3"
                    + "\f\3\16\3%\13\3\3\3\3\3\3\3\7\3*\n\3\f\3\16\3-\13\3\3\3\5\3\60\n\3\3\4"
                    + "\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"
                    + "\4\3\4\3\4\3\4\5\4H\n\4\3\4\3\4\5\4L\n\4\3\4\3\4\3\4\5\4Q\n\4\3\4\3\4"
                    + "\5\4U\n\4\3\4\7\4X\n\4\f\4\16\4[\13\4\3\4\3\4\7\4_\n\4\f\4\16\4b\13\4"
                    + "\3\4\3\4\7\4f\n\4\f\4\16\4i\13\4\3\4\7\4l\n\4\f\4\16\4o\13\4\3\4\3\4\7"
                    + "\4s\n\4\f\4\16\4v\13\4\3\4\3\4\3\4\7\4{\n\4\f\4\16\4~\13\4\3\4\6\4\u0081"
                    + "\n\4\r\4\16\4\u0082\5\4\u0085\n\4\3\5\3\5\3\5\7\5\u008a\n\5\f\5\16\5\u008d"
                    + "\13\5\3\5\3\5\3\6\3\6\3\6\6\6\u0094\n\6\r\6\16\6\u0095\5\6\u0098\n\6\3"
                    + "\7\3\7\3\b\6\b\u009d\n\b\r\b\16\b\u009e\3\b\3\b\3\b\6\b\u00a4\n\b\r\b"
                    + "\16\b\u00a5\3\b\3\b\7\b\u00aa\n\b\f\b\16\b\u00ad\13\b\3\b\3\b\3\b\3\b"
                    + "\5\b\u00b3\n\b\3\b\3\b\7\b\u00b7\n\b\f\b\16\b\u00ba\13\b\5\b\u00bc\n\b"
                    + "\3\t\3\t\3\t\3\t\5\t\u00c2\n\t\3\t\2\2\n\2\4\6\b\n\f\16\20\2\4\4\2\5\5"
                    + "\t\t\4\2\5\5\b\t\u00e3\2\22\3\2\2\2\4/\3\2\2\2\6\u0084\3\2\2\2\b\u008b"
                    + "\3\2\2\2\n\u0097\3\2\2\2\f\u0099\3\2\2\2\16\u00bb\3\2\2\2\20\u00c1\3\2"
                    + "\2\2\22\23\5\4\3\2\23\3\3\2\2\2\24\35\5\6\4\2\25\27\7\7\2\2\26\25\3\2"
                    + "\2\2\27\30\3\2\2\2\30\26\3\2\2\2\30\31\3\2\2\2\31\32\3\2\2\2\32\34\5\6"
                    + "\4\2\33\26\3\2\2\2\34\37\3\2\2\2\35\33\3\2\2\2\35\36\3\2\2\2\36#\3\2\2"
                    + "\2\37\35\3\2\2\2 \"\7\7\2\2! \3\2\2\2\"%\3\2\2\2#!\3\2\2\2#$\3\2\2\2$"
                    + "&\3\2\2\2%#\3\2\2\2&\'\7\2\2\3\'\60\3\2\2\2(*\7\7\2\2)(\3\2\2\2*-\3\2"
                    + "\2\2+)\3\2\2\2+,\3\2\2\2,.\3\2\2\2-+\3\2\2\2.\60\7\2\2\3/\24\3\2\2\2/"
                    + "+\3\2\2\2\60\5\3\2\2\2\61\62\5\16\b\2\62\63\7\6\2\2\63\64\5\b\5\2\64\u0085"
                    + "\3\2\2\2\65\66\5\16\b\2\66\67\7\6\2\2\678\5\b\5\289\5\n\6\29\u0085\3\2"
                    + "\2\2:;\5\16\b\2;<\7\6\2\2<=\5\n\6\2=>\5\b\5\2>\u0085\3\2\2\2?@\5\16\b"
                    + "\2@A\7\6\2\2AB\5\n\6\2BC\5\b\5\2CD\5\n\6\2D\u0085\3\2\2\2EG\7\6\2\2FH"
                    + "\5\n\6\2GF\3\2\2\2GH\3\2\2\2HI\3\2\2\2IK\5\b\5\2JL\5\n\6\2KJ\3\2\2\2K"
                    + "L\3\2\2\2L\u0085\3\2\2\2MN\5\16\b\2NP\7\6\2\2OQ\5\n\6\2PO\3\2\2\2PQ\3"
                    + "\2\2\2Q\u0085\3\2\2\2RT\7\6\2\2SU\5\n\6\2TS\3\2\2\2TU\3\2\2\2U\u0085\3"
                    + "\2\2\2VX\t\2\2\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\\\3\2\2\2[Y"
                    + "\3\2\2\2\\`\7\b\2\2]_\5\f\7\2^]\3\2\2\2_b\3\2\2\2`^\3\2\2\2`a\3\2\2\2"
                    + "ac\3\2\2\2b`\3\2\2\2cg\7\6\2\2df\5\f\7\2ed\3\2\2\2fi\3\2\2\2ge\3\2\2\2"
                    + "gh\3\2\2\2h\u0085\3\2\2\2ig\3\2\2\2jl\5\f\7\2kj\3\2\2\2lo\3\2\2\2mk\3"
                    + "\2\2\2mn\3\2\2\2np\3\2\2\2om\3\2\2\2pt\7\6\2\2qs\5\f\7\2rq\3\2\2\2sv\3"
                    + "\2\2\2tr\3\2\2\2tu\3\2\2\2uw\3\2\2\2vt\3\2\2\2w|\7\6\2\2x{\5\f\7\2y{\7"
                    + "\6\2\2zx\3\2\2\2zy\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\u0085\3\2\2"
                    + "\2~|\3\2\2\2\177\u0081\5\f\7\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082"
                    + "\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084\61\3\2\2"
                    + "\2\u0084\65\3\2\2\2\u0084:\3\2\2\2\u0084?\3\2\2\2\u0084E\3\2\2\2\u0084"
                    + "M\3\2\2\2\u0084R\3\2\2\2\u0084Y\3\2\2\2\u0084m\3\2\2\2\u0084\u0080\3\2"
                    + "\2\2\u0085\7\3\2\2\2\u0086\u0087\5\16\b\2\u0087\u0088\5\n\6\2\u0088\u008a"
                    + "\3\2\2\2\u0089\u0086\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008b"
                    + "\u008c\3\2\2\2\u008c\u008e\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u008f\5\16"
                    + "\b\2\u008f\t\3\2\2\2\u0090\u0098\7\b\2\2\u0091\u0093\7\b\2\2\u0092\u0094"
                    + "\7\b\2\2\u0093\u0092\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0093\3\2\2\2\u0095"
                    + "\u0096\3\2\2\2\u0096\u0098\3\2\2\2\u0097\u0090\3\2\2\2\u0097\u0091\3\2"
                    + "\2\2\u0098\13\3\2\2\2\u0099\u009a\t\3\2\2\u009a\r\3\2\2\2\u009b\u009d"
                    + "\7\5\2\2\u009c\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u009c\3\2\2\2\u009e"
                    + "\u009f\3\2\2\2\u009f\u00bc\3\2\2\2\u00a0\u00bc\7\t\2\2\u00a1\u00a3\7\t"
                    + "\2\2\u00a2\u00a4\7\t\2\2\u00a3\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5"
                    + "\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00bc\3\2\2\2\u00a7\u00aa\7\t"
                    + "\2\2\u00a8\u00aa\7\5\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00a8\3\2\2\2\u00aa"
                    + "\u00ad\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00b2\3\2"
                    + "\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00af\7\t\2\2\u00af\u00b3\7\5\2\2\u00b0"
                    + "\u00b1\7\5\2\2\u00b1\u00b3\7\t\2\2\u00b2\u00ae\3\2\2\2\u00b2\u00b0\3\2"
                    + "\2\2\u00b3\u00b8\3\2\2\2\u00b4\u00b7\7\t\2\2\u00b5\u00b7\7\5\2\2\u00b6"
                    + "\u00b4\3\2\2\2\u00b6\u00b5\3\2\2\2\u00b7\u00ba\3\2\2\2\u00b8\u00b6\3\2"
                    + "\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb"
                    + "\u009c\3\2\2\2\u00bb\u00a0\3\2\2\2\u00bb\u00a1\3\2\2\2\u00bb\u00ab\3\2"
                    + "\2\2\u00bc\17\3\2\2\2\u00bd\u00be\5\16\b\2\u00be\u00bf\7\2\2\3\u00bf\u00c2"
                    + "\3\2\2\2\u00c0\u00c2\7\2\2\3\u00c1\u00bd\3\2\2\2\u00c1\u00c0\3\2\2\2\u00c2"
                    + "\21\3\2\2\2 \30\35#+/GKPTY`gmtz|\u0082\u0084\u008b\u0095\u0097\u009e\u00a5"
                    + "\u00a9\u00ab\u00b2\u00b6\u00b8\u00bb\u00c1";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
