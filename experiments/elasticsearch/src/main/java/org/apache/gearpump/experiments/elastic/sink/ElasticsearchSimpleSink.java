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
package org.apache.gearpump.experiments.elastic.sink;


import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.gearpump.Message;
import org.apache.gearpump.streaming.sink.DataSink;
import org.apache.gearpump.streaming.task.TaskContext;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class ElasticsearchSimpleSink implements DataSink {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchSimpleSink.class);

    private final String cluster;
    private final String index;
    private final String type;

    private final List<InetSocketAddress> transportAddresses;
    private final Map<String, String> userConfig;

    private TransportClient transportClient;

    public ElasticsearchSimpleSink(String cluster, String index, String type,
                                   List<InetSocketAddress> transportAddresses,
                                   Map<String, String> userConfig) {
        this.cluster = cluster;
        this.index = index;
        this.type = type;
        this.userConfig = userConfig;
        this.transportAddresses = transportAddresses;
    }

    public void open(TaskContext context) {
        LOG.info("ElasticsearchSimpleSink-----------------open");
        LOG.debug("cluster = {} transportAddresses = {}", cluster, transportAddresses);

        Settings settings = Settings.settingsBuilder().put(userConfig).put("cluster.name", cluster).build();
        transportClient = TransportClient.builder().settings(settings).build();
        LOG.debug("settings: " + settings);

        for (InetSocketAddress address : transportAddresses) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(address));
        }

        LOG.info("ElasticsearchSimpleSink--------Created Elasticsearch TransportClient {}", transportClient);

        ImmutableList<DiscoveryNode> nodes = ImmutableList.copyOf(transportClient.connectedNodes());
        LOG.debug("Connected to these nodes: {}", nodes);

        if (nodes.isEmpty()) {
            throw new RuntimeException("Client is not connected to any Elasticsearch nodes!");
        }
    }

    public void write(Message message) {
        LOG.debug("ElasticsearchSimpleSink-----------------write: {}" + message);

        Object payload = message.msg();

        IndexRequestBuilder builder = transportClient.prepareIndex(index, type);
        String body = null;

        // if we have a (id, body) tuple, set id for the request and get to document body
        if (payload instanceof Tuple2) {
            builder.setId((String) ((Tuple2) payload)._1());
            body = (String) ((Tuple2) payload)._2();
        }

        // if it is only a string, assume it's the body alone
        if (payload instanceof String) {
            body = (String) payload;
        }

        if (!Strings.isNullOrEmpty(body)) {
            LOG.debug("body = " + body);
            IndexResponse response = builder.setSource(body).get();
            LOG.debug("Response from ES: {}", response);
        }

        LOG.debug("ElasticsearchSimpleSink-----------------finished");
    }

    public void close() {
        LOG.info("ElasticsearchSimpleSink----------------close");
        transportClient.close();
        LOG.debug("Elasticsearch client closed.");
    }
}
