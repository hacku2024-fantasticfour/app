package com.example.huck_app.com.example.huck_app

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val RTSP_REQUEST_KEY = "rtsp_request"
private const val RTSP_USERNAME_KEY = "rtsp_username"
private const val RTSP_PASSWORD_KEY = "rtsp_password"

// 事前に設定したいRTSPリクエスト、ユーザー名、パスワード
private const val DEFAULT_RTSP_REQUEST = "rtsp://10.0.1.3:554/axis-media/media.amp"
private const val DEFAULT_RTSP_USERNAME = "your_default_username"
private const val DEFAULT_RTSP_PASSWORD = "your_default_password"

private const val LIVE_PARAMS_FILENAME = "live_params"

@SuppressLint("LogNotTimber")
class LiveViewModel : ViewModel() {

    companion object {
        private val TAG: String = LiveViewModel::class.java.simpleName
        private const val DEBUG = false
    }

    val rtspRequest = MutableLiveData<String>().apply {
        value = DEFAULT_RTSP_REQUEST
    }
    val rtspUsername = MutableLiveData<String>().apply {
        value = DEFAULT_RTSP_USERNAME
    }
    val rtspPassword = MutableLiveData<String>().apply {
        value = DEFAULT_RTSP_PASSWORD
    }

    fun loadParams(context: Context) {
        if (DEBUG) Log.v(TAG, "loadParams()")
        // ここでは何もせず、デフォルト値がすでにセットされていることを仮定する
        rtspRequest.value = DEFAULT_RTSP_REQUEST
        rtspUsername.value = DEFAULT_RTSP_USERNAME
        rtspPassword.value = DEFAULT_RTSP_PASSWORD
    }

    fun saveParams(context: Context) {
        if (DEBUG) Log.v(TAG, "saveParams()")
        val editor = context.getSharedPreferences(LIVE_PARAMS_FILENAME, Context.MODE_PRIVATE).edit()
        editor.putString(RTSP_REQUEST_KEY, rtspRequest.value)
        editor.putString(RTSP_USERNAME_KEY, rtspUsername.value)
        editor.putString(RTSP_PASSWORD_KEY, rtspPassword.value)
        editor.apply()
    }
}
