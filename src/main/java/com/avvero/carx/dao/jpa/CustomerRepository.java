package com.avvero.carx.dao.jpa;

import com.avvero.carx.domain.Customer;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Avvero
 */
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Customer findOneByUuid(String uuid);
}
