package org.apache.gearpump.experiments.elastic.example.builders;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class Kafka2Words {
    private static final String PUNCTUATION_REGEXP = "[\\u2000-\\u206F\\u2E00-\\u2E7F\\'!\"#$%&()*+,./:;<=>?@\\[\\]^`{|}~]";
    private static final String LINKS_REGEXP = "(https?://([-\\w\\.]+[-\\w])+(:\\d+)?(/([\\w/_\\.#-]*(\\?\\S+)?[^\\.\\s])?)?)";
    public static final Pattern PUNCTUATION_PATTERN = Pattern.compile(PUNCTUATION_REGEXP);
    public static final Pattern LINKS_PATTERN = Pattern.compile(LINKS_REGEXP);

    String convertPayload(Object payload) {
        String text = "";
        try {
            text = new String((byte[]) payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return text;
    }

    String[] splitWords(String text) {
        return text.split("\\s+");
    }

    String clean(String input) {
        // remove links
        String text = LINKS_PATTERN.matcher(input).replaceAll("");
        // remove punctuation (removed _ and - from the replacement)
        return PUNCTUATION_PATTERN.matcher(text).replaceAll(" ");
    }

    public String[] convert(Object payload) {
        return splitWords(clean(convertPayload(payload)));
    }

}
