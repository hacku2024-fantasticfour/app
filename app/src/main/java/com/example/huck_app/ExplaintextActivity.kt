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
            "bottle" -> "ボトルはガラスやプラスチックでできた容器であり、液体を保存するのに広く使用されます。とても危険です。割れた場合には鋭利な破片ができるため、注意が必要です。"
            "cup" -> "カップはとても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "laptop" -> "ラップトップはとても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "clock" -> "古い時計はとても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "person" -> "人です。とても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "cell phone" -> "スマホです。とても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "tv" -> "テレビです。とても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
            "corner" -> "安全対策として、コーナークッションの使用をおすすめします。 家具の角に柔らかいクッションを取り付けることで、衝撃を和らげ、怪我を防ぐことができます。\n" +
                    "\nまた、可能であれば、角の少ないデザインの家具を選ぶことも一つの方法です。"
            else -> "特定のアドバイスはありません。とても危険です。ああああああああああああああああああああああああああああああああああああああああああああああああああああああああああああ"
        }
    }

    // ラベルに応じたアドバイスを返す関数
    private fun getAdviceText(label: String?): String {
        return when (label) {
            "bottle" -> " ボトルの歴史は古代エジプト時代にまでさかのぼります。当初はガラス製で、香水や薬の保存に使用されていました。18世紀になると、工業化が進み、ガラス吹き技術の向上により、より大規模な生産が可能になりました。20世紀にはプラスチックの登場により、軽量で割れにくいボトルが広く普及しました。"
            "cup" -> "カップは洗浄して再利用できます。使い捨てを避けましょう。"
            "laptop" -> "ラップトップは電子機器としてリサイクルすることが推奨されます。"
            "clock" -> "古い時計はリサイクル可能ですが、部品の取り扱いに注意が必要です。"
            "people" -> "人です。"
            "cell phone" -> "スマホです。"
            "tv" -> "テレビです。"
            "person" -> "人です。優しくしましょう"
            "corner" -> "小さい子供は運動能力や注意力が未熟で、家具の角にぶつかりやすいため危険です。また、視線の高さが低く、頭部が重いため、頭や顔に怪我をしやすいです。そのため角にクッションを付けるなどの安全対策が重要です。"
            else -> "特定のアドバイスはありません。"
        }
    }

    // ラベルに応じた画像リソースを設定する関数
    private fun setImageResourceForLabel(label: String?, imageView: ImageView) {
        val imageResId = when (label) {
            "bottle" -> R.drawable.kado // ボトルの画像リソースID
            else -> R.drawable.kado     // デフォルトの画像リソースID
        }

        imageView.setImageResource(imageResId)
    }

    private fun setImageResourceForLabel2(label: String?, imageView: ImageView) {
        val imageResId = when (label) {
            "bottle" -> R.drawable.kado // ボトルの画像リソースID
            else -> R.drawable.kado     // デフォルトの画像リソースID
        }

        imageView.setImageResource(imageResId)
    }
}
