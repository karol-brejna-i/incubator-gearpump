curl -XPUT http://localhost:9200/myindex

curl -XPUT http://localhost:9200/myindex/_mapping/mytype -d '{"properties":{"word": { "type": "string" },"count": { "type": "integer" },"sinkIndexTime": { "type": "date" }}}'
curl -XPUT http://localhost:9200/myindex/_mapping/mytype2 -d '{"properties":{"count": { "type": "integer" },"sinkIndexTime": { "type": "date" }}}'
