package com.avvero.carx.utils;

import com.google.gson.Gson;

/**
 * @author Avvero
 */
public class ApplicationUtils {

    public static String dataToJson(Object o) {
        return new Gson().toJson(o);
    }

}
