package it.croway;

import org.apache.camel.builder.RouteBuilder;

import it.croway.model.Item;

public class MicroServiceRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		// post data
		from("platform-http:/items?consumes=application/json&produces=application/json&httpMethodRestrict=POST")
				.unmarshal().json(Item.class)
				.to("jpa:" + Item.class.getName())
				.marshal().json()
				.setHeader("CamelHttpResponseCode", constant(201))
				.log("${body}");

		// get data
		from("platform-http:/items?produces=application/json&httpMethodRestrict=GET")
				.to("jpa:" + Item.class.getName() + "?namedQuery=findAll")
				.marshal().json()
				.log("${body}");

		from("platform-http:/search?produces=application/json&httpMethodRestrict=GET")
				.transform().simple("{\n" +
						"    \"query\" : {\n" +
						"        \"match_all\" : {}\n" +
						"    }\n" +
						"}")
				.to("elasticsearch-rest-quarkus://elasticsearch?operation=Search&indexName=items")
				.marshal().json()
				.log("${body}");
	}

}
