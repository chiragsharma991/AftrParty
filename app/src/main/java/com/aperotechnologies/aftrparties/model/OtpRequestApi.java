package com.aperotechnologies.aftrparties.model;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by mpatil on 26/05/16.
 */

//Interface for OTP request(Used for callback)
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
