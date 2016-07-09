# Elasticsearch simple sink
This is a simple sink that allows for indexing a document in Elasticsearch, in given index of given type.
The sink assumes it gets json representation of the document to be indexed. No transformations, no mappings are done here.

## Configuring the sink
When creating the sink, the developer should supply:

* information required for the connection (cluster name, nodes' addresses)
* indexing information (index name, document type)

## Message format
The sink consumes the following payload:

* String - it assumes that the string contains json representation of indexed document. The payload will be indexed as a new document (new id)
* Tuple2[String, String] - it assumes that the firs string is the id of the document, the second string is the json representation of the document. It allows for updating existing documents in Elasticserach.
* A sequence of any of above ([TBD])

## Elasticsearch API operation
The first message format would translate into https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#_automatic_id_generation.

The second format would be https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html#docs-index_ request.

The third option would be translated into sequence of requests of any of the above types.

# Example usage
To create the sink that connect to local Elasticsearch node you could do something like this:

```
        List<InetSocketAddress> transportAddresses = new ArrayList<>();
        transportAddresses.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9300));

        ElasticsearchSimpleSink elasticSink = new ElasticsearchSimpleSink("gearpump-test", "myindex", "mytype", transportAddresses, new HashMap());
        Processor elasticSinkProcessor = Processor.sink(elasticSink, 1, "elasticSink", UserConfig.empty(), masterClient.system());
```

For a bit more advanced example on how to use the sink, please take a look at https://github.com/apache/incubator-gearpump/experiments/elasticsearch-example.


