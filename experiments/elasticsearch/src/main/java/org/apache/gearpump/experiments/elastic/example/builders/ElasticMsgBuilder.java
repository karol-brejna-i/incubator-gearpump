package org.apache.gearpump.experiments.elastic.example.builders;

import org.apache.gearpump.Message;
import scala.Tuple2;

public class ElasticMsgBuilder  implements MessageBuilder{

    private DocumentBuilder docBuilder;

    private Long now() {
        return System.currentTimeMillis();
    }

    public ElasticMsgBuilder(DocumentBuilder docBuilder) {
        this.docBuilder = docBuilder;
    }

    public Message build(Object payload) {
        Message result = null;
        if (payload instanceof Tuple2) {
            String word = (String) ((Tuple2) payload)._1();
            Integer count = (Integer) ((Tuple2) payload)._2();
            String body = docBuilder.build(word, count, now());
            result = new Message(body, now());
        }

        return result;
    }
}
