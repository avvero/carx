package com.avvero.carx.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Created by fxdev-belyaev-ay on 05.10.16.
 */
@Component
public class CommonRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:customer-data")
                .inOnly("seda:customer-data");
        from("seda:customer-data?concurrentConsumers=10")
                .to("bean:customerDataService?method=updateCustomerData");

        from("direct:activity")
                .inOnly("seda:activity");
        from("seda:activity?concurrentConsumers=10")
                .to("bean:customerActivityService?method=save");

    }

}
