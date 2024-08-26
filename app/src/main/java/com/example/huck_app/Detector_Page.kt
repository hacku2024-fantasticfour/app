package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton

class Detector_Page : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detectorpage)

        // ボタンを探し、クリックリスナーを設定します
        val childButton = findViewById<ImageButton>(R.id.image_button)
        childButton?.setOnClickListener {
            val intent = Intent(this, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.image_button)
            startActivity(intent)
        }
    }
}
