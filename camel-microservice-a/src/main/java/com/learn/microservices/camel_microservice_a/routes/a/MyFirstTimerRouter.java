package com.learn.microservices.camel_microservice_a.routes.a;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class MyFirstTimerRouter extends RouteBuilder{
	
	@Autowired
	private GetCurrentTimeBean getCurrentTimeBean;
	
	@Autowired
	private SimpleLogggingProcessingComponent loggingComponent;

	@Override
	public void configure() throws Exception {
		// timer
		// transformation
		// log
		//Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
		
		from("timer:first-timer") 
		.log("${body}") //null
		.transform().constant("My Constant Message")
		.log("${body}") // My Constant Message
		//.transform().constant("Time now is "+LocalDateTime.now())
		
		//Processing 
		//Transformation
		
		.bean(getCurrentTimeBean,"getCurrentTime")
		.log("${body}") // Time now is 2024-12-26T15:05:37.643983
		.bean(loggingComponent)
		.log("${body}")
		.process(new SimpleLogggingProcesser())
		.to("log:first-timer");	//database	
	}

}

@Component
class GetCurrentTimeBean{
	public String getCurrentTime() {
		return "Time now is " + LocalDateTime.now();
	}
}

@Component
class SimpleLogggingProcessingComponent{
	Logger logger = LoggerFactory.getLogger(SimpleLogggingProcessingComponent.class);
	
	public void process(String message) {
		logger.info("SimpleLogggingProcessingComponent {}",message);
	}
}	
	
class SimpleLogggingProcesser implements Processor{
	Logger logger = LoggerFactory.getLogger(SimpleLogggingProcesser.class);
		
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("SimpleLogggingProcesser {}",exchange.getMessage().getBody());	
	}
}
