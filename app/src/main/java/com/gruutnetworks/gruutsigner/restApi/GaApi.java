package com.gruutnetworks.gruutsigner.restApi;

import com.gruutnetworks.gruutsigner.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GaApi {
    private static Retrofit retrofit;
    private static GaService gaService;

    public static GaService getInstance() {
        if (gaService != null) {
            return gaService;
        }
        if (retrofit == null) {
            initRetrofit();
        }

        gaService = retrofit.create(GaService.class);
        return gaService;
    }

    private static void initRetrofit() {

        OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder().build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL + BuildConfig.API_VERSION)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OK_HTTP_CLIENT)
                .build();
    }
}
