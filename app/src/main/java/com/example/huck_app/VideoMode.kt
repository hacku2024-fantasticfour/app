package com.example.huck_app

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors

class VideoMode : AppCompatActivity(), Detector.DetectorListener {

    private lateinit var webView: WebView
    private lateinit var detector: Detector
    private val mainHandler = Handler(Looper.getMainLooper())
    private val frameCaptureExecutor = Executors.newSingleThreadExecutor()

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
            return false
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest?,
            error: WebResourceError
        ) {
            val message = "Error: ${error.description}"
            Toast.makeText(this@VideoMode, message, Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_mode)

        // WebViewの設定
        webView = WebView(this)
        webView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setContentView(webView)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.supportMultipleWindows()
        webView.webViewClient = MyWebViewClient()
        webView.loadUrl("http://192.168.1.129:8080/camera.mjpg")

        // Detectorのセットアップ
        detector = Detector(
            context = this,
            modelPath = "yolov8s_float32.tflite", // モデルファイルのパス
            labelPath = "labels.txt",             // ラベルファイルのパス
            detectorListener = this
        )
        detector.setup()

        // フレームキャプチャを開始
        startFrameProcessing()
    }

    private fun startFrameProcessing() {
        frameCaptureExecutor.execute {
            while (true) {
                mainHandler.post {
                    captureFrameFromWebView()
                }
                Thread.sleep(500) // フレームキャプチャの間隔（調整可能）
            }
        }
    }

    private fun captureFrameFromWebView() {
        val bitmap = Bitmap.createBitmap(webView.width, webView.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        webView.draw(canvas)

        // ビットマップをDetectorで処理
        detector.detect(bitmap)
    }

    override fun onDetect(boundingBoxes: List<BoundingBox>) {
        // 検出結果をUIに表示する
        runOnUiThread {
            Toast.makeText(this, "Detected ${boundingBoxes.size} objects", Toast.LENGTH_SHORT).show()
            // 必要に応じてUIにバウンディングボックスを描画
        }
    }

    override fun onEmptyDetect() {
        runOnUiThread {
            Toast.makeText(this, "No objects detected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        frameCaptureExecutor.shutdown()
    }
}
