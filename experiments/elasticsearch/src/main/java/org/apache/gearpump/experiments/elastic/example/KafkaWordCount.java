package org.apache.gearpump.experiments.elastic.example;

import com.typesafe.config.Config;
import org.apache.gearpump.cluster.ClusterConfig;
import org.apache.gearpump.cluster.UserConfig;
import org.apache.gearpump.cluster.client.ClientContext;
import org.apache.gearpump.experiments.elastic.example.tasks.BuildBody;
import org.apache.gearpump.experiments.elastic.example.tasks.BuildBodyWithId;
import org.apache.gearpump.experiments.elastic.example.tasks.KafkaSplit;
import org.apache.gearpump.experiments.elastic.example.tasks.Sum;
import org.apache.gearpump.experiments.elastic.sink.ConsoleSink;
import org.apache.gearpump.experiments.elastic.sink.ElasticsearchSimpleSink;
import org.apache.gearpump.partitioner.HashPartitioner;
import org.apache.gearpump.partitioner.Partitioner;
import org.apache.gearpump.streaming.javaapi.Graph;
import org.apache.gearpump.streaming.javaapi.Processor;
import org.apache.gearpump.streaming.javaapi.StreamApplication;
import org.apache.gearpump.streaming.kafka.KafkaSource;
import org.apache.gearpump.streaming.kafka.KafkaStoreFactory;
import org.apache.gearpump.streaming.kafka.util.KafkaConfig;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Java word count example that stores the results to Elasticsearch.
 */
public class KafkaWordCount {
    public static void main(String[] args) throws InterruptedException {
        main(ClusterConfig.defaultConfig(), args);
    }

    public static void main(Config akkaConf, String[] args) throws InterruptedException {
        ClientContext masterClient = new ClientContext(akkaConf);

        /*
         * Input params:
         *  inputTopic
          * zookeeper servers
          * bootstrap servers
          *
          * elasticsearch cluster name
          * nodes addresses "127.0.0.1":9300
          *
          * ?? index name
          * ?? type name
         */


        String sourceTopic = "test";
        String appName = "KafkaWordCount";
        Properties props = new Properties();
        props.put(KafkaConfig.ZOOKEEPER_CONNECT_CONFIG, "localhost:2181");
        props.put(KafkaConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //props.put(KafkaConfig.CONSUMER_START_OFFSET_CONFIG, new java.lang.Long(OffsetRequest.LatestTime));
        props.put(KafkaConfig.CHECKPOINT_STORE_NAME_PREFIX_CONFIG, appName);

        KafkaStoreFactory checkpointStoreFactory = new KafkaStoreFactory(props);
        KafkaSource kafkaSource = new KafkaSource(sourceTopic, props);
        kafkaSource.setCheckpointStore(checkpointStoreFactory);

        Processor sourceProcessor = Processor.source(kafkaSource, 1, "kafkaSource", UserConfig.empty(), masterClient.system());

        // Task for producing words
        int splitTaskNumber = 1;
        Processor split = new Processor(KafkaSplit.class).withParallelism(splitTaskNumber);

        // Task for computing of word counts
        int sumTaskNumber = 1;
        Processor sum = new Processor(Sum.class).withParallelism(sumTaskNumber);

        ConsoleSink consoleSink = new ConsoleSink();
        Processor consoleSinkProcessor = Processor.sink(consoleSink, 1, "consoleSink", UserConfig.empty(), masterClient.system());

        // Tasks that build json for indexing in elasticsearch
        Processor<BuildBody> buildBody = new Processor(BuildBody.class);
        Processor<BuildBodyWithId> buildBodyWithId = new Processor(BuildBodyWithId.class);


        List<InetSocketAddress> transportAddresses = new ArrayList<>();
        try {
            transportAddresses.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // first elasticsearch sink
        ElasticsearchSimpleSink elasticSink = new ElasticsearchSimpleSink("gearpump-test", "myindex", "mytype", transportAddresses, new HashMap());
        Processor elasticSinkProcessor = Processor.sink(elasticSink, 1, "elasticSink", UserConfig.empty(), masterClient.system());

        // second elasticsearch sink
        ElasticsearchSimpleSink elasticSink2 = new ElasticsearchSimpleSink("gearpump-test", "myindex2", "mytype2", transportAddresses, new HashMap());
        Processor elasticSinkProcessor2 = Processor.sink(elasticSink2, 1, "elasticSink-id", UserConfig.empty(), masterClient.system());

        // construct the graph
        Graph graph = new Graph();
        graph.addVertex(sourceProcessor);
        graph.addVertex(split);
        graph.addVertex(sum);
        graph.addVertex(consoleSinkProcessor);
        graph.addVertex(buildBody);
        graph.addVertex(buildBodyWithId);
        graph.addVertex(elasticSinkProcessor);
        graph.addVertex(elasticSinkProcessor2);

        Partitioner partitioner = new HashPartitioner();
        graph.addEdge(sourceProcessor, partitioner, split);
        graph.addEdge(split, partitioner, sum);
        graph.addEdge(split, partitioner, consoleSinkProcessor);
        graph.addEdge(sum, partitioner, buildBody);
        graph.addEdge(sum, partitioner, buildBodyWithId);

        graph.addEdge(buildBody, partitioner, elasticSinkProcessor);
        graph.addEdge(buildBodyWithId, partitioner, elasticSinkProcessor2);

        UserConfig conf = UserConfig.empty();
        StreamApplication app = new StreamApplication(appName, conf, graph);

        masterClient.submit(app);
        masterClient.close();
    }
}