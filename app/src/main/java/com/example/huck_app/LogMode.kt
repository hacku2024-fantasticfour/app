package com.example.huck_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogMode : AppCompatActivity(), TextToSpeech.OnInitListener {

    interface DetectionApi {
        @GET("/labels")
        fun getLabels(): Call<LabelResponse>
    }

    data class LabelResponse(
        val detected_labels: List<String>,
        val error: String? = null
    )

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var fetchLabelsRunnable: Runnable
    private val channelId = "label_detection_channel"
    private lateinit var tts: TextToSpeech
    private val imageLogList = mutableListOf<Bitmap>()
    private val timeLogList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_mode)

        createNotificationChannel()

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        // Set up the label fetching routine
        fetchLabelsRunnable = object : Runnable {
            override fun run() {
                fetchLabels()
            }
        }
        handler.post(fetchLabelsRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(fetchLabelsRunnable)
        // Release TextToSpeech resources
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
                        if (it.contains("microrobot")) {
                            val message = "危険が検出されました"
                            showNotification("小物を検出", message)
                            speakOut(message)
                        }
                    }
                } else {
                    val message = "Failed to fetch labels: ${response.message()}"
                    showNotification("Fetch Failed", message)
                    speakOut(message)
                }

                // カメラ画像の取得
                fetchCameraImage()
                handler.postDelayed(fetchLabelsRunnable, 5000)
            }

            override fun onFailure(call: Call<LabelResponse>, t: Throwable) {
                val message = "リクエストが失敗しました: ${t.message}"
                showNotification("リクエストの失敗", message)
                speakOut(message)
                handler.postDelayed(fetchLabelsRunnable, 5000)
            }
        })
    }

    private fun fetchCameraImage() {
        val imageUrl = "http://192.168.75.131:8080/camera.mjpg"
        Thread {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val input: InputStream = connection.inputStream
                val bufferedInput = BufferedInputStream(input)

                while (true) {
                    try {
                        // バウンダリを探す
                        var line: String? = readLine(bufferedInput)
                        while (line != null && !line.contains("--jpgboundary")) {
                            line = readLine(bufferedInput)
                        }

                        // ヘッダーを読み飛ばす
                        line = readLine(bufferedInput)
                        while (line != null && line.trim().isNotEmpty()) {
                            line = readLine(bufferedInput)
                        }

                        // JPEG画像データを読み取る
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        var prevByte = -1
                        var currByte = bufferedInput.read()
                        while (currByte != -1) {
                            byteArrayOutputStream.write(currByte)
                            if (prevByte == 0xFF && currByte == 0xD9) { // JPEG End of Image
                                break
                            }
                            prevByte = currByte
                            currByte = bufferedInput.read()
                        }

                        val imageBytes = byteArrayOutputStream.toByteArray()

                        // デバッグのため、デコード前にデータの長さをログに出力
                        Log.d("LogMode", "JPEG data size: ${imageBytes.size}")

                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                        if (bitmap != null) {
                            runOnUiThread {
                                updateImageLog(bitmap)
                            }
                        } else {
                            Log.e("LogMode", "Failed to decode bitmap")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("LogMode", "Exception while processing image stream: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("LogMode", "Error fetching image: ${e.message}")
            }
        }.start()
    }

    // `readLine` ヘルパーメソッドを追加
    private fun readLine(input: InputStream): String? {
        val lineBuffer = StringBuilder()
        while (true) {
            val nextChar = input.read()
            if (nextChar == -1 || nextChar == '\n'.code) {
                break
            }
            lineBuffer.append(nextChar.toChar())
        }
        return if (lineBuffer.isNotEmpty()) lineBuffer.toString() else null
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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
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

    private fun updateImageLog(bitmap: Bitmap) {
        if (imageLogList.size >= 10) {
            imageLogList.removeAt(0) // 最も古い画像を削除
            timeLogList.removeAt(0)  // 最も古い時間を削除
        }
        imageLogList.add(bitmap)

        // 現在の時間を取得してリストに追加
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        timeLogList.add(currentTime)

        // ImageViewとTextViewに画像と時間を設定
        for (i in imageLogList.indices) {
            val imageViewId = resources.getIdentifier("log_image${i + 1}", "id", packageName)
            val textViewId = resources.getIdentifier("log_text${i + 1}", "id", packageName)
            findViewById<ImageView>(imageViewId)?.setImageBitmap(imageLogList[i])
            findViewById<TextView>(textViewId)?.text = timeLogList[i]
        }
    }
}
