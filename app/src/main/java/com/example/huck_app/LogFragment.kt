package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment

class LogFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // activity_log_fragment.xml レイアウトをインフレート
        val view = inflater.inflate(R.layout.activity_log_fragment, container, false)

        // log_start ボタンの取得
        val logStartButton: AppCompatImageButton = view.findViewById(R.id.log_start)

        // log_start ボタンのクリックリスナーを設定
        logStartButton.setOnClickListener {
            // LogMode アクティビティに移行
            val intent = Intent(activity, LogMode::class.java)
            startActivity(intent)
        }

        return view
    }
}
