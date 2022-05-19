package com.android.player.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.RemoteException
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.android.player.R
import com.android.player.service.SongPlayerService
import com.google.android.exoplayer2.MediaItem
import java.io.File

/**
 * This class is responsible for managing Notification
 *
 * @author ZARA
 * */
class PlayerNotificationManager @Throws(RemoteException::class)
constructor(private val mService: SongPlayerService) : BroadcastReceiver() {

    private val mPlayIntent: PendingIntent
    private val mPauseIntent: PendingIntent
    private val mPreviousIntent: PendingIntent
    private val mNextIntent: PendingIntent
    private val mStopIntent: PendingIntent
    private var mRemoteViews: RemoteViews? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var mNotificationManager: NotificationManager? = null
    var mStarted = false //To check if notification manager is started or not!
    private val mediaItem: MediaItem?
        get() = mService.getCurrentMediaItem()

    private fun getPackageName(): String {
        return mService.packageName
    }

    init {
        mNotificationManager =
            mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mPauseIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PAUSE).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mPlayIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PLAY).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mPreviousIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_PREV).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mNextIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_NEXT).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mStopIntent = PendingIntent.getBroadcast(
            mService, NOTIFICATION_REQUEST_CODE,
            Intent(ACTION_STOP).setPackage(getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Cancel all notifications to handle the case where the Service was killed and restarted by the system.
        mNotificationManager?.cancelAll()
    }

    /**
     * To start notification and service
     */
    fun createMediaNotification() {
        // The notification must be updated after setting started to true
        val filter = IntentFilter().apply {
            addAction(ACTION_NEXT)
            addAction(ACTION_PAUSE)
            addAction(ACTION_PLAY)
            addAction(ACTION_PREV)
            addAction(ACTION_STOP)
        }
        mService.registerReceiver(this, filter)

        if (!mStarted) {
            mStarted = true
            mService.startForeground(NOTIFICATION_ID, generateNotification())
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAUSE -> mService.pause()
            ACTION_PLAY -> mService.play()
            ACTION_NEXT -> mService.skipToNext()
            ACTION_PREV -> mService.skipToPrevious()
            ACTION_STOP -> {
                mService.run {
                    unregisterReceiver(this@PlayerNotificationManager)
                    stop()
                }
            }
        }
    }

    fun generateNotification(isPlaying: Boolean? = null): Notification? {
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(mService, CHANNEL_ID)
            notificationBuilder?.setSmallIcon(R.drawable.itunes)
                ?.setLargeIcon(BitmapFactory.decodeResource(mService.resources, R.drawable.itunes))
                ?.setContentTitle(mService.getString(R.string.app_name))
                ?.setContentText(mService.getString(R.string.app_name))
                ?.setDeleteIntent(mStopIntent)
                ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                ?.setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                ?.setOnlyAlertOnce(true)

            // Notification channels are only supported on Android O+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
        }

        mRemoteViews = RemoteViews(getPackageName(), R.layout.player_notification_view)
        notificationBuilder?.setCustomContentView(mRemoteViews)
        // To make sure that the notification can be dismissed by the user when we are not playing.
        notificationBuilder?.setOngoing(true)
        mRemoteViews?.let { createRemoteViews(it) }

        mService.getCurrentMediaItem()?.mediaMetadata?.artworkUri?.let {
            val bitmap = BitmapFactory.decodeFile(File(it.path.toString()).path)
            mRemoteViews?.setImageViewBitmap(R.id.notification_image_view, bitmap)
        } ?: run {
            val bitmap = BitmapFactory.decodeResource(mService.resources, R.drawable.placeholder)
            mRemoteViews?.setImageViewBitmap(R.id.notification_image_view, bitmap)
        }
        if (isPlaying == true) showPauseIcon() else showPlayIcon()
        mNotificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
        return notificationBuilder?.build()
    }

    private fun showPlayIcon() {
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_pause_image_view,
            View.GONE
        )
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_play_image_view,
            View.VISIBLE
        )
    }

    private fun showPauseIcon() {
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_pause_image_view,
            View.VISIBLE
        )
        mRemoteViews?.setViewVisibility(
            R.id.expanded_notification_play_image_view,
            View.GONE
        )
    }

    private fun createRemoteViews(remoteViews: RemoteViews) {
        remoteViews.setViewVisibility(
            R.id.expanded_notification_skip_next_image_view,
            View.VISIBLE
        )
        remoteViews.setViewVisibility(
            R.id.expanded_notification_skip_back_image_view,
            View.VISIBLE
        )
        remoteViews.setTextViewText(
            R.id.expanded_notification_song_name_text_view,
            mediaItem?.mediaMetadata?.albumTitle
        )
        remoteViews.setTextViewText(
            R.id.expanded_notification_singer_name_text_view,
            mediaItem?.mediaMetadata?.albumArtist
        )
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        if (mNotificationManager?.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                mService.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description =
                mService.getString(R.string.notification_channel_description)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val ACTION_PAUSE = "app.pause"
        private const val ACTION_PLAY = "app.play"
        private const val ACTION_PREV = "app.prev"
        private const val ACTION_NEXT = "app.next"
        private const val ACTION_STOP = "app.stop"
        private const val CHANNEL_ID = "app.MUSIC_CHANNEL_ID"
        private const val NOTIFICATION_ID = 412
        private const val NOTIFICATION_REQUEST_CODE = 100
    }
}

