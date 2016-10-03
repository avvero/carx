package com.avvero.carx.conf;

import com.avvero.carx.domain.Activity;
import com.avvero.carx.exception.NotFoundException;
import com.avvero.carx.service.CustomerActivityService;
import com.avvero.carx.service.CustomerDataService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Collections.singletonMap;
import static spark.Spark.*;

/**
 * @author Avvero
 */
@Slf4j
@Component
public class WebConfig {

    @Autowired
    CustomerDataService customerDataService;
    @Autowired
    CustomerActivityService customerActivityService;

    public WebConfig() {
        setupRoutes();
    }

    private void setupRoutes() {
        post("/customer/:uuid/data", (request, response) -> {
            String uuid = request.params(":uuid");

            //TODO check uuid
            //TODO check required field money
            //TODO check required field country

            Document doc = Document.parse(request.body());
            customerDataService.updateCustomerData(uuid, doc);
            return "";
        });

        get("/customer/:uuid/data", (request, response) -> {
            String uuid = request.params(":uuid");

            //TODO check uuid

            Document customerData = customerDataService.findOneCustomerDataByUuid(uuid);
            if (customerData == null) {
                throw new NotFoundException("Customer not found");
            } else {
                response.type("application/json");
                return customerData.toJson();
            }
        });

        post("/customer/:uuid/activity", (request, response) -> {
            String uuid = request.params(":uuid");

            //TODO check uuid
            //TODO check value of the activity

            Activity activity = new Gson().fromJson(request.body(), Activity.class);
            customerActivityService.save(uuid, activity);
            return "";
        });

        exception(Exception.class, (exception, request, response) -> {
            log.error(exception.getMessage(), exception);

            response.type("application/json");
            response.status(500);
            response.body(new Gson().toJson(singletonMap("message", "Unexpected exception")));
            return;
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            log.error(exception.getMessage(), exception);

            response.type("application/json");
            response.status(404);
            response.body(new Gson().toJson(singletonMap("message", exception.getMessage())));
            return;
        });
    }
}
