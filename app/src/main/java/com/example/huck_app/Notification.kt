import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.net.Uri
import android.os.Build
import com.example.huck_app.R

class Notification : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "important_notifications"
            val channelName = "重要なお知らせ"
            val channelDescription = "ユーザーに重要な通知を提供するためのチャンネル"
            val importance = NotificationManager.IMPORTANCE_HIGH

            // カスタムサウンドのURIを設定
            val soundUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.custom_sound)

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                setSound(soundUri, null)  // サウンドを設定
            }

            // 通知チャンネルをシステムに登録
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
