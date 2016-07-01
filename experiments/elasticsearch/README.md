# Elasticsearch simple sink
This is a simple sink that allows for indexing a document in given index, of given type.
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

The third option would be tranSlated into sequence of requests of any of the above types.

# Example usage
The example is basically a wordcount example that indexes the output as Elasticsearch documents.

Example processing pipeline including simple elasticsearch sink could be represented like this:

```
(split : [word] )~>(sum : [word, count] )~>
                   (sum)~>(buildDocument :[json])~>(elasticSink)
                   (sum)~>(buildDocumentWithId :[id, json])~>(elasticSink)
```

("[word]" represents what type of message given node produces)


[TBD] Example image of Kibana diagrams.

# See the docs
* [Set up local development environment](doc/Local-development.md)
* Configure Elasticsearch (index, mapping)
* Configure Kibana

# TODO
* Show example kibana screens
* Think of Elasticsearch 'command' sink. This sink would allow for issuing more "complicated" commands like index, update, delete.

