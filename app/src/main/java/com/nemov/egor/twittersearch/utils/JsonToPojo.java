package com.nemov.egor.twittersearch.utils;

import com.google.gson.Gson;

/**
 * Created by egor.nemov on 03.02.16.
 */
public class JsonToPojo<T> {

    public T convert(String rawData, Class<T> classOfT) {
        T objData = null;
        if (rawData != null && rawData.length() > 0) {
            try {
                Gson gson = new Gson();
                objData = gson.fromJson(rawData, classOfT);
            } catch (IllegalStateException ex) { /* Fail fast */ }
        }
        return objData;
    }
}
