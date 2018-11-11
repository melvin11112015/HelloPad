package com.gki.v107.net;

import com.gki.v107.entity.ItemVsBomInfo;
import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.PadMessageAddon;
import com.gki.v107.entity.PadMessageInfo;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsInfo;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmDetailsInfo;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.entity.ProdSpecDetailsAddon;
import com.gki.v107.entity.ProdSpecDetailsInfo;
import com.gki.v107.entity.WebPordOrderCompInfo;
import com.gki.v107.entity.WebProdOrderInfo;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiService {

    String PARAM_JSON = "?$format=json";

    @GET("WebPordOrderComp" + PARAM_JSON)
    Call<GenericResult<WebPordOrderCompInfo>> getWebPordOrderCompList(@Query(value = "$filter", encoded = true) String filter);

    @POST("ProdConfirmBomItems" + PARAM_JSON)
    Call<Map<String, Object>> addProdConfirmBomItems(@Body ProdConfirmBomItemsAddon addon);

    @GET("ProdConfirmBomItems" + PARAM_JSON)
    Call<GenericResult<ProdConfirmBomItemsInfo>> getProdConfirmBomItemsList(@Query(value = "$filter", encoded = true) String filter);

    @GET("ProdConfirmItems" + PARAM_JSON)
    Call<GenericResult<ProdConfirmItemsInfo>> getProdConfirmItemsList();

    @POST("ProdConfirmDetails" + PARAM_JSON)
    Call<Map<String, Object>> addProdConfirmDetails(@Body ProdConfirmDetailsAddon addon);

    @GET("ProdConfirmDetails" + PARAM_JSON)
    Call<GenericResult<ProdConfirmDetailsInfo>> getProdConfirmDetailsList(@Query(value = "$filter", encoded = true) String filter);

    @GET("ItemVsSpecItem" + PARAM_JSON)
    Call<GenericResult<ItemVsSpecItemInfo>> getItemVsSpecItemList(@Query(value = "$filter", encoded = true) String filter);

    @POST("ProdSpecDetails" + PARAM_JSON)
    Call<Map<String, Object>> addProdSpecDetails(@Body ProdSpecDetailsAddon addon);

    @GET("ProdSpecDetails" + PARAM_JSON)
    Call<GenericResult<ProdSpecDetailsInfo>> getProdSpecDetailsList(@Query(value = "$filter", encoded = true) String filter);

    @GET("ItemVsBom" + PARAM_JSON)
    Call<GenericResult<ItemVsBomInfo>> getItemVsBomList(@Query(value = "$filter", encoded = true) String filter);

    @GET("WebProdOrder" + PARAM_JSON)
    Call<GenericResult<WebProdOrderInfo>> getWebProdOrderList(@Query(value = "$filter", encoded = true) String filter);

    @GET("PadMessage" + PARAM_JSON + "&$orderby=Create_DateTime desc")
    Call<GenericResult<PadMessageInfo>> getPadMessageList(@Query(value = "$filter", encoded = true) String filter);

    @POST("PadMessage" + PARAM_JSON)
    Call<Map<String, Object>> addPadMessage(@Body PadMessageAddon addon);

    @Headers("If-Match: *")
    @PATCH
    Call<String> updatePadMessage(@Url String url, @Body PadMessageAddon addon);

    @Headers("If-Match: *")
    @PATCH
    Call<String> updateProdConfirmBomItems(@Url String url,@Body ProdConfirmBomItemsAddon addon);

    @Headers("If-Match: *")
    @PATCH
    Call<String> updateProdConfirmDetails(@Url String url,@Body ProdConfirmDetailsAddon addon);

    @Headers("If-Match: *")
    @PATCH
    Call<String> updateProdSpecDetails(@Url String url,@Body ProdSpecDetailsAddon addon);

}
