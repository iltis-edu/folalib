package de.tudortmund.cs.iltis.folalib.io.writer.regex;

import java.io.Serializable;

/** Properties class for customizing {@link RegularExpressionWriter} output */
public class RegularExpressionWriterProperties implements Serializable {
    private String kleeneStar;
    private String kleenePlus;
    private String option;

    private String repetitionStart;
    private String repetitionSeparator;
    private String repetitionEnd;

    private String rangeStart;
    private String rangeSeparator;
    private String rangeEnd;

    private String alternation;
    private String concatenation;

    private String openingParenthesis;
    private String closingParenthesis;

    private String emptySet;
    private String epsilon;

    public static RegularExpressionWriterProperties defaultTextProperties() {
        RegularExpressionWriterProperties props = new RegularExpressionWriterProperties();
        props.setKleeneStar("^*");
        props.setKleenePlus("^+");
        props.setOption("?");

        props.setRepetitionStart("^{");
        props.setRepetitionSeparator(",");
        props.setRepetitionEnd("}");

        props.setRangeStart("[");
        props.setRangeSeparator("-");
        props.setRangeEnd("]");

        props.setAlternation("+");
        props.setConcatenation("");

        props.setOpeningParenthesis("(");
        props.setClosingParenthesis(")");

        props.setEmptySet("∅");
        props.setEpsilon("ε");
        return props;
    }

    public static RegularExpressionWriterProperties defaultSafeTextProperties() {
        RegularExpressionWriterProperties props = defaultTextProperties();
        props.setEpsilon("epsilon");
        props.setEmptySet("emptyset");
        return props;
    }

    public static RegularExpressionWriterProperties defaultHtmlProperties() {
        RegularExpressionWriterProperties props = defaultTextProperties();
        props.setEpsilon("&epsilon;");
        props.setEmptySet("&empty;");

        props.setKleeneStar("<sup>*</sup>");
        props.setKleenePlus("<sup>+</sup>");

        props.setRepetitionStart("<sup>{");
        props.setRepetitionEnd("}</sup>");

        return props;
    }

    public static RegularExpressionWriterProperties defaultLaTeXProperties() {
        RegularExpressionWriterProperties props = defaultTextProperties();
        props.setEpsilon("\\epsilon");
        props.setEmptySet("\\emptyset");
        props.setOpeningParenthesis("\\left(");
        props.setClosingParenthesis("\\right)");
        props.setRepetitionStart("^{\\{");
        props.setRepetitionEnd("\\}}");
        props.setRangeStart("\\left[");
        props.setRangeEnd("\\right]");
        return props;
    }

    public void setKleeneStar(String kleeneStar) {
        this.kleeneStar = kleeneStar;
    }

    public void setKleenePlus(String kleenePlus) {
        this.kleenePlus = kleenePlus;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setRepetitionStart(String repetitionStart) {
        this.repetitionStart = repetitionStart;
    }

    public void setRepetitionSeparator(String repetitionSeparator) {
        this.repetitionSeparator = repetitionSeparator;
    }

    public void setRepetitionEnd(String repetitionEnd) {
        this.repetitionEnd = repetitionEnd;
    }

    public void setRangeStart(String rangeStart) {
        this.rangeStart = rangeStart;
    }

    public void setRangeSeparator(String rangeSeparator) {
        this.rangeSeparator = rangeSeparator;
    }

    public void setRangeEnd(String rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    public void setAlternation(String alternation) {
        this.alternation = alternation;
    }

    public void setConcatenation(String concatenation) {
        this.concatenation = concatenation;
    }

    public void setOpeningParenthesis(String openingParenthesis) {
        this.openingParenthesis = openingParenthesis;
    }

    public void setClosingParenthesis(String closingParenthesis) {
        this.closingParenthesis = closingParenthesis;
    }

    public void setEmptySet(String emptySet) {
        this.emptySet = emptySet;
    }

    public void setEpsilon(String epsilon) {
        this.epsilon = epsilon;
    }

    public String getKleeneStar() {
        return kleeneStar;
    }

    public String getKleenePlus() {
        return kleenePlus;
    }

    public String getOption() {
        return option;
    }

    public String getRepetitionStart() {
        return repetitionStart;
    }

    public String getRepetitionSeparator() {
        return repetitionSeparator;
    }

    public String getRepetitionEnd() {
        return repetitionEnd;
    }

    public String getRangeStart() {
        return rangeStart;
    }

    public String getRangeSeparator() {
        return rangeSeparator;
    }

    public String getRangeEnd() {
        return rangeEnd;
    }

    public String getAlternation() {
        return alternation;
    }

    public String getConcatenation() {
        return concatenation;
    }

    public String getOpeningParenthesis() {
        return openingParenthesis;
    }

    public String getClosingParenthesis() {
        return closingParenthesis;
    }

    public String getEmptySet() {
        return emptySet;
    }

    public String getEpsilon() {
        return epsilon;
    }
}
