package org.apache.gearpump.experiments.elastic.example.builders;


import org.apache.gearpump.Message;
import scala.Tuple2;

public class ElasticMsgWithIdBuilder implements MessageBuilder{

    private DocumentBuilder docBuilder;

    private Long now() {
        return System.currentTimeMillis();
    }

    public ElasticMsgWithIdBuilder(DocumentBuilder docBuilder) {
        this.docBuilder = docBuilder;
    }

    public Message build(Object payload) {
        Message result = null;
        if (payload instanceof Tuple2) {
            // decode word/count
            String word = (String) ((Tuple2) payload)._1();
            Integer count = (Integer) ((Tuple2) payload)._2();
            String body = docBuilder.build(word, count, now());

            // prepare indexing message
            Tuple2<String, String> tuple = new Tuple2<>(word, body);
            result = new Message(tuple, now());
        }

        return result;
    }

}
