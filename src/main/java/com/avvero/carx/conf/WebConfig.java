package com.avvero.carx.conf;

import com.avvero.carx.dao.jpa.CustomerRepository;
import com.avvero.carx.dao.mongo.CustomerDataRepository;
import com.avvero.carx.domain.Customer;
import com.avvero.carx.domain.CustomerData;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import static spark.Spark.post;

/**
 * @author Avvero
 */
@Component
public class WebConfig {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerDataRepository customerDataRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public WebConfig() {
        setupRoutes();
    }

    private void setupRoutes() {
        post("/hello", (req, res) -> {
            String guid = "123127";
            Customer customer = customerRepository.findOneByGuid(guid);
            if (customer == null) {
                customer = customerRepository.save(new Customer(null, guid));
            }
            CustomerData customerData = customerDataRepository.findOne(guid);
            if (customerData == null){
                Document doc = Document.parse(req.body());
                doc.put("_id", guid); //TODO required to use something like @Id
                mongoTemplate.insert(doc, "customerData");
            } else {
                Document doc = Document.parse(req.body());
                doc.put("_id", guid); //TODO required to use something like @Id
                mongoTemplate.save(doc, "customerData");
            }
            customerData = customerDataRepository.findAllByMoneyLessThan(10);
            return "Hello, " + customerData;
        });
    }
}
