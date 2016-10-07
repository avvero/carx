package com.avvero.carx.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Routing configuration
 * Created by fxdev-belyaev-ay on 05.10.16.
 */
@Component
public class CommonRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("log:input") //it will be good to store undelivered in something more persistence
                        .maximumRedeliveries(5)
                        .redeliveryDelay(1000)
                        .backOffMultiplier(2)
                        .logRetryStackTrace(true)
        );

        from("direct:customer-data-update")
                .inOnly("bean:customerDataService?method=updateCustomerData");
        from("seda:customer-data-update?concurrentConsumers=10")
                .inOnly("direct:customer-data-update");

        from("direct:customer-data-fetch")
                .to("bean:customerDataService?method=findOneCustomerDataByUuid");

        from("direct:activity")
                .inOnly("bean:customerActivityService?method=save");
        from("seda:activity?concurrentConsumers=10")
                .inOnly("direct:activity");

    }

}
