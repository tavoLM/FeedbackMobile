package com.example.feedback.Interface;

import com.example.feedback.Model.Area;
import com.example.feedback.Model.ColorCode;
import com.example.feedback.Model.Defect;
import com.example.feedback.Model.FeedbackData;
import com.example.feedback.Model.Location;
import com.example.feedback.Model.Model;
import com.example.feedback.Model.Part;
import com.example.feedback.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FeedbackServiceApi {

    @POST("Users/")
    Call<User> GetUser(@Body User user);

    @GET("Models")
    Call<List<Model>> GetModels();

    @GET("Areas")
    Call<List<Area>> GetAreas();

    @GET("ColorCodes")
    Call<List<ColorCode>> GetColors();

    @GET("Parts")
    Call<List<Part>> GetParts();

    @GET("Defects")
    Call<List<Defect>> GetDefects();

    @GET("Locations")
    Call<List<Location>> GetLocations();

    @POST("Feedback/")
    Call<FeedbackData> PostFeedback(@Body FeedbackData feedback);
}
