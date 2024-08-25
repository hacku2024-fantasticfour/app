package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment

class ImageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_image_fragment, container, false)

        // ButtonをImageButtonに変更
        val childButton = view.findViewById<ImageButton>(R.id.image_button)
        childButton.setOnClickListener {
            val intent = Intent(activity, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.image_button)
            startActivity(intent)
        }

        return view
    }
}
