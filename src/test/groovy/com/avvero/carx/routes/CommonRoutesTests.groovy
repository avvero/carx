package com.avvero.carx.routes

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.constants.CommonConstants
import com.avvero.carx.service.CustomerDataService
import org.apache.camel.ProducerTemplate
import org.bson.Document
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Issue https://github.com/mockito/mockito/pull/171
 * Spring boot test does not support mockito 2
 * @author Avvero
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = [App, LocationRepositoryTestConfiguration, MockConfiguration],
        loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
class CommonRoutesTests extends Specification {

    @Autowired
    ProducerTemplate producerTemplate;
    @Autowired
    CustomerDataService customerDataService;
    @Autowired
    GenericApplicationContext applicationContext;

    public static class MockConfiguration {
        @Bean
        @Profile("test")
        CustomerDataService customerDataService() {
            return Mockito.mock(CustomerDataService.class)
        };
    }

    @Unroll
    def "T"(){
        setup:
            def doc = new Document()
        when:
            producerTemplate.sendBodyAndHeader(uri, doc, CommonConstants.UUID, uuid);
        then:
            Thread.sleep(1000)
            Mockito.verify(customerDataService).updateCustomerData(uuid, doc)
        where:
            uuid  | uri                           |_
            "aaa" | "direct:customer-data-update" |_
            "bbb" | "seda:customer-data-update"   |_
    }
}
