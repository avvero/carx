package com.avvero.carx.conf;

import com.avvero.carx.dao.jpa.CustomerRepository;
import com.avvero.carx.dao.mongo.CustomerDataRepository;
import com.avvero.carx.domain.Customer;
import com.avvero.carx.domain.CustomerData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static spark.Spark.get;

/**
 * @author Avvero
 */
@Component
public class WebConfig {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerDataRepository customerDataRepository;

    public WebConfig() {
        setupRoutes();
    }

    private void setupRoutes() {
        get("/hello", (req, res) -> {
            String guid = "123123";
            Customer customer = customerRepository.findOneByGuid(guid);
            if (customer == null) {
                customer = customerRepository.save(new Customer(null, guid));
            }
            CustomerData customerData = customerDataRepository.findOne(guid);
            if (customerData == null) {
                customerData = customerDataRepository.save(new CustomerData(guid, 0L, ""));
            }
            return "Hello, " + customer.getGuid();
        });
    }
}
