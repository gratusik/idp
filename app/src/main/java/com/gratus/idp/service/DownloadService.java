package com.gratus.idp.service;

import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.util.constants.ServiceConstants;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DownloadService {
    @GET(ServiceConstants.DOWNLOAD_URL)
    Single<ResponseBody> download();
}
