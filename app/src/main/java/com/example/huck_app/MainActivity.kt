package com.example.huck_app

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.huck_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var set: Animator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OneTimeWorkRequestを作成してBackgroundクラスのWorkerをスケジュール
        val backgroundWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<Background>().build()

        // WorkManagerを使ってWorkRequestをスケジュール
        WorkManager.getInstance(this).enqueue(backgroundWorkRequest)

        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }

        // アニメーションの読み込みとターゲットの設定
        set = AnimatorInflater.loadAnimator(this, R.animator.blink_animation).apply {
            setTarget(binding.TapText)
        }

        // タップイベントの設定
        binding.root.setOnClickListener {
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // アニメーションの開始
        set.start()
    }
}
