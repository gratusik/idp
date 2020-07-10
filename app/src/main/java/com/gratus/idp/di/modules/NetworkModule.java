package com.gratus.idp.di.modules;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gratus.idp.service.DownloadService;
import com.gratus.idp.service.FilterService;
import com.gratus.idp.service.LoginService;
import com.gratus.idp.service.PathService;
import com.gratus.idp.service.ProfileService;
import com.gratus.idp.service.ResetPasswordService;
import com.gratus.idp.service.SignUpService;
import com.gratus.idp.util.constants.ServiceConstants;
import com.gratus.idp.util.networkManager.NetworkHelper;
import com.gratus.idp.util.networkManager.NetworkOnlineCheck;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.util.pref.PreferencesHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.content.ContextCompat.getSystemService;

@Module(includes = ViewModelModule.class)
public abstract class NetworkModule {

    @Provides
    @Singleton
    static Gson provideGson(){
        GsonBuilder builder =
                new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return builder.setLenient().create();
    }


    @Provides
    @Singleton
    static Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {

        return new Retrofit.Builder()
                .baseUrl(ServiceConstants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    @Singleton
    static OkHttpClient getRequestHeader() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .build();
            return chain.proceed(request);
        })
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS);

        return httpClient.build();
    }

    @Provides
    @Singleton
    static LoginService provideLoginService(Retrofit retrofit) {
        return retrofit.create(LoginService.class);
    }

    @Provides
    @Singleton
    static ResetPasswordService provideResetPasswordService(Retrofit retrofit) {
        return retrofit.create(ResetPasswordService.class);
    }

    @Provides
    @Singleton
    static SignUpService provideSignUpService(Retrofit retrofit) {
        return retrofit.create(SignUpService.class);
    }

    @Provides
    @Singleton
    static ProfileService provideProfileService(Retrofit retrofit) {
        return retrofit.create(ProfileService.class);
    }

    @Provides
    @Singleton
    static FilterService provideFilterService(Retrofit retrofit) {
        return retrofit.create(FilterService.class);
    }

    @Provides
    @Singleton
    static DownloadService provideDownloadService(Retrofit retrofit) {
        return retrofit.create(DownloadService.class);
    }

    @Provides
    @Singleton
    static PathService providePathService(Retrofit retrofit) {
        return retrofit.create(PathService.class);
    }
}
