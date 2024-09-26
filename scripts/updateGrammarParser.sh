#!/bin/bash
# configuration: set paths relative to this file
DIR=../src/main/java/de/tudortmund/cs/iltis/folalib/io/parser/grammar
GRAMMARNAME=GrammarParser.g4
PCKG=de.tudortmund.cs.iltis.folalib.io.parser.grammar
# cd to the folder of this file
cd "${0%/*}" || exit
# execute the parser creation
java -cp "$ANTLRTOOLJAR:%CLASSPATH%" org.antlr.v4.Tool -no-listener -visitor -package $PCKG -lib "$DIR" "$DIR/$GRAMMARNAME"
