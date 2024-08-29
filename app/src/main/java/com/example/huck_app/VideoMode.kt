package com.example.huck_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.annotation.SuppressLint
import android.view.KeyEvent
import android.view.Window
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class VideoMode : AppCompatActivity() {

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
    }
}