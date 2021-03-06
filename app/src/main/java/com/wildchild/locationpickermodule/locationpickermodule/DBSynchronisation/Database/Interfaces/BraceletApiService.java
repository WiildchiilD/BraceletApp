package com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Database.Interfaces;

import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Database.Annotations.Retry;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.Bracelet;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.History;
import com.wildchild.locationpickermodule.locationpickermodule.DBSynchronisation.Models.SResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface BraceletApiService {
    @GET("/user/find/bracelet/{id}")
    Call<List<Bracelet>> getBracelets(@Path("id") String id);

    @GET("/bracelet/{id}")
    Call<Bracelet> getBracelet(@Path("id") String id);

    @GET("/history/find/{id}")
    Call<List<History>> getBraceletHistory(@Path("id") String id);

    @GET("/bracelet/affect/{braceletid}/to/{userid}")
    Call<SResponse> pairDeviceWith(@Path("braceletid") String braceletId, @Path("userid") String userId);

}
