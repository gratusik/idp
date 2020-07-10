package com.gratus.idp.repository;

import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.service.LoginService;
import com.gratus.idp.service.ResetPasswordService;

import javax.inject.Inject;

import io.reactivex.Single;

public class ResetPasswordRepo {
    private ResetPasswordService resetPasswordService;

    @Inject
    public ResetPasswordRepo(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    public Single<ApiSuccessResponse> getResetPasswordResponse(ResetPasswordRequest resetPasswordRequest){
        return resetPasswordService.reset(resetPasswordRequest);
    }
}
