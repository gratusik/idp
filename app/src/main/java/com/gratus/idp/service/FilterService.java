package com.gratus.idp.service;

import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.util.constants.ServiceConstants;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FilterService {
    @POST(ServiceConstants.FILTER_URL)
    Single<FilterResponse> filterRequest(@Body FilterRequest filterRequest);
}
