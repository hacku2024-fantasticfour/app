package com.example.huck_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class Notification(private val context: Context, private val textToSpeech: TextToSpeech) {

    fun showNotification(label: String) {
        val title = "検出結果"
        val content = "”$label”を検出しました"

        // ラベルを読み上げる
        val speechText = when (label) {
            "bottle" -> "ボトルの危険を検知しました。"
            else -> "$label の危険を検出しました。"
        }
        textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, null)

        // 通知の表示前に権限をチェック
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            // 通知チャネルの作成（Android O以降）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "important_notifications"
                val channelName = "重要なお知らせ"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = "ユーザーに重要な通知を提供するためのチャンネル"
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // 通知のビルダーを作成
            val notificationBuilder = NotificationCompat.Builder(context, "important_notifications")
                .setSmallIcon(R.drawable.notification_icon)  // 通知のアイコンを適切なリソースに変更
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            // 通知を表示
            with(NotificationManagerCompat.from(context)) {
                notify(1, notificationBuilder.build())
            }

        } else {
            // 権限がない場合、ログに警告を表示する
            Log.w("Notification", "POST_NOTIFICATIONS permission is not granted.")
        }
    }
}
