package com.learn.microservices.camel_microservice_a.routes.a;

import java.time.LocalDateTime;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFirstTimerRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// timer
		// transformation
		// log
		//Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
		
		from("timer:first-timer") 
		//.transform().constant("My Constant Message")
		//.transform().constant("Time now is "+LocalDateTime.now())
		.bean("getCurrentTimeBean")
		.to("log:first-timer");		
	}

}

@Component
class GetCurrentTimeBean{
	public String getCurrentTime() {
		return "Time now is "+LocalDateTime.now();
	}
}
