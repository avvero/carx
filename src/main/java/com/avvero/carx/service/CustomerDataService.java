package com.avvero.carx.service;

import com.avvero.carx.constants.CommonConstants;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.bson.Document;

/**
 * @author Avvero
 */
public interface CustomerDataService {

    void updateCustomerData(@Header(CommonConstants.UUID) String uuid, @Body Document doc);
    Document findOneCustomerDataByUuid(String uuid);

}
