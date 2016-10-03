package com.avvero.carx.conf;

import com.avvero.carx.dao.CustomerRepository;
import com.avvero.carx.domain.Customer;
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

    public WebConfig() {
        setupRoutes();
    }

    private void setupRoutes() {
        get("/hello", (req, res) -> {
            String guid = req.params("guid");
            Customer customer = customerRepository.findOneByGuid(guid);
            if (customer == null) {
                customer = customerRepository.save(new Customer(null, guid));
            }
            return "Hello, " + customer.getGuid();
        });
    }
}
