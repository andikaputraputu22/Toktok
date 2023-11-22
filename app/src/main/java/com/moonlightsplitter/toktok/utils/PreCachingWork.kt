package com.moonlightsplitter.toktok.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.util.Util
import com.moonlightsplitter.toktok.MyApp
import kotlinx.coroutines.coroutineScope
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll

class PreCachingWork(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private var cacheDataSourceFactory: CacheDataSource.Factory? = null
    private val simpleCache = MyApp.simpleCache

    override suspend fun doWork(): Result = coroutineScope {
        val dataList = inputData.getStringArray(Constants.KEY_STORIES_LIST_DATA)
        val jobs = dataList?.map { data ->
            async {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec.Builder()
                    .setUri(dataUri)
                    .setLength(500 * 1024)
                    .setKey(null).build()

                val dataSource = DefaultHttpDataSource.Factory()
                    .setUserAgent(Util.getUserAgent(applicationContext, "toktok")).createDataSource()

//                preloadVideo(
//                    dataSpec,
//                    simpleCache,
//                    dataSource,
//                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, newBytesCached: Long ->
//                        val downloadPercentage = (bytesCached * 100.0
//                                / requestLength)
//                        Log.d(TAG, "downloadPercentage: $downloadPercentage")
//                    }
//                )
            }
        }
        jobs?.joinAll()
        Result.success()
    }

//    private fun preloadVideo(
//        dataSpec: DataSpec?,
//        cache: Cache?,
//        upstream: DataSource?,
//        progressListener: CacheUtil.ProgressListener?
//    ) {
//        Log.d(TAG, "preloadVideo")
//        try {
//            CacheUtil.cache(
//                dataSpec,
//                cache,
//                CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
//                upstream,
//                progressListener,
//                null
//            )
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}