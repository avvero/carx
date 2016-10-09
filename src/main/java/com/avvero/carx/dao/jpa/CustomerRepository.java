package com.avvero.carx.dao.jpa;

import com.avvero.carx.domain.Customer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Avvero
 */
public interface CustomerRepository extends CrudRepository<Customer, Integer> {

    @Cacheable(value = "customer", unless = "#result == null")
    Customer findOneByUuid(String uuid);
}
