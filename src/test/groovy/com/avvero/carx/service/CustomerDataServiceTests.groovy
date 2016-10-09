package com.avvero.carx.service

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.constants.CommonConstants
import com.avvero.carx.dao.jpa.CustomerRepository
import com.avvero.carx.dao.mongo.CustomerDataRepository
import org.apache.camel.ProducerTemplate
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll


/**
 * Tests for CustomerDataService
 * @author Avvero
 */
@ContextConfiguration(classes = [App, LocationRepositoryTestConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
class CustomerDataServiceTests extends Specification {

    @Autowired
    CustomerDataService customerDataService;
    @Autowired
    CustomerDataRepository customerDataRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProducerTemplate producerTemplate;

    def cleanup() {
        customerRepository.deleteAll()
        customerDataRepository.deleteAll()
    }

    @Unroll
    @Rollback
    def "CustomerData can be inserted or updated"() {
        setup:
            customerDataService.updateCustomerData("a1", new Document([money: 100, country: "RUS"]))
        when:
            customerDataService.updateCustomerData(uuid, new Document(json))
            def fetchedJson = customerDataService.findOneCustomerDataByUuid(uuid)
        then:
            fetchedJson.money == json.money
            fetchedJson.country == json.country
            customerRepository.findOneByUuid(uuid).country == json.country
        where:
            uuid | json
            "a1" | [money: 200, country: "RUS"]
            "a2" | [money: 300, country: "GBR"]
    }

    @Unroll
    @Rollback
    def "Existed CustomerData can be fetched by customer uuid"() {
        when:
            customerDataService.updateCustomerData("b1", new Document([money: 100, country: "RUS"]))
            def persistedData = customerDataService.findOneCustomerDataByUuid(uuid)
        then:
            persistedData?.money == result?.money
            persistedData?.country == result?.country
        where:
            uuid | result
            "b1" | [money: 100, country: "RUS"]
            "b2" | null
    }

    @Rollback
    def "Can update a lot of data"() {
        when:
            1000.times {
                customerDataService.updateCustomerData("a_${it}", new Document([money: 1, country: "RUS"]))
            }
        then:
            noExceptionThrown()
    }

    @Rollback
    def "Can update a lot of data async"() {
        when:
            1000.times {
                producerTemplate.sendBodyAndHeader("direct:customer-data-update",
                        new Document([money: 1, country: "RUS"]), CommonConstants.UUID, "a_${it}");
            }
        then:
            noExceptionThrown()
    }

}
