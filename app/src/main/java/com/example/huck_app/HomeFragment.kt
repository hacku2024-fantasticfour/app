package com.example.huck_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // SharedPreferencesからテーマを取得して適用
        preferences = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        applyTheme()

        // フラグメントのレイアウトをインフレート
        val view = inflater.inflate(R.layout.activity_home_fragment, container, false)

        // 子モードボタンの設定
        val childButton = view.findViewById<Button>(R.id.ditector_button)
        childButton?.setOnClickListener {
            val intent = Intent(activity, Detector_Page::class.java)
            intent.putExtra("BUTTON_ID", R.id.ditector_button)
            startActivity(intent)
        }

        // 設定ボタンの設定
        val settingButton = view.findViewById<Button>(R.id.setting_button)
        settingButton?.setOnClickListener {
            val intent = Intent(activity, Setting::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun applyTheme() {
        // 現在選択されているテーマを取得
        val selectedTheme = preferences.getString("selectedTheme", "YellowTheme")
        when (selectedTheme) {
            "YellowTheme" -> requireActivity().setTheme(R.style.Yellow_Theme)
            "BlueTheme" -> requireActivity().setTheme(R.style.Blue_Theme)
            "PurpleTheme" -> requireActivity().setTheme(R.style.Purple_Theme)
        }
    }
}
