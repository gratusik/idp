package com.gratus.idp.repository;

import com.gratus.idp.service.DownloadService;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.ResponseBody;

public class DownloadRepo {
    private DownloadService downloadService;

    @Inject
    public DownloadRepo(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public Single<ResponseBody> download(){
        return downloadService.download();
    }
}
