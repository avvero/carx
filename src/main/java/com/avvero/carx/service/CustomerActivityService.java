package com.avvero.carx.service;

import com.avvero.carx.dao.jpa.ActivityRepository;
import com.avvero.carx.dao.jpa.CustomerRepository;
import com.avvero.carx.domain.Activity;
import com.avvero.carx.domain.Customer;
import com.avvero.carx.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Avvero
 */
@Service
public class CustomerActivityService {

    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    CustomerRepository customerRepository;

    public void save(String uuid, Activity activity) {
        Customer customer = customerRepository.findOneByUuid(uuid); //TODO additional request is bad to do
        if (customer == null) {
            throw new NotFoundException("Customer not found");
        }
        activity.setCustomerId(customer.getId());
        activityRepository.save(activity);
    }
}
