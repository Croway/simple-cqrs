package it.croway;

import org.junit.jupiter.api.Test;

import org.hamcrest.Matchers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(ElasticsearchResource.class)
@QuarkusTestResource(CDCResource.class)
public class ArchitectureTest {

	@TestHTTPResource("/items")
	URI service;

	@TestHTTPResource("/search")
	URI searchService;

	@Test
	public void test() throws Exception {
		String uuid = UUID.randomUUID().toString();

		RestAssured.given()
				.header("Content-type", "application/json")
				.and()
				.body("{\"description\": \"" + uuid + "\"}")
				.when().post(service).then().statusCode(201);

		RestAssured.when()
				.get(service).then().statusCode(200).body("[0].description", Matchers.equalTo(uuid));

		Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> RestAssured.when()
				.get(searchService).then().statusCode(200).body("totalHits.value", Matchers.equalTo(1))
				.and().body("hits[0].sourceAsMap.description", Matchers.equalTo(uuid)));

		for (int i = 0; i < 100; i++) {
			RestAssured.given()
					.header("Content-type", "application/json")
					.and()
					.body("{\"description\": \"" + UUID.randomUUID() + "\"}")
					.when().post(service).then().statusCode(201);
		}

		Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> RestAssured.when()
				.get(searchService).then().statusCode(200).body("totalHits.value", Matchers.equalTo(101)));
	}
}
