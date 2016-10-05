package com.avvero.carx.service;

import com.avvero.carx.constants.CommonConstants;
import com.avvero.carx.domain.Activity;
import org.apache.camel.Body;
import org.apache.camel.Header;

/**
 * @author Avvero
 */
public interface CustomerActivityService {

    void save(@Header(CommonConstants.UUID) String uuid, @Body Activity activity);

}
