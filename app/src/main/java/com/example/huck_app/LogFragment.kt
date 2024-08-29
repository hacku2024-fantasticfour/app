// LogFragment.kt
package com.example.huck_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.huck_app.com.example.huck_app.LogFragmentViewModel

class LogFragment : Fragment() {

    private val viewModel: LogFragmentViewModel by viewModels()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // IntentからBitmapを取得
            val bitmap = intent.getParcelableExtra<Bitmap>("detected_image")
            if (bitmap != null) {
                // ViewModelを通じてBitmapを設定
                viewModel.setDetectedImage(bitmap)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_log_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ImageViewを取得
        val detectedImageView: ImageView = view.findViewById(R.id.detectedImageView)

        // ViewModelのLiveDataを監視し、変更があればImageViewに表示
        viewModel.detectedImage.observe(viewLifecycleOwner, Observer { bitmap ->
            detectedImageView.setImageBitmap(bitmap)
        })

        // LocalBroadcastManagerを使用してブロードキャストを登録
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(broadcastReceiver, IntentFilter("com.example.huck_app.BOTTLE_DETECTED"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // LocalBroadcastManagerの登録解除
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}
