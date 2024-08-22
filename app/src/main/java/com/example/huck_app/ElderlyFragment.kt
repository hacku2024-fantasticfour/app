package com.example.huck_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class ElderlyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_elderly_fragment, container, false)


        val Lv = view.findViewById<Button>(R.id.Elderly_living)
        Lv.setOnClickListener {

            val intent = Intent(activity, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.Elderly_living)
            startActivity(intent)

        }
        val Bath = view.findViewById<Button>(R.id.Elderly_bathroom)
        Bath.setOnClickListener {

            val intent = Intent(activity, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.Elderly_bathroom)
            startActivity(intent)
        }

        val Hw = view.findViewById<Button>(R.id.Elderly_hollway)
        Hw.setOnClickListener {

            val intent = Intent(activity, CameraMode::class.java)
            intent.putExtra("BUTTON_ID", R.id.Elderly_hollway)
            startActivity(intent)
        }

        return view
    }
}
