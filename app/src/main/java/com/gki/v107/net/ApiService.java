package com.gki.v107.net;

import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.WebPordOrderCompInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {

    String PARAM_JSON = "?$format=json";

    @GET("WebPordOrderComp" + PARAM_JSON)
    Call<GenericResult<WebPordOrderCompInfo>> getWebPordOrderCompList(@Query(value = "$filter", encoded = true) String filter);


    @POST("ProdConfirmBomItems" + PARAM_JSON)
    Call<Map<String, Object>> addProdConfirmBomItems(@Body ProdConfirmBomItemsAddon addon);


}
