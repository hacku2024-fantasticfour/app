package com.example.huck_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment

class VideoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // activity_video_fragment.xml レイアウトをインフレート
        val view = inflater.inflate(R.layout.activity_video_fragment, container, false)

        // Switch の参照を取得
        val notificationSwitch = view.findViewById<Switch>(R.id.notification_switch)

        // Switch の状態を監視するリスナーを設定
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // 通知をオンにする処理
                enableNotifications()
                Toast.makeText(activity, "通知がオンになりました", Toast.LENGTH_SHORT).show()
            } else {
                // 通知をオフにする処理
                disableNotifications()
                Toast.makeText(activity, "通知がオフになりました", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun enableNotifications() {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 通知チャンネルの作成（見守りビデオ通知）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "video_notifications"
            val channelName = "見守りビデオの通知"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun disableNotifications() {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // APIレベル26以上の場合、チャネルを削除する
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel("video_notifications")
        } else {
            // APIレベルが26未満の場合、特定の通知IDをキャンセルすることで通知を無効化
            notificationManager.cancelAll() // すべての通知をキャンセル
        }
    }
}
