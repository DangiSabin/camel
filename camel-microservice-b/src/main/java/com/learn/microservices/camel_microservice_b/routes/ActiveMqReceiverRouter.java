package com.learn.microservices.camel_microservice_b.routes;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.learn.microservices.camel_microservice_b.CurrencyExchange;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder{
	@Autowired
	MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;
	
	@Autowired
	MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

	@Override
	public void configure() throws Exception {
		from("activemq: my-activemq-queue")
		.unmarshal(createEncryptor())
		//.unmarshal().json(JsonLibrary.Jackson,CurrencyExchange.class)
		//.bean(myCurrencyExchangeProcessor)
		//.bean(myCurrencyExchangeTransformer)
		.to("log: received-message-from-activemq");
		
		//from("activemq: my-activemq-xml-queue")
		//.unmarshal()
		//.jacksonXml(CurrencyExchange.class)
		//.to("log: received-message-from-activemq");
		
		//from("activemq:split-queue")
		//.to("log:received-message-from-activemq");
	}
	
	private CryptoDataFormat createEncryptor() throws KeyStoreException, IOException, NoSuchAlgorithmException,
	CertificateException, UnrecoverableKeyException {
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		ClassLoader classLoader = getClass().getClassLoader();
		keyStore.load(classLoader.getResourceAsStream("myDesKey.jceks"), "someKeystorePassword".toCharArray());
		Key sharedKey = keyStore.getKey("myDesKey", "someKeyPassword".toCharArray());

		CryptoDataFormat sharedKeyCrypto = new CryptoDataFormat("DES", sharedKey);
		return sharedKeyCrypto;
	}
}

@Component
class MyCurrencyExchangeProcessor{
	
	Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeProcessor.class);
	
	public void processMessage(CurrencyExchange currencyExchange) {
		logger.info("Do some processing with currencyExchange.getConversionMultiple() value which is "+currencyExchange.getConversionMultiple());
	}
}
	
@Component
class MyCurrencyExchangeTransformer{
	Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeTransformer.class);
		
	public CurrencyExchange processMessage(CurrencyExchange currencyExchange) {
		currencyExchange.setConversionMultiple(
		currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN));
			
		logger.info("Do some transformation with currencyExchange.getConversionMultiple() value which is "+currencyExchange.getConversionMultiple());
		return currencyExchange;
}
	
}
