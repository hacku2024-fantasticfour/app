package com.example.huck_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

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
import java.util.Locale

class VideoMode : AppCompatActivity(), TextToSpeech.OnInitListener {

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
    private val channelId = "label_detection_channel"
    private lateinit var tts: TextToSpeech

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
            showNotification("WebView Error", message)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_mode)

        createNotificationChannel()

        // TextToSpeechの初期化
        tts = TextToSpeech(this, this)

        val webView = WebView(this)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.supportMultipleWindows()
        webView.webViewClient = MyWebViewClient()
        setContentView(webView)

        webView.loadUrl("http://192.168.75.131:8080/camera.mjpg")

        fetchLabelsRunnable = object : Runnable {
            override fun run() {
                fetchLabels()
                handler.postDelayed(this, 5000)
            }
        }
        handler.post(fetchLabelsRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchLabelsRunnable)
        // TextToSpeechのリソース解放
        tts.stop()
        tts.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.JAPANESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                showNotification("TTS Error", "この言語はサポートされていません。")
            }
        } else {
            showNotification("TTS Error", "Text-to-Speechの初期化に失敗しました。")
        }
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
                        // "person" がリストに含まれているかチェック
                        if (it.contains("person")) {
                            val message = "危険が検出されました"
                            showNotification("人を検出", message)
                            speakOut(message)  // 読み上げを追加
                        }
                    }
                } else {
                    val message = "Failed to fetch labels: ${response.message()}"
                    showNotification("Fetch Failed", message)
                    speakOut(message)  // 読み上げを追加
                }
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                val message = "リクエストが失敗しました: ${t.message}"
                showNotification("リクエストの失敗", message)
                speakOut(message)  // 読み上げを追加
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "危険の検知"
            val descriptionText = "危険検出の通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app's icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    private fun speakOut(message: String) {
        if (::tts.isInitialized) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}