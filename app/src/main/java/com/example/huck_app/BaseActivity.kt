package com.example.huck_app

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

open class BaseActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // ThemePrefsを初期化し、選択されたテーマを適用
        preferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        applyTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        // ナビゲーションコントローラーとボトムナビゲーションビューを設定
        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        setupWithNavController(bottomNavigationView, navController)

        // テーマに基づいてアイコンを設定
        setBottomNavigationIcons(bottomNavigationView)
    }

    private fun applyTheme() {
        val selectedTheme = preferences.getString("selectedTheme", "YellowTheme")
        when (selectedTheme) {
            "YellowTheme" -> {
                setTheme(R.style.Yellow_Theme)
                setStatusBarAndNavBarColor(R.color.yellow_primary_dark, R.color.yellow_primary)
            }
            "BlueTheme" -> {
                setTheme(R.style.Blue_Theme)
                setStatusBarAndNavBarColor(R.color.blue_primary_dark, R.color.blue_primary)
            }
            "PurpleTheme" -> {
                setTheme(R.style.Purple_Theme)
                setStatusBarAndNavBarColor(R.color.purple_primary_dark, R.color.purple_primary)
            }
        }
    }

    private fun setStatusBarAndNavBarColor(statusBarColorRes: Int, navBarColorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, statusBarColorRes)
            window.navigationBarColor = ContextCompat.getColor(this, navBarColorRes)
        }
    }

    private fun setBottomNavigationIcons(bottomNavigationView: BottomNavigationView) {
        val selectedTheme = preferences.getString("selectedTheme", "YellowTheme")
        when (selectedTheme) {
            "YellowTheme" -> {
                bottomNavigationView.menu.findItem(R.id.navi_home).icon = ContextCompat.getDrawable(this, R.drawable.yellow_home)
                bottomNavigationView.menu.findItem(R.id.navi_log).icon = ContextCompat.getDrawable(this, R.drawable.yellow_alart)
                bottomNavigationView.menu.findItem(R.id.navi_video).icon = ContextCompat.getDrawable(this, R.drawable.yellow_video)
            }
            "BlueTheme" -> {
                bottomNavigationView.menu.findItem(R.id.navi_home).icon = ContextCompat.getDrawable(this, R.drawable.blue_home)
                bottomNavigationView.menu.findItem(R.id.navi_log).icon = ContextCompat.getDrawable(this, R.drawable.blue_alart)
                bottomNavigationView.menu.findItem(R.id.navi_video).icon = ContextCompat.getDrawable(this, R.drawable.blue_video)
            }
            "PurpleTheme" -> {
                bottomNavigationView.menu.findItem(R.id.navi_home).icon = ContextCompat.getDrawable(this, R.drawable.purple_home)
                bottomNavigationView.menu.findItem(R.id.navi_log).icon = ContextCompat.getDrawable(this, R.drawable.purple_alart)
                bottomNavigationView.menu.findItem(R.id.navi_video).icon = ContextCompat.getDrawable(this, R.drawable.purple_video)
            }
        }
    }
}
