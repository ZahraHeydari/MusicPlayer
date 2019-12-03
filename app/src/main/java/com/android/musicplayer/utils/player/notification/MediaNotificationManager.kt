package com.android.musicplayer.utils.player.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import coil.Coil
import coil.api.load
import com.android.musicplayer.R
import com.android.musicplayer.data.model.Song
import com.android.musicplayer.utils.player.exo.PlaybackState
import com.android.musicplayer.utils.player.helper.ResourceHelper
import com.android.musicplayer.utils.player.service.PlayerService
import com.android.musicplayer.presentation.songplayer.SongPlayerActivity

/**
 * This class is responsible for managing Notification
 *
 * @author ZARA
 * */
class MediaNotificationManager @Throws(RemoteException::class)
constructor(private val mService: PlayerService) : BroadcastReceiver() {

    private val TAG = MediaNotificationManager::class.java.name
    private var mNotificationManager: NotificationManager? = null
    private val mPlayIntent: PendingIntent
    private val mPauseIntent: PendingIntent
    private val mPreviousIntent: PendingIntent
    private val mNextIntent: PendingIntent
    private val mStopIntent: PendingIntent
    private val mStopCastIntent: PendingIntent
    private val mNotificationColor: Int =
        ResourceHelper.getThemeColor(mService, R.attr.colorPrimary, Color.DKGRAY)
    private var mStarted = false
    private var mCollapsedRemoteViews: RemoteViews =
        RemoteViews(mService.packageName, R.layout.player_small_notification)
    private var mExpandedRemoteViews: RemoteViews =
        RemoteViews(mService.packageName, R.layout.player_big_notification)
    private var notificationBuilder: NotificationCompat.Builder? = null


    init {
        mNotificationManager =
            mService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val packageName = mService.packageName
        mPauseIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_PAUSE).setPackage(packageName), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mPlayIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_PLAY).setPackage(packageName), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mPreviousIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_PREV).setPackage(packageName), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mNextIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_NEXT).setPackage(packageName), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mStopIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_STOP).setPackage(packageName), PendingIntent.FLAG_CANCEL_CURRENT
        )
        mStopCastIntent = PendingIntent.getBroadcast(
            mService, REQUEST_CODE,
            Intent(ACTION_STOP_CASTING).setPackage(packageName),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        // Cancel all notifications to handle the case where the Service was killed and restarted by the system.
        mNotificationManager?.cancelAll()
    }

    /**
     * To start notification and service
     */
    fun startNotification() {
        if (!mStarted) {
            mStarted = true
            // The notification must be updated after setting started to true
            val notification = createOrUpdateNotification()
            val filter = IntentFilter()
            filter.addAction(ACTION_NEXT)
            filter.addAction(ACTION_PAUSE)
            filter.addAction(ACTION_PLAY)
            filter.addAction(ACTION_PREV)
            filter.addAction(ACTION_STOP)
            filter.addAction(ACTION_STOP_CASTING)
            mService.registerReceiver(this, filter)
            mService.startForeground(NOTIFICATION_ID, notification)
        }
    }

    /**
     * To stop notification and service
     */
    fun stopNotification() {
        if (mStarted) {
            mStarted = false
            mNotificationManager?.cancel(NOTIFICATION_ID)
            mService.unregisterReceiver(this)
            mService.stopForeground(true)
        }
    }

    fun updateNotification() {
        createOrUpdateNotification()
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAUSE -> {
                mService.pause()
                updateNotification()
            }
            ACTION_PLAY -> {
                mService.getCurrentSong()?.let {
                    mService.play(it)
                }
            }
            ACTION_NEXT -> mService.skipToNext()
            ACTION_PREV -> mService.skipToPrevious()
            ACTION_STOP -> mService.stop()
            ACTION_STOP_CASTING -> {
                val i = Intent(context, PlayerService::class.java)
                i.action = PlayerService.ACTION_CMD
                i.putExtra(PlayerService.CMD_NAME, PlayerService.CMD_STOP_CASTING)
                mService.startService(i)
            }
            else -> Log.w(TAG, "Unknown intent ignored.")
        }
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(mService, SongPlayerActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        mService.getCurrentSong()?.let {
            openUI.putExtra(Song::class.java.name, it as Song)
        }
        mService.getCurrentSongList()?.let {
            openUI.putExtra(SongPlayerActivity.SONG_LIST_KEY, it)
        }
        return PendingIntent.getActivity(
            mService,
            REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun createOrUpdateNotification(): Notification? {
        if (notificationBuilder == null) {
            notificationBuilder = NotificationCompat.Builder(mService, CHANNEL_ID)
            notificationBuilder?.setSmallIcon(R.drawable.itunes)
                ?.setLargeIcon(
                    BitmapFactory.decodeResource(
                        mService.resources,
                        R.drawable.itunes
                    )
                )
                ?.setContentTitle(mService.getString(R.string.app_name))
                ?.setContentText(mService.getString(R.string.app_name))
                ?.setDeleteIntent(mStopIntent)
                ?.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                ?.setOnlyAlertOnce(true)
                ?.setContentIntent(createContentIntent())
                ?.setCustomContentView(mCollapsedRemoteViews)
                ?.setCustomBigContentView(mExpandedRemoteViews)

            // Notification channels are only supported on Android O+.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
        }
        loadNotificationView()
        mNotificationManager?.notify(NOTIFICATION_ID, notificationBuilder?.build())
        return notificationBuilder?.build()

    }

    private fun loadNotificationView() {
        // To make sure that the notification can be dismissed by the user when we are not playing.
        notificationBuilder?.setOngoing(mService.getSongPlayingState() == PlaybackState.STATE_PLAYING)

        createCollapsedRemoteViews(mCollapsedRemoteViews)
        createExpandedRemoteViews(mExpandedRemoteViews)

        Log.i(TAG, "notification current song: ${mService.getCurrentSong()}")
        mService.getCurrentSong()?.getFeatureAvatar()?.let {nonNullImage->
            Coil.load(this.mService, nonNullImage) {
                mCollapsedRemoteViews.setImageViewUri(R.id.notification_image_view, Uri.parse(nonNullImage))
                mExpandedRemoteViews.setImageViewUri(R.id.notification_image_view, Uri.parse(nonNullImage))
                placeholder(R.drawable.placeholder)
                error(R.drawable.placeholder)
            }
        }

        Log.i(TAG, "mService.getSongPlayingState(): ${mService.getSongPlayingState()}")
        if (mService.getSongPlayingState() == PlaybackState.STATE_PLAYING ||
            mService.getSongPlayingState() == PlaybackState.STATE_BUFFERING
        ) showPauseIcon() else showPlayIcon()

    }

    private fun showPlayIcon() {
        mCollapsedRemoteViews.setViewVisibility(R.id.notification_pause_image_view, View.GONE)
        mCollapsedRemoteViews.setViewVisibility(R.id.notification_play_image_view, View.VISIBLE)
        mExpandedRemoteViews.setViewVisibility(R.id.notification_pause_image_view, View.GONE)
        mExpandedRemoteViews.setViewVisibility(R.id.notification_play_image_view, View.VISIBLE)
    }

    private fun showPauseIcon() {
        mCollapsedRemoteViews.setViewVisibility(
            R.id.notification_pause_image_view,
            View.VISIBLE
        )
        mCollapsedRemoteViews.setViewVisibility(R.id.notification_play_image_view, View.GONE)
        mExpandedRemoteViews.setViewVisibility(R.id.notification_pause_image_view, View.VISIBLE)
        mExpandedRemoteViews.setViewVisibility(R.id.notification_play_image_view, View.GONE)
    }

    private fun createExpandedRemoteViews(expandedRemoteViews: RemoteViews) {
        if (isSupportExpand) {
            setRemoteViewsListeners(expandedRemoteViews)
            // use a placeholder art while the remote art is being downloaded
            expandedRemoteViews.setImageViewResource(
                R.id.notification_image_view,
                R.drawable.placeholder
            )
        }
        expandedRemoteViews.setViewVisibility(R.id.notification_skip_next_image_view, View.VISIBLE)
        expandedRemoteViews.setViewVisibility(R.id.notification_skip_back_image_view, View.VISIBLE)
        expandedRemoteViews.setTextViewText(
            R.id.notification_song_name_text_view,
            mService.getCurrentSong()?.getName()
        )
        expandedRemoteViews.setTextViewText(
            R.id.notification_singer_name_text_view,
            mService.getCurrentSong()?.getSingerName()
        )

    }

    private fun createCollapsedRemoteViews(collapsedRemoteViews: RemoteViews) {
        setRemoteViewsListeners(collapsedRemoteViews)
        // use a placeholder art while the remote art is being downloaded
        collapsedRemoteViews.setImageViewResource(
            R.id.notification_image_view,
            R.drawable.placeholder
        )
        collapsedRemoteViews.setViewVisibility(R.id.notification_skip_next_image_view, View.VISIBLE)
        collapsedRemoteViews.setViewVisibility(R.id.notification_skip_back_image_view, View.VISIBLE)
        collapsedRemoteViews.setTextViewText(
            R.id.notification_song_name_text_view,
            mService.getCurrentSong()?.getName()
        )
        collapsedRemoteViews.setTextViewText(
            R.id.notification_singer_name_text_view,
            mService.getCurrentSong()?.getSingerName()
        )
    }


    private fun setRemoteViewsListeners(remoteViews: RemoteViews) {
        try {
            remoteViews.setOnClickPendingIntent(R.id.notification_skip_back_image_view, mPreviousIntent)
            remoteViews.setOnClickPendingIntent(R.id.notification_clear_image_view, mStopIntent)
            remoteViews.setOnClickPendingIntent(R.id.notification_pause_image_view, mPauseIntent)
            remoteViews.setOnClickPendingIntent(R.id.notification_skip_next_image_view, mNextIntent)
            remoteViews.setOnClickPendingIntent(R.id.notification_play_image_view, mPlayIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
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

        private val isSupportExpand = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
        private const val ACTION_PAUSE = "app.pause"
        private const val ACTION_PLAY = "app.play"
        private const val ACTION_PREV = "app.prev"
        private const val ACTION_NEXT = "app.next"
        private const val ACTION_STOP = "app.stop"
        private const val ACTION_STOP_CASTING = "app.stop_cast"
        private const val CHANNEL_ID = "app.MUSIC_CHANNEL_ID"
        private const val NOTIFICATION_ID = 412
        private const val REQUEST_CODE = 100
    }
}

