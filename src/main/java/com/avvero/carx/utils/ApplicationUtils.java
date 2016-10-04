package com.avvero.carx.utils;

import com.google.gson.Gson;

/**
 * @author Avvero
 */
public class ApplicationUtils {

    public static boolean isInteger(Object o) {
        try {
            Integer.valueOf(o.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String dataToJson(Object o) {
        return new Gson().toJson(o);
    }

}
