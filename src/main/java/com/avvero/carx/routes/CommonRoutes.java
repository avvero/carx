package com.avvero.carx.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Routing configuration
 * Created by fxdev-belyaev-ay on 05.10.16.
 */
@Component
public class CommonRoutes extends RouteBuilder {

    @Value("${carx.customerDataUpdate.concurrentConsumers}")
    public Integer customerDataUpdateConcurrentConsumers;
    @Value("${carx.customerActivity.concurrentConsumers}")
    public Integer customerActivityUpdateConcurrentConsumers;
    @Value("${carx.deadLetterChannel.maximumRedeliveries}")
    public Integer deadLetterChannelMaximumRedeliveries;
    @Value("${carx.deadLetterChannel.redeliveryDelay}")
    public Integer deadLetterChannelRedeliveryDelay;

    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("log:input") //it will be good to store undelivered in something more persistence
                        .maximumRedeliveries(deadLetterChannelMaximumRedeliveries)
                        .redeliveryDelay(deadLetterChannelRedeliveryDelay)
                        .backOffMultiplier(2)
                        .logRetryStackTrace(true)
        );

        from("direct:customer-data-update")
                .inOnly("bean:customerDataService?method=updateCustomerData")
                .errorHandler(defaultErrorHandler());

        from("seda:customer-data-update?concurrentConsumers=" + customerDataUpdateConcurrentConsumers)
                .inOnly("bean:customerDataService?method=updateCustomerData");

        from("direct:customer-data-fetch")
                .to("bean:customerDataService?method=findOneCustomerDataByUuid")
                .errorHandler(defaultErrorHandler());

        from("direct:activity")
                .inOnly("bean:customerActivityService?method=save")
                .errorHandler(defaultErrorHandler());

        from("seda:activity?concurrentConsumers=" + customerActivityUpdateConcurrentConsumers)
                .inOnly("bean:customerActivityService?method=save");

    }

}
