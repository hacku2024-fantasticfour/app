package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView

class ExplaintextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_explaintext)

        // 戻るボタンの設定
        val childButton = findViewById<Button>(R.id.return_home)
        childButton.setOnClickListener {
            // CameraModeに移行
            val intent = Intent(this, BaseActivity::class.java)
            startActivity(intent)
        }

        // WindowInsetsの設定
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Intent からラベルを取得
        val label = intent.getStringExtra("label")

        // 取得したラベルをTextViewに表示
        val textView = findViewById<TextView>(R.id.label_text_view)
        textView.text = label ?: "ラベルが見つかりません"

        // ラベルに応じたアドバイスをadvice_textに表示
        val adviceTextView = findViewById<TextView>(R.id.advice_text)
        adviceTextView.text = getAdviceText(label)
    }

    // ラベルに応じたアドバイスを返す関数
    private fun getAdviceText(label: String?): String {
        return when (label) {
            "bottle" -> "ボトルはリサイクルできる場合があります。適切な廃棄方法を確認しましょう。"
            "cup" -> "カップは洗浄して再利用できます。使い捨てを避けましょう。"
            "laptop" -> "ラップトップは電子機器としてリサイクルすることが推奨されます。"
            "clock" -> "古い時計はリサイクル可能ですが、部品の取り扱いに注意が必要です。"
            else -> "特定のアドバイスはありません。"
        }
    }
}
