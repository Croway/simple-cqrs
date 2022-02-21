package it.croway;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class PostgresResource implements
		QuarkusTestResourceLifecycleManager {

	private static String DATABASE_NAME = "qa";
	private static String DATABASE_USERNAME = "quarkus";
	private static String DATABASE_PASSWORD = "quarkus";

	private static PostgreSQLContainer<?> db =
			new PostgreSQLContainer<>(DockerImageName.parse("debezium/postgres:14").asCompatibleSubstituteFor("postgres"))
					.withDatabaseName(DATABASE_NAME)
					.withUsername(DATABASE_USERNAME)
					.withPassword(DATABASE_PASSWORD);

	@Override
	public Map<String, String> start() {
		db.start();

		return Map.of(
				"quarkus.datasource.jdbc.url", db.getJdbcUrl(),
				"postgres.datasource.hostname", db.getHost(),
				"postgres.datasource.port", String.valueOf(db.getMappedPort(db.POSTGRESQL_PORT)),
				"quarkus.datasource.username", DATABASE_USERNAME,
				"quarkus.datasource.password", DATABASE_PASSWORD,
				"postgres.datasource.database.name", DATABASE_NAME
		);
	}

	@Override
	public void stop() {
		db.stop();
	}
}
