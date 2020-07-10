package com.gratus.idp.service;

import com.gratus.idp.model.request.ProfileRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.ProfileResponse;
import com.gratus.idp.util.constants.ServiceConstants;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ProfileService {
    @POST(ServiceConstants.PROFILE__URL)
    Single<ProfileResponse> profile(@Header("Authorization") String token, @Body ProfileRequest profileRequest);
}
