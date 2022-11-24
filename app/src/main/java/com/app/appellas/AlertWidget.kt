package com.app.appellas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import com.app.appellas.R
import com.app.appellas.data.models.dtos.body.CreateLocationBody
import com.app.appellas.data.network.AppAPI
import com.app.appellas.interactors.PostAlertaWigdet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.net.URL
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class AlertWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    var ACTION_WIDGET = "ActionReceiver"

    lateinit var sharedPref: SharedPreferences
    var token_session: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var id_user: String? = null

    val postPanicows = PostAlertaWigdet(AppAPI.widgetService)

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent) // <-- it's the trick
        Log.d("AppDebug", "PASS")

        if (context != null) {
            sharedPref = context.getSharedPreferences("appellas", Context.MODE_PRIVATE)
            token_session = sharedPref.getString("token_session", "")
            latitude = sharedPref.getString("latitude", "")
            longitude = sharedPref.getString("longitude", "")
            id_user = sharedPref.getString("id_user", "")

            Log.d("AppDebug", "onReceive:${token_session} ${latitude} ${longitude}")
        }

        if (intent?.action.equals(ACTION_WIDGET)) {
            //notification(context!!)

            scope.launch {
                postPanicows.execute(
                    token_session!!,
                    CreateLocationBody(
                        latitude!!.toDouble(),
                        longitude!!.toDouble(),
                        1,
                        id_user!!.toInt(),
                    ), true
                ).onEach { dataState ->

                    dataState.loading?.let { isload ->

                        /*if(isload){
                            progressDialog.show()
                        }else{
                        progressDialog.dismiss()
                    }*/
                    }

                    dataState.error?.let { error ->
                    }

                    dataState.data?.let { data ->
                        Log.d("SIUUUU", "SUIIIII")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Alerta enviada!", Toast.LENGTH_SHORT).show()
                        }
                        /* if (data.errorCode == 1) {
                        messageToast(data.message)

                    } else {
                    messageToast(data.message)

                }*/
                    }
                }.launchIn(scope)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them

        sharedPref = context.getSharedPreferences("appellas", Context.MODE_PRIVATE)
        token_session = sharedPref.getString("token_session", "")
        latitude = sharedPref.getString("latitude", "")
        longitude = sharedPref.getString("longitude", "")
        id_user = sharedPref.getString("id_user", "")

        for (appWidgetId in appWidgetIds) {
            with(sharedPref.edit()) {
                putInt("clicks", 0)
                commit()
            }
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId,
                scope,
                postPanicows,
                token_session
            )
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

/*private fun notification(context: Context) {
    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("chat", "chat", importancia).let {
                it.enableLights(true)
                it.lightColor = Color.GREEN
                it.enableVibration(true)
                it
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(context, "chat").also { noti ->
                noti.setContentTitle("widget")
                noti.setContentText("PruebA")
                noti.setAutoCancel(true)
                noti.setSmallIcon(R.mipmap.ic_launcher)
            }.build()

            val notificationManager = NotificationManagerCompat.from(context)
            val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            notificationManager.notify(m, notification)
        }
    } else {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("chat", "chat", importancia)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(context, "chat").also { noti ->
                noti.setContentTitle("Widget")
                noti.setContentText("Prueba")
                noti.setAutoCancel(true)
                noti.setSmallIcon(R.mipmap.ic_launcher)
            }.build()

            val notificationManager = NotificationManagerCompat.from(context)
            val m = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            notificationManager.notify(m, notification)
        }
    }
}*/

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    scope: CoroutineScope,
    postPanicoWidget: PostAlertaWigdet,
    token_session: String?
) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.alert_widget)


    val active = Intent(context, AlertWidget::class.java)

    active.action = "ActionReceiver";
    val actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, PendingIntent.FLAG_MUTABLE)
    views.setOnClickPendingIntent(R.id.btnSolicitar, actionPendingIntent)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}