package com.roshan.jha.attendance.retrofit

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServices {

    @POST("upload/simpleimage")
    fun uploadImage(@Body body: RequestBody): Call<JsonObject>

    @POST("attendance")
    fun markAttendance(@Header("Content-Type") contentType: String, @Body body: RequestBody): Call<JsonObject>
}