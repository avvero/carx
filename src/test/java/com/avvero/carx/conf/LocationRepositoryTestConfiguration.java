package com.avvero.carx.conf;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author Avvero
 */
@Configuration
@Profile("test")
public class LocationRepositoryTestConfiguration extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "carx";
    }

    @Bean
    public Mongo mongo() {
        // Configure a Fongo instance
        return new Fongo("mongo-test").getMongo();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), "carx");
    }

}