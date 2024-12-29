package com.learn.microservices.camel_microservice_a.routes.pattern;

import java.util.List;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.learn.microservices.camel_microservice_a.CurrencyExchange;

//@Component
public class EipPatternsRouter extends RouteBuilder{
	@Autowired
	DynamicRouterBean dynamicRouterBean;
	
	@Autowired
	SplitterComponent splitter;

	@Override
	public void configure() throws Exception {
		
		//Tracing
		getContext().setTracing(true);
		//Dead Letter Queue
		errorHandler(deadLetterChannel("activemq:dead-letter-queue"));
		
		/*
		Patterns: 
		1. Pipeline Pattern (or Default Pattern)
		2. Content Based Routing - choice
		3. Multicast Pattern
		*/
		
		//from("timer: multicast?period=10000")
		//.multicast() // Multicast Pattern
		//.to("log:something1", "log:something2", "log:something3");
		
		//Splitter Pattern
		//from("file:files/csv")
		//.unmarshal().csv()
		//.split(body())
		//.to("log:split-files");
		//.to("activemq:split-queue");
		
		//from("file:files/csv")
		//.convertBodyTo(String.class)
		//.split(body(),",")
		//.split(method(splitter))
		//.to("activemq:split-queue");
		
		//Aggregate
		//Messages => Aggregate => Endpoint
		//to, 3
		from("file:files/aggregate-json")
		.unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
		.aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
		.completionSize(3)
		.to("log:aggregate-json");	
		
		//Routing Slip Pattern
		
		String routingSlip = "direct:endpoint1, direct:endpoint2";
		
		from("timer:routingSlip?period={{timePeriod}}")
		.transform().constant("My Message is Hardcoded")
		.routingSlip(simple(routingSlip));
		
		from("direct:endpoint1")
		.wireTap("log:wire-tap") //WireTap
		.log("{{endpoint-for-logging}}");
		
		from("direct:endpoint2")
		.log("log:directendpoint2");
		
		from("direct:endpoint3")
		.log("log:directendpoint3");
		
		//Dynamic Routing
		from("timer:dynamicRouting?period=10000")
		.transform().constant("My Message is Hardcoded")
		.dynamicRouter(method(dynamicRouterBean));
	}
}

@Component
class SplitterComponent{
	public List<String> splitInput(String body){
		return List.of("ABC","DEF","GHI");
	}
}

@Component
class DynamicRouterBean{
	
	Logger logger = LoggerFactory.getLogger(DynamicRouterBean.class);
	int invocations;
	
	public String decideTheNextEndPoint(
			@ExchangeProperties Map<String,String> properties,
			@Headers Map<String,String> headers,
			@Body String body) {
		logger.info("{} {} {}", properties, headers, body);
		
		invocations++;
		if(invocations%3==0)
			return "direct:endpoint1";
		if(invocations%3==1)
			return "direct:endpoint2,direct:endpoint3";
		
		return null;
	}
}
