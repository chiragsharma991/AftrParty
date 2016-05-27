package com.aperotechnologies.aftrparties.Login;

import com.aperotechnologies.aftrparties.model.OtpVerifiedResponse;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by mpatil on 26/05/16.
 */
public interface OtpVerificationApi
{


    @GET("/")
    public void getBooks(Callback<OtpVerifiedResponse> response);


}
