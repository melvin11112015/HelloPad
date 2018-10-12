package com.gki.v107.net;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class BaseOdataCallback<T> implements Callback<T> {

    public abstract void onDataAvailable(T datas);

    public abstract void onDataUnAvailable(String msg, int errorCode);

    protected void processDatas(Response<T> response) {
        onDataAvailable(response.body());
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {

        if (response.body() != null) {
            processDatas(response);

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
    public void onFailure(Call<T> call, Throwable t) {
        t.printStackTrace();
        onDataUnAvailable(t.getMessage(), -99);
    }
}
