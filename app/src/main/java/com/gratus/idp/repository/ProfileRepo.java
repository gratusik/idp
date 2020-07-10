package com.gratus.idp.repository;

import com.gratus.idp.model.request.ProfileRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.ProfileResponse;
import com.gratus.idp.service.ProfileService;
import com.gratus.idp.service.ResetPasswordService;

import javax.inject.Inject;

import io.reactivex.Single;

public class ProfileRepo {
    private ProfileService profileService;

    @Inject
    public ProfileRepo(ProfileService profileService) {
        this.profileService = profileService;
    }

    public Single<ProfileResponse> getProfile(ProfileRequest profileRequest,String token){
        return profileService.profile(token, profileRequest);
    }
}
