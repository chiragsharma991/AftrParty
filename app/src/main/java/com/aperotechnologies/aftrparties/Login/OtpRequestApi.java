package com.aperotechnologies.aftrparties.Login;

import com.aperotechnologies.aftrparties.model.ValidOTPResponse;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by mpatil on 26/05/16.
 */
public interface OtpRequestApi
{
    @FormUrlEncoded
    @POST("/sms")
    void getOTP(
            @Field("appKey") String appkey,
            @Field("customerId") String customerId,
            @Field("isoCountryCode") String in,
            @Field("msisdn") String msisdn,
            @Field("Content-Type") String ctype,
            Callback<ValidOTPResponse> callback);


}
