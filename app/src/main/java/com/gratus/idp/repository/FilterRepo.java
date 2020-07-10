package com.gratus.idp.repository;

import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.service.FilterService;

import javax.inject.Inject;

import io.reactivex.Single;

public class FilterRepo {
    private FilterService filterService;

    @Inject
    public FilterRepo(FilterService filterService) {
        this.filterService = filterService;
    }

    public Single<FilterResponse> getLoginResponse(FilterRequest filterRequest){
        return filterService.filterRequest(filterRequest);
    }
}
