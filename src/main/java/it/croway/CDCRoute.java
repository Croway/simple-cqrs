package it.croway;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.elasticsearch.ElasticsearchComponent;
import org.apache.kafka.connect.data.Struct;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import java.util.UUID;

@ApplicationScoped
public class CDCRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("debezium-postgres:dbz-test?offsetStorageFileName={{cdc.storage.filename}}" +
				"&databaseHostname={{postgres.datasource.hostname}}" +
				"&databasePort={{postgres.datasource.port}}" +
				"&databaseUser={{quarkus.datasource.username}}" +
				"&databasePassword={{quarkus.datasource.password}}" +
				"&databaseDbname={{postgres.datasource.database.name}}" +
				"&databaseServerName=cdc-connector")
				.log("Event received from Debezium : ${body}")
				.log("    with this identifier ${headers.CamelDebeziumIdentifier}")
				.log("    with these source metadata ${headers.CamelDebeziumSourceMetadata}")
				.log("    the event occured upon this operation '${headers.CamelDebeziumSourceOperation}'")
				.log("    with the key ${headers.CamelDebeziumKey}")
				.log("    the previous value is ${headers.CamelDebeziumBefore}")
				.process(exchange -> {
					Struct body = exchange.getMessage().getBody(Struct.class);

					String description = body.getString("description");

					IndexRequest indexRequest = new IndexRequest("items");
					indexRequest.source("{ \"description\": \"" + description + "\"}", XContentType.JSON);

					exchange.getMessage().setBody(indexRequest);
				})
				.to("elasticsearch-rest-quarkus://elasticsearch?operation=Index&indexName=items");
	}

	@Named("elasticsearch-rest-quarkus")
	public ElasticsearchComponent elasticsearchQuarkus(RestClient client) {
		// Use the RestClient bean created by the Quarkus ElasticSearch extension
		ElasticsearchComponent component = new ElasticsearchComponent();
		component.setClient(client);
		return component;
	}
}
