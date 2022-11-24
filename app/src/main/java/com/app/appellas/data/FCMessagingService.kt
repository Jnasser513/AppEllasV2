package com.app.appellas.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.app.appellas.R
import com.app.appellas.ui.views.activitys.BottomNavActivity
import com.app.appellas.ui.views.activitys.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import java.util.*

class FCMessagingService: FirebaseMessagingService() {

    private val channelID = "chat"
    private val channelName = "chat"
    private var mensaje: String? = ""
    private var titulo: String? = ""
    private var id: String? = ""
    private var imagenUrl: String? = ""
    private lateinit var intent: Intent

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("BODY", message.data.toString())

        if(message.data.isNotEmpty()) {
            titulo = message.data["titulo"]
            id = message.data["id"]
            mensaje = message.data["message"]
            imagenUrl = message.data["imagenURL"]

            intent = Intent(this, BottomNavActivity::class.java)
            intent.putExtra("id", id)
            notification(mensaje, titulo, intent)
        }
    }

    private fun notification(mensaje: String?, titulo: String?, intent: Intent) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val requestCode = System.currentTimeMillis().toInt()
            val pIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importancia = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelID, channelName, importancia).let {
                    it.enableLights(true)
                    it.lightColor = Color.GREEN
                    it.enableVibration(true)
                    it
                }

                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)

                val inputStream = URL(imagenUrl).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val notification = NotificationCompat.Builder(this, channelID).also { noti ->
                    noti.setContentTitle(titulo)
                    noti.setContentText(mensaje)
                    noti.setLargeIcon(bitmap)
                    noti.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(bitmap))
                    noti.setAutoCancel(true)
                    noti.setSmallIcon(R.mipmap.ic_launcher)
                    noti.setContentIntent(pIntent)
                }.build()

                val notificationManager = NotificationManagerCompat.from(applicationContext)
                val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
                notificationManager.notify(m, notification)
            }
        } else {
            val requestCode = System.currentTimeMillis().toInt()
            val pIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(requestCode, PendingIntent.FLAG_MUTABLE)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importancia = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(channelID, channelName, importancia)

                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)

                val inputStream = URL(imagenUrl).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val notification = NotificationCompat.Builder(this, channelID).also { noti ->
                    noti.setContentTitle(titulo)
                    noti.setContentText(mensaje)
                    noti.setLargeIcon(bitmap)
                    noti.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(bitmap))
                    noti.setAutoCancel(true)
                    noti.setSmallIcon(R.mipmap.ic_launcher)
                    noti.setContentIntent(pIntent)
                }.build()

                val notificationManager = NotificationManagerCompat.from(applicationContext)
                val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
                notificationManager.notify(m, notification)
            }
        }
    }

}