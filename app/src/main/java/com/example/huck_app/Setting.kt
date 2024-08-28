package com.example.huck_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Setting : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        applyTheme()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val themeGroup = findViewById<RadioGroup>(R.id.themeGroup)
        val mainLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)

        val selectedTheme = preferences.getString("selectedTheme", "YellowTheme")
        when (selectedTheme) {
            "YellowTheme" -> findViewById<RadioButton>(R.id.Yellow_theme).isChecked = true
            "BlueTheme" -> findViewById<RadioButton>(R.id.Blue_theme).isChecked = true
            "PurpleTheme" -> findViewById<RadioButton>(R.id.Purple_theme).isChecked = true
        }

        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = preferences.edit()
            when (checkedId) {
                R.id.Yellow_theme -> {
                    editor.putString("selectedTheme", "YellowTheme")
                    mainLayout.setBackgroundResource(R.drawable.background)
                }
                R.id.Blue_theme -> {
                    editor.putString("selectedTheme", "BlueTheme")
                    mainLayout.setBackgroundResource(R.drawable.background2)
                }
                R.id.Purple_theme -> {
                    editor.putString("selectedTheme", "PurpleTheme")
                    mainLayout.setBackgroundResource(R.drawable.background3)
                }
            }
            editor.apply()

            // トーストを表示し、MainActivityに移動する
            Toast.makeText(this, "再起動します", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 現在のアクティビティを終了
        }
    }

    private fun applyTheme() {
        val selectedTheme = preferences.getString("selectedTheme", "YellowTheme")
        when (selectedTheme) {
            "YellowTheme" -> setTheme(R.style.Yellow_Theme)
            "BlueTheme" -> setTheme(R.style.Blue_Theme)
            "PurpleTheme" -> setTheme(R.style.Purple_Theme)
        }
    }
}
