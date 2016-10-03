package com.avvero.carx.dao.mongo;

import com.avvero.carx.domain.CustomerData;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Avvero
 */
public interface CustomerDataRepository extends MongoRepository<CustomerData, String> {
    CustomerData findAllByMoneyLessThan(long i);
}
