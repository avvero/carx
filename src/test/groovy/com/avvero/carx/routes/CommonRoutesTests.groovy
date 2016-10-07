package com.avvero.carx.routes

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.constants.CommonConstants
import com.avvero.carx.domain.Activity
import com.avvero.carx.service.CustomerActivityService
import com.avvero.carx.service.CustomerDataService
import org.apache.camel.ProducerTemplate
import org.bson.Document
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Matchers.any
import static org.mockito.Matchers.anyString

/**
 * Tests for routes
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
    CustomerActivityService customerActivityService;

    public static class MockConfiguration {
        @Bean
        @Profile("test")
        CustomerDataService customerDataService() {
            return Mockito.mock(CustomerDataService.class)
        };
        @Bean
        @Profile("test")
        CustomerActivityService customerActivityService() {
            return Mockito.mock(CustomerActivityService.class)
        };
    }

    @Unroll
    def "Message that has been sent to #uri goes to customerDataService.updateCustomerData"(){
        setup:
            def doc = new Document()
        when:
            producerTemplate.sendBodyAndHeader(uri, doc, CommonConstants.UUID, uuid);
            if (isAsync) Thread.sleep(1000)
        then:
            Mockito.verify(customerDataService).updateCustomerData(uuid, doc)
        where:
            uuid  | uri                           | isAsync
            "aaa" | "direct:customer-data-update" | false
            "bbb" | "seda:customer-data-update"   | true
    }

    def "Exception will be rethrown in sync route 'direct:customer-data-update'"() {
        when:
            Mockito.doThrow(new RuntimeException()).when(customerDataService).updateCustomerData(anyString(),
                    any(Document.class))
            producerTemplate.sendBodyAndHeader(uri, new Document(), CommonConstants.UUID, "");
        then:
            thrown(RuntimeException)
        where:
            uri  = "direct:customer-data-update"
    }

    def "Exception will not be rethrown in async route 'seda:customer-data-update'"() {
        when:
            Mockito.doThrow(new RuntimeException()).when(customerDataService).updateCustomerData(anyString(),
                    any(Document.class))
            producerTemplate.sendBodyAndHeader(uri, new Document(), CommonConstants.UUID, "");
        then:
            noExceptionThrown()
        where:
            uri  = "seda:customer-data-update"
    }

    @Unroll
    def "Message that has been sent to #uri goes to customerDataService.findOneCustomerDataByUuid"(){
        setup:
            def doc = new Document()
        when:
            Mockito.doReturn(doc).when(customerDataService).findOneCustomerDataByUuid(uuid)
        then:
            producerTemplate.requestBody(uri, uuid) == doc
            Mockito.verify(customerDataService).findOneCustomerDataByUuid(uuid) == null //requires expression in 'then'
        where:
            uuid  | uri
            "aaa" | "direct:customer-data-fetch"
    }

    def "Exception will be rethrown in sync route 'direct:customer-data-fetch'"() {
        when:
            Mockito.doThrow(new RuntimeException()).when(customerDataService).findOneCustomerDataByUuid(anyString())
            producerTemplate.requestBody("direct:customer-data-fetch", "")
        then:
            thrown(RuntimeException)
    }

    @Unroll
    def "Message that has been sent to #uri goes to customerActivityService.save"(){
        setup:
            def activity = new Activity(value: 100)
        when:
            producerTemplate.sendBodyAndHeader(uri, activity, CommonConstants.UUID, uuid);
            if (isAsync) Thread.sleep(1000)
        then:
            Mockito.verify(customerActivityService).save(uuid, activity)
        where:
            uuid  | uri               | isAsync
            "ccc" | "direct:activity" | false
            "ddd" | "seda:activity"   | true
    }

    def "Exception will be rethrown in sync route 'direct:activity'"() {
        when:
            Mockito.doThrow(new RuntimeException()).when(customerActivityService).save(anyString(),
                    any(Activity.class))
            producerTemplate.sendBodyAndHeader(uri, new Activity(value: 100), CommonConstants.UUID, "");
        then:
            thrown(RuntimeException)
        where:
            uri  = "direct:activity"
    }

    def "Exception will not be rethrown in assync route 'seda:activity'"() {
        when:
            Mockito.doThrow(new RuntimeException()).when(customerActivityService).save(anyString(),
                    any(Activity.class))
            producerTemplate.sendBodyAndHeader(uri, new Activity(value: 100), CommonConstants.UUID, "");
        then:
            noExceptionThrown()
        where:
            uri  = "seda:activity"
    }
}
