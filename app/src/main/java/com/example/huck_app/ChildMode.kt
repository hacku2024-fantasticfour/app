package com.example.huck_app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.huck_app.ui.theme.Huck_appTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildMode : AppCompatActivity() {
    private var image by mutableStateOf<Bitmap?>(null)
    private lateinit var detector1: Detector
    private lateinit var detector2: Detector
    private val MODEL_PATH1 = "code_best_float32.tflite"
    private val MODEL_PATH2 = "corner_best_float32.tflite"
    private val CODE_LABELS_PATH = "code_labels.txt"
    private val CORNER_LABELS_PATH = "corner_labels.txt"
    private val processingScope = CoroutineScope(Dispatchers.IO)
    private var boundingBoxes: MutableList<BoundingBox> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child_mode)
        setupPermissions()
        setupView()
        setupDetectors()
    }

    private fun setupPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    private fun setupView() {
        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            Huck_appTheme {
                ChildImage(bitmap = image)
            }
        }
        composeView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                handleTouchOnImage(view, event)
            }
            true
        }
    }

    private fun setupDetectors() {
        val imageUri = intent.getStringExtra("image_uri") ?: return
        val inputStream = contentResolver.openInputStream(Uri.parse(imageUri))
        image = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        detector1 = Detector(baseContext, MODEL_PATH1, CODE_LABELS_PATH, this::handleDetection)
        detector2 = Detector(baseContext, MODEL_PATH2, CORNER_LABELS_PATH, this::handleDetection)
        detector1.setup()
        detector2.setup()

        image?.let { img ->
            detector1.detect(img)
            detector2.detect(img)
        }
    }

    private fun handleDetection(boundingBoxes: List<BoundingBox>) {
        this.boundingBoxes.addAll(boundingBoxes)
        updateImageWithBoxes()
    }

    private fun updateImageWithBoxes() {
        image?.let { bmp ->
            processingScope.launch {
                val updatedBitmap = drawBoundingBoxes(bmp, boundingBoxes)
                image = updatedBitmap
            }
        }
    }

    private fun drawBoundingBoxes(bitmap: Bitmap, boxes: List<BoundingBox>): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 20f
        }
        val textPaint = Paint().apply {
            color = Color.rgb(0, 255, 0)
            textSize = 80f
            typeface = Typeface.DEFAULT_BOLD
        }

        boxes.forEach { box ->
            paint.color = getColorForLabel(box.clsName)
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

    private fun getColorForLabel(label: String): Int {
        return when (label) {
            "コード" -> Color.RED
            "角" -> Color.RED
            else -> Color.GRAY // Ensure that there's an else condition to cover all possible inputs
        }
    }


    private fun handleTouchOnImage(view: View, event: MotionEvent) {
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

    private fun navigateToExplaintextActivity(label: String) {
        val intent = Intent(this, ExplaintextActivity::class.java).apply {
            putExtra("label", label)
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