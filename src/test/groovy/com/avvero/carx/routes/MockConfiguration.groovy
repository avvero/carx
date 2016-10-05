package com.avvero.carx.routes

import com.avvero.carx.service.CustomerDataService
import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * @author Avvero
 */
@Configuration
class MockConfiguration {
    @Bean
    @Profile("test")
    CustomerDataService customerDataService() {
        return Mockito.mock(CustomerDataService.class)
    };
}