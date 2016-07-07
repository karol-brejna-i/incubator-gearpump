package org.apache.gearpump.experiments.elastic.example.builders;

public class WordCountDocumentBuilder implements DocumentBuilder {
    private static final String JSON_TEMPLATE = "{\"word\":\"%s\",\"count\":%d,\"sinkIndexTime\":%d}";

    @Override
    public String build(Object... args) {
        return String.format(JSON_TEMPLATE, args[0], args[1], args[2]);
    }
}
