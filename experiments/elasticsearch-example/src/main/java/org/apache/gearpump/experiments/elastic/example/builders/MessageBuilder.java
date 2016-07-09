package org.apache.gearpump.experiments.elastic.example.builders;


import org.apache.gearpump.Message;

/**
 * Builds a Gearpump streaming message to be passed to Elasticsearch sink.
 */
public interface MessageBuilder {
    public Message build(Object payload);

}
