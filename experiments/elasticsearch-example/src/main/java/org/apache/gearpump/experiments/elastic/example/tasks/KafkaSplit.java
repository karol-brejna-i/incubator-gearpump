package org.apache.gearpump.experiments.elastic.example.tasks;

import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.experiments.elastic.example.builders.Kafka2Words;
import org.apache.gearpump.streaming.javaapi.Task;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.TaskContext;
import org.slf4j.Logger;

import java.util.Arrays;

public class KafkaSplit extends Task {
    private Logger LOG = super.LOG();
    private Long now() {
        return System.currentTimeMillis();
    }


    /** takes care of converting message from kafka source to array of words. */
    private Kafka2Words converter = new Kafka2Words();

    public KafkaSplit(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
    }

    @Override
    public void onStart(StartTime startTime) {
        System.out.println("-----kafkaSplit onStart");
    }

    @Override
    public void onNext(Message message) {
        LOG.info("-----kafkaSplit onNext: {}", now());
        String[] words = converter.convert(message.msg());
        LOG.info("words: " + Arrays.asList(words));
        for (String word : words) {
            context.output(new Message(word, now()));
        }
    }
}