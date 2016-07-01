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

public class Sum extends Task {

    private Logger LOG = super.LOG();
    private HashMap<String, Integer> wordCount = new HashMap<String, Integer>();

    private Long now() {
        return System.currentTimeMillis();
    }

    public Sum(TaskContext taskContext, UserConfig userConf) {
        super(taskContext, userConf);
    }

    @Override
    public void onStart(StartTime startTime) {
        System.out.println("-----sum onStart");
    }


    /**
     * Expects:
     *  word (String) - a word to be counted
     *
     * Issues:
     *  word, count (String, Integer) - a tuple that consists from a word and it's frequency (count)
     * @param message
     */
    @Override
    public void onNext(Message message) {
        System.out.println("-----sum onNext: " + message);
        String word = (String) (message.msg());
        Integer current = wordCount.get(word);
        if (current == null) {
            current = 0;
        }
        Integer newCount = current + 1;
        wordCount.put(word, newCount);

        Tuple2<String, Integer> tuple = new Tuple2<>(word, newCount);
        context.output(new Message(tuple, now()));
    }
}
