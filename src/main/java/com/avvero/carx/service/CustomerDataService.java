package com.avvero.carx.service;

import com.avvero.carx.constants.CommonConstants;
import com.avvero.carx.dao.jpa.CustomerRepository;
import com.avvero.carx.dao.mongo.CustomerDataRepository;
import com.avvero.carx.domain.Customer;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Avvero
 */
@Service
public class CustomerDataService {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerDataRepository customerDataRepository;

    @Transactional
    public void updateCustomerData(@Header(CommonConstants.UUID) String uuid, @Body Document doc) {
        Customer customer = customerRepository.findOneByUuid(uuid);
        if (customer == null) {
            customerRepository.save(new Customer(null, uuid));
            customerDataRepository.save(uuid, doc);
        } else {
            customerDataRepository.update(uuid, doc);
        }
    }

    public Document findOneCustomerDataByUuid(String uuid) {
        return customerDataRepository.findOneByUuid(uuid);
    }
}
