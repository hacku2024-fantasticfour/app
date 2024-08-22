package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class EarthquakeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_earthquake_fragment, container, false)


        val childButton = view.findViewById<Button>(R.id.Earth_button)
        childButton.setOnClickListener {
            // ChildModeに移行
            val intent = Intent(activity, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.Earth_button)
            startActivity(intent)
        }

        return view
    }
}
