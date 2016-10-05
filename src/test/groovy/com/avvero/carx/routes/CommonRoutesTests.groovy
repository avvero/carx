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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

/**
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

    @Unroll
    def "T"(){
        setup:
            def doc = new Document()
            def uuid = "aaa"
        when:
            producerTemplate.sendBodyAndHeader(uri, doc, CommonConstants.UUID, uuid);
        then:
            Thread.sleep(1000)
            Mockito.verify(customerDataService).updateCustomerData(uuid, doc)
        where:
            uri                           |_
            "direct:customer-data-update" |_
            "seda:customer-data-update"   |_
    }



}
