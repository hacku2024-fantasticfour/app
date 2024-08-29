package com.example.huck_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

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
            val message = "error: ($errorCode) $description"
            Toast.makeText(this@VideoMode, message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_mode)

        val webView = WebView(this)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.supportMultipleWindows()
        webView.webViewClient = MyWebViewClient()
        setContentView(webView)

        webView.loadUrl("http://192.168.1.129:8080/camera.mjpg")

        fetchLabels()
    }

    private fun fetchLabels() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.129:8080/labels")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val detectionApi = retrofit.create(DetectionApi::class.java)

        val call = detectionApi.getLabels()
        call.enqueue(object : Callback<LabelResponse> {
            override fun onResponse(call: Call<LabelResponse>, response: Response<LabelResponse>) {
                if (response.isSuccessful) {
                    val labels = response.body()?.detected_labels
                    println(1)
                    labels?.let {
                        Toast.makeText(this@VideoMode, "Detected labels: $it", Toast.LENGTH_LONG).show()
                    }
                } else {
                    println(2)
                    Toast.makeText(this@VideoMode, "Failed to fetch labels: ${response.message()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                println(3)
                Toast.makeText(this@VideoMode, "Request failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
