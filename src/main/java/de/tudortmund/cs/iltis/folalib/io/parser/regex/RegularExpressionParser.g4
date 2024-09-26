parser grammar RegularExpressionParser;

options { superClass=AbstractParser; }
tokens {
    OPENING_PARENTHESIS, CLOSING_PARENTHESIS, OPENING_REPETITION, CLOSING_REPETITION, FIXED_REPETITION_OPENER,
    OPENING_RANGE, CLOSING_RANGE, REPETITION_SEPARATOR,
    EPSILON, SYMBOL, EMPTY_SET, WORD, REPETITION_OPERAND,
    ALTERNATION, KLEENE_STAR, KLEENE_PLUS,
    OPTIONAL, RANGE_SEPARATOR,
    WHITESPACE
}

@header {
import de.tudortmund.cs.iltis.utils.io.parser.general.AbstractParser;
}

onlyRegex
    : regex EOF                                                 # CorrectOnlyRegex
    | ALTERNATION+ regex? EOF                                   # StartWithAlternationERROR
    | (KLEENE_STAR | KLEENE_PLUS | OPTIONAL | repetition)+ regex? EOF
                                                                # StartWithCardinalityOperatorERROR
    | EOF                                                       # EmptyRegexERROR
    ;

regex: concatenation (alternationContinuation)*;

alternationContinuation
    : ALTERNATION concatenation                                 # CorrectAlternation
    | ALTERNATION                                               # MissingAlternationOperandERROR
    | ALTERNATION (KLEENE_STAR | KLEENE_PLUS | OPTIONAL | repetition)+ concatenation?
                                                                # CardinalityOperatorAfterAlternationERROR
    ;

concatenation: (subexpressionWrap)+;

subexpressionWrap
    : subexpressionWrap KLEENE_STAR                             # KleeneStar
    | subexpressionWrap KLEENE_PLUS                             # KleenePlus
    | subexpressionWrap OPTIONAL                                # Optional
    | subexpressionWrap repetition                              # RepetitionWithSubexpression
    | subexpression                                             # SubexpressionLINK
    ;

repetition
    : FIXED_REPETITION_OPENER numberOfRepetitions=REPETITION_OPERAND
                                                                # FixedRepetition
    | OPENING_REPETITION numberOfRepetitions=REPETITION_OPERAND CLOSING_REPETITION
                                                                # RepetitionAmount
    | OPENING_REPETITION lower=REPETITION_OPERAND REPETITION_SEPARATOR upper=REPETITION_OPERAND CLOSING_REPETITION
                                                                # RepetitionRange
    | OPENING_REPETITION lower=REPETITION_OPERAND upper=REPETITION_OPERAND CLOSING_REPETITION
                                                                # RepetitionAmbiguousERROR
    ;

subexpression
    : OPENING_PARENTHESIS inner = regex CLOSING_PARENTHESIS     # Nesting
    | EPSILON                                                   # Epsilon
    | symbol                                                    # SymbolLINK
    | range                                                     # RangeLINK
    | EMPTY_SET                                                 # EmptySet
    ;

range: OPENING_RANGE lower=symbol RANGE_SEPARATOR upper=symbol CLOSING_RANGE;

symbol: SYMBOL;