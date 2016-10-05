package com.avvero.carx.service

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.constants.CommonConstants
import com.avvero.carx.dao.jpa.ActivityRepository
import com.avvero.carx.dao.jpa.CustomerRepository
import com.avvero.carx.domain.Activity
import com.avvero.carx.domain.Customer
import com.avvero.carx.exception.NotFoundException
import org.apache.camel.ProducerTemplate
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static com.avvero.carx.constants.CommonConstants.UUID

/**
 * Tests for CustomerActivityService
 * @author Avvero
 */
@ContextConfiguration(classes = [App, LocationRepositoryTestConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
class CustomerActivityServiceTests extends Specification {

    @Autowired
    CustomerActivityService customerActivityService
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProducerTemplate producerTemplate;

    def cleanup() {
        customerRepository.deleteAll()
        activityRepository.deleteAll()
    }

    @Unroll
    def "Customer activity can be saved for the existed customer"(){
        when:
            def customer = customerRepository.save(new Customer(uuid: uuid))
            customerActivityService.save(uuid, new Activity(value: value))

            def activities = activityRepository.findAll()
        then:
            activities.value == [value]
            activities.customerId == [customer.id]
        where:
            uuid     | value
            "aaaaaa" | 100
            "bbbbbb" | 200

    }

    @Unroll
    def "NotFoundException will be thrown if customer does not exist"(){
        when:
            customerRepository.save(new Customer(uuid: customerUuuid))
            customerActivityService.save(putativeUuid, new Activity(value: 1))
        then:
            thrown NotFoundException
        where:
            customerUuuid | putativeUuid
            "aaaaaa"      | "bbbbbb"
    }

    @Unroll
    def "Unlimited amount activities can be saved for the existed customer"(){
        when:
            customerRepository.save(new Customer(uuid: uuid))
            n.times {
                customerActivityService.save(uuid, new Activity(value: 100 * it))
            }

            def activities = activityRepository.findAll()
        then:
            activities.value == values
        where:
            uuid     | n | values
            "aaaaaa" | 1 | [0]
            "aaaaaa" | 5 | [0, 100, 200, 300, 400]

    }

    def "Can update a lot of data"() {
        when:
            customerRepository.save(new Customer(uuid: "aaaaaa"))
            1000.times {
                customerActivityService.save("aaaaaa", new Activity(value: 100 * it))
            }
        then:
            noExceptionThrown()
    }

    def "Can update a lot of data async"() {
        when:
            customerRepository.save(new Customer(uuid: "aaaaaa"))
            1000.times {
                producerTemplate.sendBodyAndHeader("direct:activity", new Activity(value: 100 * it),
                        CommonConstants.UUID, "aaaaaa");
            }
        then:
            noExceptionThrown()
    }

}
