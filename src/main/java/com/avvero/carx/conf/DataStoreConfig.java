package com.avvero.carx.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Avvero
 */
@Configuration
@EnableJpaRepositories("com.avvero.carx.dao.jpa")
@EnableMongoRepositories("com.avvero.carx.dao.mongo")
public class DataStoreConfig {
}
