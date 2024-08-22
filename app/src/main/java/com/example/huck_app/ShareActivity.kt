package com.example.huck_app

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ShareActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var shareButton: Button
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        imageView = findViewById(R.id.imageView)
        shareButton = findViewById(R.id.shareButton)

        // Intentからバイト配列を取得
        val byteArray = intent.getByteArrayExtra("detected_image")
        if (byteArray != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                imageView.setImageBitmap(bitmap)

                // Bitmapをファイルに保存
                withContext(Dispatchers.IO) {
                    val file = File(cacheDir, "shared_image.png")
                    val fos = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()
                    imageUri = Uri.fromFile(file)
                }
            }
        } else {
            // 例としてローカルの画像を使用
            imageUri = Uri.parse("android.resource://${packageName}/drawable/sample_image")
            imageView.setImageURI(imageUri)
        }

        shareButton.setOnClickListener {
            shareImage(imageUri)
        }
    }

    private fun shareImage(imageUri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}
