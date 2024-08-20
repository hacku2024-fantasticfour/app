package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 子供モードボタンを押したら ChildMode に遷移
        val childButton = findViewById<ImageButton>(R.id.Child_button)
        childButton.setOnClickListener {
            val intent = Intent(this, CameraMode::class.java).apply {
                putExtra("BUTTON_ID", R.id.Child_button)
            }
            startActivity(intent)
        }

        // 高齢者モードボタンを押したら ElderlyMode に遷移
        val elderlyButton = findViewById<ImageButton>(R.id.Elderly_button)
        elderlyButton.setOnClickListener {
            val intent = Intent(this, CameraMode::class.java).apply {
                putExtra("BUTTON_ID", R.id.Elderly_button)
            }
            startActivity(intent)
        }

        // 地震防災モードボタンを押したら EarthquakeMode に遷移
        val earthButton = findViewById<ImageButton>(R.id.Earth_button)
        earthButton.setOnClickListener {
            val intent = Intent(this, CameraMode::class.java).apply {
                putExtra("BUTTON_ID", R.id.Earth_button)
            }
            startActivity(intent)
        }
    }
}
