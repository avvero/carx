package com.avvero.carx.service

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.dao.jpa.ActivityRepository
import com.avvero.carx.dao.jpa.CustomerRepository
import com.avvero.carx.domain.Activity
import com.avvero.carx.domain.Customer
import com.avvero.carx.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

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
            def customer = customerRepository.save(new Customer(uuid: uuid))
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

}
