package com.gratus.idp.service;

import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.util.constants.ServiceConstants;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SignUpService {
    @POST(ServiceConstants.SIGNUP__URL)
    Single<ApiSuccessResponse> signUp(@Body RegistrationRequest registrationRequest);
    @POST(ServiceConstants.EDIT_PROFILE__URL)
    Single<ApiSuccessResponse> editProfile(@Body RegistrationRequest registrationRequest);
    @POST(ServiceConstants.EDIT_PROFILE_URL_U)
    Single<ApiSuccessResponse> editProfileU(@Body RegistrationRequest registrationRequest);
}
