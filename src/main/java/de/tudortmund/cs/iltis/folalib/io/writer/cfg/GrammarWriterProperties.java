package de.tudortmund.cs.iltis.folalib.io.writer.cfg;

import java.io.Serializable;

public class GrammarWriterProperties implements Serializable {

    private String emptyProductionSymbol;
    private String arrow;
    private String grammarSymbolSeparator;
    private String productionInitiator;
    private String productionTerminator;

    public static GrammarWriterProperties defaultTextProperties() {
        GrammarWriterProperties properties = new GrammarWriterProperties();
        properties.setEmptyProductionSymbol("ε");
        properties.setArrow(" -> ");
        properties.setGrammarSymbolSeparator(" ");
        properties.setProductionInitiator("");
        properties.setProductionTerminator("\n");
        return properties;
    }

    public static GrammarWriterProperties defaultJSONProperties() {
        GrammarWriterProperties properties = defaultTextProperties();
        properties.setProductionInitiator("\"");
        properties.setProductionTerminator("\"");
        return properties;
    }

    public String getEmptyProductionSymbol() {
        return emptyProductionSymbol;
    }

    public void setEmptyProductionSymbol(String emptyProductionSymbol) {
        this.emptyProductionSymbol = emptyProductionSymbol;
    }

    public String getArrow() {
        return arrow;
    }

    public void setArrow(String arrow) {
        this.arrow = arrow;
    }

    public String getGrammarSymbolSeparator() {
        return grammarSymbolSeparator;
    }

    public void setGrammarSymbolSeparator(String grammarSymbolSeparator) {
        this.grammarSymbolSeparator = grammarSymbolSeparator;
    }

    public String getProductionInitiator() {
        return productionInitiator;
    }

    public void setProductionInitiator(String productionInitiator) {
        this.productionInitiator = productionInitiator;
    }

    public String getProductionTerminator() {
        return productionTerminator;
    }

    public void setProductionTerminator(String productionTerminator) {
        this.productionTerminator = productionTerminator;
    }
}
