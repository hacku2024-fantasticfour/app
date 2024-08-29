package com.example.huck_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Background(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams), TextToSpeech.OnInitListener {

    private lateinit var interpreter: Interpreter
    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false

    override fun doWork(): Result {
        // テキスト読み上げの初期化
        tts = TextToSpeech(applicationContext, this)

        // モデルのロード
        try {
            interpreter = Interpreter(loadModelFile("yolov8s_float32.tflite"))
        } catch (e: Exception) {
            Log.e("Background", "Error loading model file", e)
            return Result.failure()
        }

        // 映像ストリームの監視
        return if (monitorStream()) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val assetFileDescriptor = applicationContext.assets.openFd(modelFileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun monitorStream(): Boolean {
        return try {
            val url = URL("http://192.168.1.129:8080/camera.mjpg")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 10000  // 10秒の接続タイムアウト
            connection.readTimeout = 10000     // 10秒の読み込みタイムアウト
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)

            if (bitmap == null) {
                Log.e("BottleDetectionWorker", "Failed to decode bitmap from input stream")
                false
            } else {
                // YOLOによる物体検出
                val detectedObjects = runObjectDetection(bitmap)

                // 「bottle」が検出されたか確認
                if (detectedObjects.contains("bottle")) {
                    captureAndNotify(bitmap)
                }
                true
            }

        } catch (e: Exception) {
            Log.e("BottleDetectionWorker", "Error monitoring stream", e)
            false
        }
    }

    private fun runObjectDetection(bitmap: Bitmap): List<String> {
        // モデルの実行と検出ロジック
        return listOf("bottle") // 仮に「bottle」を検出したとする
    }

    private fun captureAndNotify(bitmap: Bitmap) {
        // 通知の設定
        createNotificationChannel()
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "bottle_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("ボトルが検出されました！")
            .setContentText("ボトルが検出されました。ログを確認してください")
            .build()
        notificationManager.notify(1, notification)

        // テキスト読み上げ
        if (isTtsInitialized) {
            tts.speak("危険が検出されました", TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("BottleDetectionWorker", "TextToSpeech is not initialized yet.")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ボトルの検出"
            val descriptionText = "Channel for bottle detection notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("bottle_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            isTtsInitialized = true
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }
}
