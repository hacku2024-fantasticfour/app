package com.example.huck_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class VideoMode : AppCompatActivity() {

    interface DetectionApi {
        @GET("/labels")
        fun getLabels(): Call<LabelResponse>
    }

    data class LabelResponse(
        val detected_labels: List<String>,
        val error: String? = null
    )

    private lateinit var webView: WebView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fetchLabelsRunnable: Runnable

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return false
        }

        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {
            // WebViewのエラーを処理（必要に応じてログに出力）
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_mode)

        // WebViewの設定
        webView = WebView(this)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.supportMultipleWindows()
        webView.webViewClient = MyWebViewClient()
        setContentView(webView)

        // WebViewでビデオストリームをロード
        webView.loadUrl("http://192.168.75.131:8080/camera.mjpg")

        // ラベルの取得を定期的に実行
        fetchLabelsRunnable = object : Runnable {
            override fun run() {
                fetchLabels()
                handler.postDelayed(this, 5000) // 5秒ごとに実行
            }
        }
        handler.post(fetchLabelsRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchLabelsRunnable)
    }

    private fun fetchLabels() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.75.131:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val detectionApi = retrofit.create(DetectionApi::class.java)

        val call = detectionApi.getLabels()
        call.enqueue(object : Callback<LabelResponse> {
            override fun onResponse(call: Call<LabelResponse>, response: Response<LabelResponse>) {
                if (response.isSuccessful) {
                    val labels = response.body()?.detected_labels
                    labels?.let {
                        // ラベル処理（必要に応じて）
                        if (it.contains("person")) {
                            // "person" が検出された場合の処理（ここでは何もしない）
                        }
                    }
                } else {
                    // ラベルの取得に失敗した場合の処理（必要に応じて）
                }
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                // リクエストが失敗した場合の処理（必要に応じて）
            }
        })
    }
}
