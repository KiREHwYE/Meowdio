package com.kire.audio.notification

//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationCompat.Action
//import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
//import androidx.core.app.NotificationManagerCompat
//import com.kire.audio.R
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NotificationModule {
//
//    @Singleton
//    @Provides
//    fun provideAudioNotificationService(
//        @ApplicationContext context: Context
//    ): AudioNotificationService {
//
//        val notificationBuilder = NotificationCompat.Builder(context, "Main Channel ID")
//            .setContentTitle("Audio Notification")
//            .setContentText("Track is playing")
//            .setSmallIcon(R.drawable.music_icon)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setVisibility(VISIBILITY_PUBLIC)
//
//
//        val notificationManager = NotificationManagerCompat.from(context)
//        val channel = NotificationChannel(
//            "Main Channel ID",
//            "Main Channel",
//            NotificationManager.IMPORTANCE_DEFAULT
//        )
//        notificationManager.createNotificationChannel(channel)
//
//
//        return AudioNotificationService(
//            context = context,
//            notificationBuilder = notificationBuilder,
//            notificationManager = notificationManager,
//        )
//    }
//}