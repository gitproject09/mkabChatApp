package org.mkab.chatapp.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IFireBaseAPI {

    @GET("/users.json")
    Call<String> getAllUsersAsJsonString();

    @GET("/doctors.json")
    Call<String> getAllDoctorsAsJsonString();

    @GET
    Call<String> getUserFriendsListAsJsonString(@Url String url);

    @GET
    Call<String> getSingleUserByEmail(@Url String url);

}
