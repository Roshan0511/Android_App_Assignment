package com.roshan.jha.attendance.mvp

import android.util.Log
import com.google.gson.JsonObject
import com.roshan.jha.attendance.retrofit.ApiServices
import com.roshan.jha.attendance.retrofit.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ApiPresenter(val view: ApiContract.View) : ApiContract.Presenter {


    /*
    * Api calling for upload a profile picture
    * */
    override fun uploadImage(file: File) {
        val services = RetrofitClient.getInstance()!!.getRetrofitInstance()!!.create(ApiServices::class.java)

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "content",
                file.name,
                RequestBody.create("image/*".toMediaTypeOrNull(), file)
            )
            .build()

        services.uploadImage(requestBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val serverResponse = response.body()
                    try {
                        val jsonObject = JSONObject(serverResponse.toString())
                        view.onJsonAPIResponse(jsonObject, 1, response.code())
                    } catch (e: JSONException) {
                        Log.e("error: ", "callSessionAPI: " + e.localizedMessage)
                    }
                } else {
                    view.onAPIFailure(response.errorBody(), "${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                view.onAPIFailure(null, t.localizedMessage)
            }

        })
    }








    /*
    * Api calling for mark the attendance
    * */
    override fun markAttendance(params: JSONObject) {
        val services = RetrofitClient.getInstance()!!.getRetrofitInstance()!!.create(ApiServices::class.java)

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(),
            params.toString()
        )

        services.markAttendance("application/json", requestBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val serverResponse = response.body()
                    try {
                        val jsonObject = JSONObject(serverResponse.toString())
                        view.onJsonAPIResponse(jsonObject, 2, response.code())
                    } catch (e: JSONException) {
                        Log.e("error: ", "callSessionAPI: " + e.localizedMessage)
                    }
                } else {
                    view.onAPIFailure(response.errorBody(), "${response.code()}")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                view.onAPIFailure(null, t.localizedMessage)
            }
        })
    }
}