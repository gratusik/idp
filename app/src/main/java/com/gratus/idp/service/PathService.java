package com.gratus.idp.service;

import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.request.PathRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.model.response.PathResponse;
import com.gratus.idp.util.constants.ServiceConstants;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PathService {
    @POST(ServiceConstants.PATH_REPORT_URL)
    Single<PathResponse> pathRequest(@Body PathRequest pathRequest);
}
