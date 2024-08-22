package com.example.huck_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import com.example.huck_app.ui.theme.Huck_appTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildMode : AppCompatActivity(), Detector.DetectorListener {
    private var image by mutableStateOf<Bitmap?>(null)
    private lateinit var detector: Detector
    private val MODEL_PATH = "yolov8s_float32.tflite"
    private val LABELS_PATH = "labels.txt"
    private val processingScope = CoroutineScope(Dispatchers.IO)
    private var boundingBoxes: List<BoundingBox> = emptyList() // 検出されたバウンディングボックスを保持するリスト

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_child_mode)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            Huck_appTheme {
                ChildImage(bitmap = image)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageUri = intent.getStringExtra("image_uri")
        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
            image = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            detector = Detector(baseContext, MODEL_PATH, LABELS_PATH, this)
            detector.setup()
            image?.let {
                detector.detect(it)
            }
        }

        // タッチイベントの設定
        composeView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x * (image?.width ?: 1) / view.width
                val y = event.y * (image?.height ?: 1) / view.height
                boundingBoxes.firstOrNull { box ->
                    val rect = RectF(
                        box.x1 * (image?.width ?: 1),
                        box.y1 * (image?.height ?: 1),
                        box.x2 * (image?.width ?: 1),
                        box.y2 * (image?.height ?: 1)
                    )
                    rect.contains(x, y)
                }?.let { box ->
                    Log.i("BoundingBoxTapped", "Tapped on: ${box.clsName}")
                    navigateToExplaintextActivity(box.clsName)
                }
            }
            true
        }

    }

    override fun onDetect(boundingBoxes: List<BoundingBox>) {
        this.boundingBoxes = boundingBoxes // 検出されたバウンディングボックスを保存
        image?.let { bmp ->
            processingScope.launch {
                val updatedBitmap = drawBoundingBoxes(bmp, boundingBoxes)
                image = updatedBitmap
            }
        }
    }

    override fun onEmptyDetect() {
        Log.i("empty", "empty")
    }

    fun drawBoundingBoxes(bitmap: Bitmap, boxes: List<BoundingBox>): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            color = Color.MAGENTA
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        val textPaint = Paint().apply {
            color = Color.rgb(0, 255, 0)
            textSize = 80f
            typeface = Typeface.DEFAULT_BOLD
        }

        for (box in boxes) {
            val rect = RectF(
                box.x1 * mutableBitmap.width,
                box.y1 * mutableBitmap.height,
                box.x2 * mutableBitmap.width,
                box.y2 * mutableBitmap.height
            )
            canvas.drawRect(rect, paint)
            canvas.drawText(box.clsName, rect.left, rect.bottom, textPaint)
        }

        return mutableBitmap
    }

    // ExplaintextActivity へ遷移するメソッド
    private fun navigateToExplaintextActivity(label: String) {
        val intent = Intent(this, ExplaintextActivity::class.java).apply {
            putExtra("label", label)  // ラベルをIntentに追加
        }
        startActivity(intent)
    }
}

@Composable
fun ChildImage(bitmap: Bitmap?) {
    bitmap?.let {
        Image(bitmap = it.asImageBitmap(), contentDescription = "Description of the image")
    }
}
