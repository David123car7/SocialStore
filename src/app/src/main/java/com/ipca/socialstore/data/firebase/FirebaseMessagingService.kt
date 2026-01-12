package com.ipca.socialstore.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ipca.socialstore.R
import com.ipca.socialstore.data.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FirebaseMessagingService: FirebaseMessagingService() {
    @Inject
    lateinit var userRepository: UserRepository
    @Inject
    lateinit var supabase: SupabaseClient

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("Notification", "GG")
        super.onMessageReceived(remoteMessage)

        // Extrair título e corpo
        val title = remoteMessage.notification?.title ?: "Loja Social"
        val body = remoteMessage.notification?.body ?: "Nova notificação"

        showNotification(title, body)
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Novo token: $token")

        val userId = supabase.auth.currentUserOrNull()?.id

        if (userId != null) {
            serviceScope.launch {
                try {
                    userRepository.saveFcmToken(userId = userId, token = token)
                    Log.d("FCM", "Token saved to Supabase successfully")
                } catch (e: Exception) {
                    Log.e("FCM", "Error saving token", e)
                }
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "social_store_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificações Gerais", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Teu ícone
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

