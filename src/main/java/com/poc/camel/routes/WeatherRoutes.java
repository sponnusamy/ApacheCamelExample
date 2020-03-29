package com.poc.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

import com.poc.camel.representations.ForcastResponse;

@Component
public class WeatherRoutes extends RouteBuilder {

        @Override
        public void configure() {
            restConfiguration().component("servlet").bindingMode(RestBindingMode.json);

            rest("/forecast").produces("application/json").get("").to("direct:forecast");

            // @formatter:off
            from("direct:forecast")
                .setHeader(Exchange.HTTP_METHOD, simple("GET"))
                .setHeader(Exchange.HTTP_PATH, constant(""))
                .setHeader(Exchange.HTTP_URI, simple(""))
                .setHeader(Exchange.TO_ENDPOINT, simple(""))
                .setHeader(Exchange.HTTP_URL, simple(""))
                .setHeader(Exchange.HTTP_QUERY, simple("appid={{openweather.appid}}&q=${header.city}"))
                .to("{{openweather.host}}?bridgeEndpoint=true")
                .unmarshal().json(JsonLibrary.Jackson, ForcastResponse.class).process((exchange)->{
                    ForcastResponse response = (ForcastResponse) exchange.getIn().getBody();
                    exchange.getIn().setBody(response.getList());
                });
         // @formatter:on
        }
        
}
