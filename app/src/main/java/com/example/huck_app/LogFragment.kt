package com.example.huck_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LogFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // activity_log_fragment.xml レイアウトをインフレート
        val view = inflater.inflate(R.layout.activity_log_fragment, container, false)

        return view
    }
}
