package com.gki.managerment.util;

import com.google.gson.Gson;


public class GsonUtils {

    public static <T> T fromJson(String json, Class<T> clz) {
        Gson gson = new Gson();
        return (T) gson.fromJson(json, clz);
    }
}
