package com.gratus.idp.repository;

import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.service.ResetPasswordService;
import com.gratus.idp.service.SignUpService;

import javax.inject.Inject;

import io.reactivex.Single;

public class SignUpRepo {
    private SignUpService signUpService;

    @Inject
    public SignUpRepo(SignUpService signUpService) {
        this.signUpService = signUpService;
    }

    public Single<ApiSuccessResponse> getSignUpResponse(RegistrationRequest registrationRequest){
        return signUpService.signUp(registrationRequest);
    }

    public Single<ApiSuccessResponse> getEditProfile(RegistrationRequest registrationRequest){
        return signUpService.editProfile(registrationRequest);
    }

    public Single<ApiSuccessResponse> getEditProfileU(RegistrationRequest registrationRequest){
        return signUpService.editProfileU(registrationRequest);
    }

}
