parser grammar GrammarParser;

options { superClass=AbstractParser; }

@header {
    import de.tudortmund.cs.iltis.utils.io.parser.general.AbstractParser;
}

tokens {
    WHITESPACE,
    SYMBOL_CONCATENATION,

    SYMBOL,
    PRODUCTION_ARROW,
    LINE_SEPARATOR,
    RIGHT_SIDE_SEPARATOR,
    EPSILON
}

// This rule exists to allow better code-structure in the visitor
entry: initGrammar;

// please note: multiple line separators do not trigger a fault. The standard properties define "\n" and "," as line
// separator and both can be used consecutively without a fault.
// Moreover, trailing line separators also do not trigger a fault to allow e.g. a trailing empty line at the end of the
// input.
initGrammar
    : lines+=productionLine (LINE_SEPARATOR+ lines+=productionLine)* LINE_SEPARATOR* EOF
                                                                    # CorrectGrammar
    | LINE_SEPARATOR* EOF                                           # EmptyGrammarERROR
    ;

productionLine
    : lhs=sententialForm PRODUCTION_ARROW rhsWithMultipleSeparatorsCheck
                                                                    # CorrectProduction
    | lhs=sententialForm PRODUCTION_ARROW rhsWithMultipleSeparatorsCheck rhsSeparators
                                                                    # ProductionWithSeparatorRightERROR
    | lhs=sententialForm PRODUCTION_ARROW rhsSeparators rhsWithMultipleSeparatorsCheck
                                                                    # ProductionWithSeparatorLeftERROR
    | lhs=sententialForm PRODUCTION_ARROW rhsSeparators rhsWithMultipleSeparatorsCheck rhsSeparators
                                                                    # ProductionWithSeparatorBothERROR
    | arrow=PRODUCTION_ARROW rhsSeparators? rhsWithMultipleSeparatorsCheck rhsSeparators?
                                                                    # LeftSideMissingERROR
    | lhs=sententialForm arrow=PRODUCTION_ARROW rhsSeparators?
                                                                    # RightSideMissingERROR
    | arrow=PRODUCTION_ARROW rhsSeparators?
                                                                    # BothSidesMissingERROR
    | (SYMBOL | EPSILON)* errorSeparator=RIGHT_SIDE_SEPARATOR symSepEps* PRODUCTION_ARROW symSepEps*
                                                                    # RightSideSeparatorOnLhsERROR
    | symSepEps* PRODUCTION_ARROW symSepEps* second=PRODUCTION_ARROW (symSepEps | PRODUCTION_ARROW)*
                                                                    # MultipleProductionArrowsERROR
    | arbitrarySymbols=symSepEps+
                                                                    # NoProductionArrowERROR
    ;

rhsWithMultipleSeparatorsCheck
    : (rhs+=sententialForm rhsSeparators)* rhs+=sententialForm
    ;

rhsSeparators
    : RIGHT_SIDE_SEPARATOR                                          # CorrectRightSideSeparator
    | RIGHT_SIDE_SEPARATOR second=RIGHT_SIDE_SEPARATOR+             # AbundantRightSideSeparatorsERROR
    ;

// Used to match error rules in rule "productionLine"
symSepEps
    : SYMBOL | EPSILON | RIGHT_SIDE_SEPARATOR
    ;

sententialForm
    : (symbols+=SYMBOL)+                                            # CorrectSententialForm
    | EPSILON                                                       # Epsilon
    | EPSILON second=EPSILON+                                       # AbundantEpsilonERROR
    | (EPSILON | symbols+=SYMBOL)* (EPSILON symbols+=SYMBOL | symbols+=SYMBOL EPSILON) (EPSILON | symbols+=SYMBOL)*
                                                                    # SymbolEpsilonMixERROR
    ;

// Only relevant for SententialFormReader
sententialFormOrEmtpy
    : sententialForm EOF                                            # SententialFormLINK
    | EOF                                                           # EmptySententialFormERROR
    ;