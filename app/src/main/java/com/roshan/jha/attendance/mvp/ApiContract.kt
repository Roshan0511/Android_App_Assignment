package com.roshan.jha.attendance.mvp

import org.json.JSONObject
import java.io.File

interface ApiContract {
    interface View {
        fun onJsonAPIResponse(jsonObject: JSONObject?, request_code: Int, status_code: Int)
        fun onAPIFailure(jsonObject: Any?, error_msg: String?)
    }

    interface Presenter {
        fun uploadImage(file: File)
        fun markAttendance(params: JSONObject)
    }
}