package com.android.musicplayer.utils.player.cache

import android.content.Context
import android.util.Log
import com.android.musicplayer.BuildConfig
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

class DataSourceWithCache private constructor(
    private val cacheName: String,
    private val BANDWIDTH_METER: DefaultBandwidthMeter
) {

    private var simpleCache: SimpleCache? = null

    fun buildDataSourceFactory(context: Context, cache: Boolean): DataSource.Factory {

        Log.d(TAG, "buildDataSourceFactory: ")

        if (!cache) {
            Log.d(TAG, "buildDataSourceFactory: NO CACH")

            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, BuildConfig.APPLICATION_ID),
                BANDWIDTH_METER
            )
            return DefaultDataSourceFactory(
                context, BANDWIDTH_METER,
                dataSourceFactory
            )

        } else {

            Log.d(TAG, "buildDataSourceFactory: USE CACH")

            return DataSource.Factory {
                Log.d(TAG, "buildDataSourceFactory: createDataSource")

                createDataSourceWithCache(context)
            }
        }
    }

    private fun createDataSourceWithCache(context: Context): DataSource {
        Log.d(TAG, "buildDataSourceFactory: createDataSource")

        val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE.toLong())
        val cacheDir = File(context.cacheDir, cacheName)
        Log.d(TAG, "createDataSource: cacheDir: $cacheDir")

        if (simpleCache == null) {
            Log.d(TAG, "createDataSource: Create new oe of SimpleCache")
            simpleCache = SimpleCache(cacheDir, evictor)
        }

        return CacheDataSource(
            simpleCache,
            buildCachedHttpDataSourceFactory(context, BANDWIDTH_METER).createDataSource(),
            FileDataSource(),
            CacheDataSink(simpleCache!!, MIN_CACHE_FILE_SIZE.toLong()),
            CacheDataSource.FLAG_BLOCK_ON_CACHE or CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
            null
        )
    }

    private fun buildCachedHttpDataSourceFactory(
        context: Context,
        bandwidthMeter: DefaultBandwidthMeter
    ): DefaultDataSourceFactory {
        Log.d(TAG, "buildDataSourceFactory: buildCachedHttpDataSourceFactory")

        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, BuildConfig.APPLICATION_ID),
            BANDWIDTH_METER
        )
        return DefaultDataSourceFactory(context, bandwidthMeter, dataSourceFactory)
    }

    companion object {
        private val TAG = DataSourceWithCache::class.java.simpleName

        private val MAX_CACHE_SIZE = 100 * 1024 * 1024
        private val MIN_CACHE_FILE_SIZE = 10 * 1024 * 1024
        private var INSTANCE: DataSourceWithCache? = null

        fun createDataSource(
            cacheName: String,
            bandwidthMeter: DefaultBandwidthMeter
        ): DataSourceWithCache {

            if (INSTANCE == null)
                INSTANCE = DataSourceWithCache(cacheName, bandwidthMeter)

            return INSTANCE!!
        }
    }
}