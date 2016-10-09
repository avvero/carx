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
            uuid   | value
            "act1" | 100
            "act2" | 200

    }

    @Unroll
    def "NotFoundException will be thrown if customer does not exist"(){
        when:
            customerRepository.save(new Customer(uuid: customerUuuid))
            customerActivityService.save(activeUuid, new Activity(value: 1))
        then:
            thrown NotFoundException
        where:
            customerUuuid | activeUuid
            "act3"        | "act4"
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
            "act6"   | 1 | [0]
            "act7"   | 5 | [0, 100, 200, 300, 400]

    }

    def "Can update a lot of data"() {
        when:
            customerRepository.save(new Customer(uuid: "act8"))
            1000.times {
                customerActivityService.save("act8", new Activity(value: 100 * it))
            }
        then:
            noExceptionThrown()
    }

    def "Can update a lot of data async"() {
        when:
            customerRepository.save(new Customer(uuid: "act9"))
            1000.times {
                producerTemplate.sendBodyAndHeader("direct:activity", new Activity(value: 100 * it),
                        CommonConstants.UUID, "act9");
            }
        then:
            noExceptionThrown()
    }

}
