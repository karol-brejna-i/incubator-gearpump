/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gearpump.experiments.elastic.example;

import org.apache.gearpump.Message;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.streaming.javaapi.Task;
import org.apache.gearpump.streaming.task.StartTime;
import org.apache.gearpump.streaming.task.TaskContext;
import org.slf4j.Logger;
import scala.Tuple2;

import java.util.HashMap;

public class BuildBody extends Task {
    private static final String JSON_TEMPLATE = "{\"word\":\"%s\", \"count\":%d, \"sinkIndexTime\":%d}";

    private Logger LOG = super.LOG();
    private HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

    private Long now() {
        return System.currentTimeMillis();
    }

    public BuildBody(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
    }

    @Override
    public void onStart(StartTime startTime) {
        System.out.println("-----buildBody onStart");
    }


    /**
     * Expects:
     *  word, count (String, Integer) - a tuple that consists from a word and it's frequency (count)
     * Issues:
     *  json-document (String) - a json representation of the message. sth like {"word": count}
     *
     * @param message
     */
    @Override
    public void onNext(Message message) {
        System.out.println("-----buildBody onNext: " + message);
        Object payload = message.msg();

        // we expect (word, count) tuple
        if (payload instanceof Tuple2) {
            String word = (String) ((Tuple2) payload)._1();
            Integer count = (Integer) ((Tuple2) payload)._2();
            String body = String.format(JSON_TEMPLATE, word, count, now());
            context.output(new Message(body, now()));
        } else {
            LOG.warn("I don't know what to do with the message :(");
        }
    }
}