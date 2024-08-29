package com.example.huck_app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class LogFragment : Fragment() {

    private lateinit var interpreter: Interpreter
    private lateinit var imageView: ImageView
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var labels: List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // activity_log_fragment.xml レイアウトをインフレート
        val view = inflater.inflate(R.layout.activity_log_fragment, container, false)

        // ImageViewの設定
        imageView = view.findViewById(R.id.detected_image_view)

        // YOLOモデルの読み込み
        interpreter = Interpreter(loadModelFile("yolov8s_float32.tflite"))

        // ラベルの読み込み
        labels = loadLabels("labels.txt")

        // 映像ストリームの監視を開始
        startMonitoringStream()

        return view
    }

    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val assetFileDescriptor = requireContext().assets.openFd(modelFileName)
        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabels(labelsFileName: String): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = requireContext().assets.open(labelsFileName)
        BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
            lines.forEach { labels.add(it) }
        }
        return labels
    }

    private fun startMonitoringStream() {
        executor.execute {
            val url = URL("http://192.168.1.129:8080/camera.mjpg")
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // YOLOによる物体検出を実行
            val detectedObjects = runObjectDetection(bitmap)

            // 「bottle」が検出されたか確認
            if (detectedObjects.contains("bottle")) {
                activity?.runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                    Toast.makeText(activity, "Bottle detected!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun runObjectDetection(bitmap: Bitmap): List<String> {
        // 入力データの準備
        val inputArray = arrayOf(arrayOf(bitmap))
        val outputMap = HashMap<Int, Any>()
        // 出力サイズに合わせた適切なデータ構造を作成する必要があります

        // モデルの実行
        interpreter.runForMultipleInputsOutputs(inputArray, outputMap)

        // 検出結果を解析して、検出されたラベルを返す
        // ここでは仮に "bottle" を検出したとする
        return listOf("bottle")
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
        executor.shutdown()
    }
}
