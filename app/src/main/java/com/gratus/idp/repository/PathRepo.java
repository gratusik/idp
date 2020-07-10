package com.gratus.idp.repository;

import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.request.PathRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.model.response.PathResponse;
import com.gratus.idp.service.FilterService;
import com.gratus.idp.service.PathService;

import javax.inject.Inject;

import io.reactivex.Single;

public class PathRepo {
    private PathService pathService;

    @Inject
    public PathRepo(PathService pathService) {
        this.pathService = pathService;
    }

    public Single<PathResponse> getPathResponse(PathRequest pathRequest){
        return pathService.pathRequest(pathRequest);
    }
}
