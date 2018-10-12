package com.gki.v107.net;

import com.google.gson.Gson;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class GenericOdataCallback<T> implements Callback<GenericResult<T>> {

    public abstract void onDataAvailable(List<T> datas);

    public abstract void onDataUnAvailable(String msg, int errorCode);

    @Override
    public void onResponse(Call<GenericResult<T>> call, Response<GenericResult<T>> response) {
        if (response.body() != null) {
            if (response.body().getValue() != null)
                onDataAvailable(response.body().getValue());
            else
                onDataUnAvailable("获取数据失败", -97);
        } else {
            String errorMsg = "";
            try {
                GenericError error = new Gson().fromJson(response.errorBody().string(), GenericError.class);
                errorMsg = error.getOdata_error().getMessage().getValue();
            } catch (Exception e) {
                e.printStackTrace();
                onDataUnAvailable("异常" + e.getMessage(), -96);
                return;
            }
            onDataUnAvailable(errorMsg, -95);
        }
    }

    @Override
    public void onFailure(Call<GenericResult<T>> call, Throwable t) {
        t.printStackTrace();
        onDataUnAvailable(t.getMessage(), -99);
    }
}
