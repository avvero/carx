package com.avvero.carx.web

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.dao.jpa.ActivityRepository
import com.avvero.carx.dao.jpa.CustomerRepository
import com.avvero.carx.dao.mongo.CustomerDataRepository
import com.avvero.carx.domain.Activity
import com.avvero.carx.service.CustomerActivityService
import com.avvero.carx.service.CustomerDataService
import com.google.gson.Gson
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import spark.utils.IOUtils
import spock.lang.Specification

import static com.avvero.carx.utils.ApplicationUtils.dataToJson
import static org.junit.Assert.fail

/**
 * @author Avvero
 */
@ContextConfiguration(classes = [App, LocationRepositoryTestConfiguration, TestConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
class WebConfigTests extends Specification {

    @Autowired
    CustomerDataRepository customerDataRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    ActivityRepository activityRepository;

    public static class TestConfiguration {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }

    def cleanup() {
        customerRepository.deleteAll()
        customerDataRepository.deleteAll()
    }

    def "POST '/customer/#uuid/data' will return http code = 200"() {
        when:
            def response = restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$uuid/data"), [money: 100, country: "RUS"], Object.class)
        then:
            response.statusCode.value() == 200
        where:
            uuid = 1
    }

    def "GET '/customer/#uuid/data' will return http code = 200 and customer data"() {
        setup:
            restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$uuid/data"), customerData, Object.class)
            sleep(1000) //save works asynchronously
        when:
            def response = restTemplate.getForEntity(URI.create("http://localhost:4567/customer/$uuid/data"), Object.class)
        then:
            response.statusCode.value() == 200
            response.body.money == customerData.money
            response.body.country == customerData.country
        where:
            uuid = 2
            customerData = [money: 100, country: "RUS"]
    }

    def "GET '/customer/#uuid/data' will return http code = 404 if there is now customer with such uuid"() {
        when:
            def response = restTemplate.getForEntity(URI.create("http://localhost:4567/customer/$uuid/data"), Object.class)
        then:
            def exception = thrown(HttpClientErrorException.class)
            exception.statusCode.value() == 404
        where:
            uuid = 3
            customerData = [money: 100, country: "RUS"]
    }

    def "POST '/customer/:uuid/activity' will return http code = 200"() {
        setup:
            restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$uuid/data"), [money: 100, country: "RUS"], Object.class)
            sleep(1000) //save works asynchronously
        when:
            def response = restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$uuid/activity"), activity, Object.class)
            sleep(1000) //save works asynchronously
        then:
            response.statusCode.value() == 200
            activityRepository.findAll().value == [activity.value]
        where:
            uuid = 3
            activity = new Activity(value: 1)
    }

    def "POST '/customer/:uuid/activity' will return http code = 200 if there is now customer with such uuid"() {
        setup:
            restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$existedCustomerUuuid/data"), [money: 100, country: "RUS"], Object.class)
            sleep(1000) //save works asynchronously
        when:
            def response = restTemplate.postForEntity(URI.create("http://localhost:4567/customer/$sentCustomerUuuid/activity"), activity, Object.class)
            sleep(1000) //save works asynchronously
        then:
            response.statusCode.value() == 200
        where:
            existedCustomerUuuid = 4
            sentCustomerUuuid = 5
            activity = new Activity(value: 1)
    }

}
