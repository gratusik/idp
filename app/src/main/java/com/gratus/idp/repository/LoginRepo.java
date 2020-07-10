package com.gratus.idp.repository;

import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.service.LoginService;

import javax.inject.Inject;

import io.reactivex.Single;

public class LoginRepo {
    private LoginService loginService;

    @Inject
    public LoginRepo(LoginService loginService) {
        this.loginService = loginService;
    }



    public Single<LoginResponse> getLoginResponse(LoginRequest loginRequest){
        return loginService.login(loginRequest);
    }
}
