package com.avvero.carx.web;

import com.avvero.carx.domain.Activity;
import com.avvero.carx.exception.NotFoundException;
import com.avvero.carx.service.CustomerDataService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.avvero.carx.constants.CommonConstants.UUID;
import static com.avvero.carx.utils.ApplicationUtils.dataToJson;
import static com.avvero.carx.utils.ApplicationUtils.isInteger;
import static org.eclipse.jetty.http.HttpStatus.*;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;
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
    ProducerTemplate producerTemplate;

    public WebConfig() {
        setupRoutes();
    }

    private void setupRoutes() {

        post("/customer/:uuid/data", (request, response) -> {
            String uuid = request.params(":uuid");

            //TODO check request size
            //TODO logging to file

            log.info("--->" + request.body());

            Document doc = Document.parse(request.body());
            //Validation for important data
            notNull(doc.get("money"), "Field 'money' is required");
            isTrue(isInteger(doc.get("money")), "Field 'money' is incorrect");
            notNull(doc.get("country"), "Field 'country' is required");

            producerTemplate.sendBodyAndHeader("direct:customer-data-update", doc, UUID, uuid);
            return "";
        });

        //Fetch
        get("/customer/:uuid/data", (request, response) -> {
            String uuid = request.params(":uuid");
            Document customerData = (Document) producerTemplate.requestBody("direct:customer-data-fetch", uuid);
            if (customerData == null) {
                throw new NotFoundException("Customer not found");
            } else {
                response.type("application/json");
                return customerData.toJson();
            }
        });

        post("/customer/:uuid/activity", (request, response) -> {
            String uuid = request.params(":uuid");

            Activity activity = new Gson().fromJson(request.body(), Activity.class);
            producerTemplate.sendBodyAndHeader("direct:activity", activity, UUID, uuid);
            return "";
        });

        /**
         * Exception handling
         */
        exception(Exception.class, (exception, request, response) -> {
            log.error(exception.getMessage(), exception);

            response.type("application/json");
            response.status(INTERNAL_SERVER_ERROR_500);
            response.body(dataToJson(new ResponseError("Unexpected exception")));
            return;
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            log.error(exception.getMessage(), exception);

            response.type("application/json");
            response.status(NOT_FOUND_404);
            response.body(dataToJson(new ResponseError(exception)));
            return;
        });

        exception(IllegalArgumentException.class, (exception, request, response) -> {
            log.error(exception.getMessage(), exception);

            response.type("application/json");
            response.status(BAD_REQUEST_400);
            response.body(dataToJson(new ResponseError(exception)));
            return;
        });
    }
}
