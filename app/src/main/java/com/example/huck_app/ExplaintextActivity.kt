package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
        val ExplainTextView = findViewById<TextView>(R.id.explain_text)
        ExplainTextView.text = getExplainText(label)

        // ラベルに応じたアドバイスをadvice_textに表示
        val adviceTextView = findViewById<TextView>(R.id.advice_text)
        adviceTextView.text = getAdviceText(label)

        // ラベルに応じた画像を表示
        val imageView = findViewById<ImageView>(R.id.denger_image)
        setImageResourceForLabel(label, imageView)

        // ラベルに応じた画像を表示
        val imagView = findViewById<ImageView>(R.id.advice_image)
        setImageResourceForLabel2(label, imagView)
    }
    // ラベルに応じたアドバイスを返す関数
    private fun getExplainText(label: String?): String {
        return when (label) {
            "コード" -> "コンセントや延長コードを口にくわえると、口や唇をやけどする可能性があり非常に危険です。\n" +
                    "濡れた手でコンセントやプラグに触れると、電気が体に通りやすくなり感電のリスクが高まります。\n" +
                    "プラグが完全に差し込まれていなかったり、隙間に金属製の物を入れたりすると、感電する危険性があります。"
            "角" -> "活発に動き回る子供が思わぬ場所で転んだりぶつかったりすることがあります。\n" +
                    "これにより頭部や顔、眼球に重度の傷を負う可能性があります。"
            else -> "特定のアドバイスはありません。とても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
        }
    }

    // ラベルに応じたアドバイスを返す関数
    private fun getAdviceText(label: String?): String {
        return when (label) {
            "コード" -> "使用していないコンセントには、いたずら防止用のコンセントキャップを取り付けましょう。\n" +
                    "コンセント全体を覆い、より安全なコンセントカバーの使用も検討しましょう。\n" +
                    "子供の興味を引かないシンプルな形状や色のものを選びましょう。"
            "角" -> "家具の角に柔らかい素材のクッションを取り付けることで、衝撃を吸収し、ケガを防止します。\n" +
                    "木製の家具は、プラスチック製の家具に比べて、衝撃を吸収しやすいため、安全です。"

            else -> "特定のアドバイスはありません。"
        }
    }

    // ラベルに応じた画像リソースを設定する関数
    private fun setImageResourceForLabel(label: String?, imageView: ImageView) {
        val imageResId = when (label) {
            "角" -> R.drawable.kado
            else -> R.drawable.kado     // デフォルトの画像リソースID
        }

        imageView.setImageResource(imageResId)
    }

    private fun setImageResourceForLabel2(label: String?, imageView: ImageView) {
        val imageResId = when (label) {
            "角" -> R.drawable.kado2 // ボトルの画像リソースID
            else -> R.drawable.kado     // デフォルトの画像リソースID
        }

        imageView.setImageResource(imageResId)
    }
}