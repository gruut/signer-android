package com.gruutnetworks.gruutuser.restApi;

import com.gruutnetworks.gruutuser.model.SignUpResponse;
import com.gruutnetworks.gruutuser.model.SignUpSourceData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GaService {

    /**
     * pID를 GA에 송신하여 가입요청
     * @param json  pID(현재는 핸드폰 번호 전송)
     */
    @Headers("Content-type: application/json")
    @POST("users")
    Call<SignUpResponse> signUp(@Body SignUpSourceData json);
}
