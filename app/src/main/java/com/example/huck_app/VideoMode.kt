package com.example.huck_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.webkit.WebView
import android.webkit.WebViewClient
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import android.widget.Toast
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
        val detectedLabels: List<String>,
        val error: String? = null
    )

    private lateinit var webView: WebView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fetchLabelsRunnable: Runnable

    private val CHANNEL_ID = "label_detection_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var textToSpeech: TextToSpeech
    private var isTtsReady: Boolean = false

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

        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                supportMultipleWindows()
            }
            webViewClient = MyWebViewClient()
        }
        setContentView(webView)
        webView.loadUrl("http://192.168.1.129:8080/camera.mjpg")

        createNotificationChannel()
        handlePermissions()

        textToSpeech = TextToSpeech(this, this)

        fetchLabelsRunnable = Runnable {
            fetchLabels()
            handler.postDelayed(fetchLabelsRunnable, 5000)  // 次の実行をスケジュール
        }
        handler.post(fetchLabelsRunnable)
    }

    private fun handlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchLabelsRunnable)
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.JAPANESE)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "サポートされていない言語です。", Toast.LENGTH_SHORT).show()
            } else {
                isTtsReady = true  // TTSが準備完了
            }
        } else {
            Toast.makeText(this, "TextToSpeechの初期化に失敗しました。", Toast.LENGTH_SHORT).show()
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "物体検知"
            val descriptionText = "ラベル検出の通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun fetchLabels() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.129:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val detectionApi = retrofit.create(DetectionApi::class.java)
        detectionApi.getLabels().enqueue(object : Callback<LabelResponse> {
            override fun onResponse(call: Call<LabelResponse>, response: Response<LabelResponse>) {
                if (response.isSuccessful) {
                    response.body()?.detectedLabels?.let {
                        showNotification("危険が検知されました： $it")
                        speak("危険が検知されました")
                    }
                } else {
                    showNotification("ラベル検出がありません。: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                val errorMessage = "リクエストの失敗: ${t.message}"
                showNotification(errorMessage)
                speak(errorMessage)
            }
        })
    }

    private fun showNotification(message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon) // 通知アイコンを設定
            .setContentTitle("Label Detection")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build())
    }

    private fun speak(message: String) {
        if (isTtsReady && ::textToSpeech.isInitialized) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Toast.makeText(this, "TextToSpeechがまだ準備ができていません。", Toast.LENGTH_SHORT).show()
        }
    }

}
