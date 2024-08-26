package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_home_fragment, container, false)


        val childButton = view.findViewById<Button>(R.id.ditector_button)
        childButton?.setOnClickListener {
            val intent = Intent(activity, Detector_Page::class.java)
            intent.putExtra("BUTTON_ID", R.id.ditector_button)
            startActivity(intent)
        }
        val SettingButton = view.findViewById<Button>(R.id.setting_button)
        SettingButton?.setOnClickListener {
            val intent = Intent(activity, Setting::class.java)
            startActivity(intent)
        }

        return view
    }
}
