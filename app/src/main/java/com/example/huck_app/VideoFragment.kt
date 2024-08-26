package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button // または MaterialButton を使用する場合は、このインポートを使用
import androidx.fragment.app.Fragment

class VideoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_video_fragment, container, false)

        val videoStartButton = view.findViewById<Button>(R.id.video_start) // または MaterialButton でキャスト

        videoStartButton.setOnClickListener {
            val intent = Intent(activity, VideoMode::class.java)
            startActivity(intent)
        }

        return view
    }
}
