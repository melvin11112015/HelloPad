package com.gki.v107.net;

import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.ProdConfirmBomItemsInfo;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmDetailsInfo;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.entity.ProdSpecDetailsAddon;
import com.gki.v107.entity.ProdSpecDetailsInfo;
import com.gki.v107.entity.WebPordOrderCompInfo;
import com.netcosports.ntlm.NTLMAuthenticator;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiTool {

    public static final String DEFAULT_API_URL = "http://10.1.1.65:7048/DynamicsNAV71/OData/Company('GKI-Live')/";
    public static String currentApiUrl = DEFAULT_API_URL;

    public static String currentAuthName = "nav";
    public static String currentAuthPsw = "Nav-123";

    private ApiTool() {
    }

    private static String getResonse(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();//同步

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response + "\n body" + response.body().string());
        String responseJson = response.body().string();
        System.out.println(responseJson);
        return responseJson;
    }

    public static void getResonseCallback(String url,okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request request = new Request.Builder()
                .url(url)
                //.header("Authorization", DEFAULT_AUTHORIZATION)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(currentApiUrl)
                .client(new OkHttpClient.Builder()
                        //NTLM认证方式
                        .authenticator(new NTLMAuthenticator(currentAuthName,currentAuthPsw,null))
                        .addInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                Request request = original.newBuilder()
                                        //.addHeader("Authorization", DEFAULT_AUTHORIZATION)
                                        .method(original.method(), original.body())
                                        .build();
                                return chain.proceed(request);
                            }
                        })
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static void callWebPordOrderComp(String filter, Callback<GenericResult<WebPordOrderCompInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getWebPordOrderCompList(filter)
                .enqueue(callback);
    }

    public static void addProdConfirmBomItems(ProdConfirmBomItemsAddon addon, Callback<Map<String, Object>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .addProdConfirmBomItems(addon)
                .enqueue(callback);
    }

    public static void callProdConfirmItemsList( Callback<GenericResult<ProdConfirmItemsInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getProdConfirmItemsList()
                .enqueue(callback);
    }

    public static void addProdConfirmDetails(ProdConfirmDetailsAddon addon, Callback<Map<String, Object>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .addProdConfirmDetails(addon)
                .enqueue(callback);
    }

    public static void callItemVsSpecItemList(String filter, Callback<GenericResult<ItemVsSpecItemInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getItemVsSpecItemList(filter)
                .enqueue(callback);
    }

    public static void addProdSpecDetails(ProdSpecDetailsAddon addon, Callback<Map<String, Object>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .addProdSpecDetails(addon)
                .enqueue(callback);
    }

    public static void callProdConfirmBomItemsList(String filter, Callback<GenericResult<ProdConfirmBomItemsInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getProdConfirmBomItemsList(filter)
                .enqueue(callback);
    }

    public static void callProdConfirmDetailsList(String filter, Callback<GenericResult<ProdConfirmDetailsInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getProdConfirmDetailsList(filter)
                .enqueue(callback);
    }

    public static void callProdSpecDetailsList(String filter, Callback<GenericResult<ProdSpecDetailsInfo>> callback) {
        getRetrofit()
                .create(ApiService.class)
                .getProdSpecDetailsList(filter)
                .enqueue(callback);
    }

    public static void updateProdConfirmBomItemsList(String itemDesc,ProdConfirmBomItemsAddon addon, Callback<String> callback) {
        String realUrl = currentApiUrl + "ProdConfirmBomItems(" + itemDesc + ")";
        getRetrofit()
                .create(ApiService.class)
                .updateProdConfirmBomItems(realUrl,addon)
                .enqueue(callback);
    }

    public static void updateProdConfirmDetailsList(String itemDesc,ProdConfirmDetailsAddon addon, Callback<String> callback) {
        String realUrl = currentApiUrl + "ProdConfirmDetails(" + itemDesc + ")";
        getRetrofit()
                .create(ApiService.class)
                .updateProdConfirmDetails(realUrl,addon)
                .enqueue(callback);
    }

    public static void updateProdSpecDetailsList(String itemDesc,ProdSpecDetailsAddon addon, Callback<String> callback) {
        String realUrl = currentApiUrl + "ProdSpecDetails(" + itemDesc + ")";
        getRetrofit()
                .create(ApiService.class)
                .updateProdSpecDetails(realUrl,addon)
                .enqueue(callback);
    }

}
