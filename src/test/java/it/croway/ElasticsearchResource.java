package it.croway;

import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class ElasticsearchResource implements
		QuarkusTestResourceLifecycleManager {

	private static final int ELASTICSEARCH_PORT = 9200;

	private static ElasticsearchContainer container =
			new ElasticsearchContainer(
					DockerImageName
							.parse("docker.elastic.co/elasticsearch/elasticsearch-oss")
							.withTag("7.10.1"));

	@Override
	public Map<String, String> start() {
		container.start();

		String hostAddresses = String.format("localhost:%s", container.getMappedPort(ELASTICSEARCH_PORT));

		return Map.of(
				"quarkus.elasticsearch.hosts", hostAddresses,
				"camel.component.elasticsearch-rest.autowired-enabled", "false",
				"camel.component.elasticsearch-rest.host-addresses", hostAddresses
				);
	}

	@Override
	public void stop() {
		container.stop();
	}
}
