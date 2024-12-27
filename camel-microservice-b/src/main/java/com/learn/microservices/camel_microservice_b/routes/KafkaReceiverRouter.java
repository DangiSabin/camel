package com.learn.microservices.camel_microservice_b.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiverRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		from("kafka:myKafkaTopic")
		.to("log: received-message-from-kafka");
	}
}

	
