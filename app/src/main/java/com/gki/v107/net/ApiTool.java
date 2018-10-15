package com.gki.v107.net;

import com.gki.v107.entity.ItemVsSpecItemInfo;
import com.gki.v107.entity.ProdConfirmDetailsAddon;
import com.gki.v107.entity.ProdConfirmBomItemsAddon;
import com.gki.v107.entity.ProdConfirmItemsInfo;
import com.gki.v107.entity.ProdSpecDetailsAddon;
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

    public static final String DEFAULT_API_URL = "http://192.168.0.122:9048/GKI_2013R2/OData/Company('GKI_2013R2')/";
    public static String currentApiUrl = DEFAULT_API_URL;

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
                        .authenticator(new NTLMAuthenticator("tim","kybmgnc",null))
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

}
