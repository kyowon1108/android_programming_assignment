package com.example.babydiary.data.api;

import com.example.babydiary.data.dto.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TagApi {

    @GET("tags")
    Call<List<Tag>> getTags();
}