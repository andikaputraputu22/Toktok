package com.moonlightsplitter.toktok.utils

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
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

                preloadVideo(
                    dataSpec,
                    simpleCache,
                    dataSource
                )
            }
        }
        jobs?.joinAll()
        Result.success()
    }

    private fun preloadVideo(
        dataSpec: DataSpec?,
        cache: Cache?,
        dataSource: DataSource
    ) {
        try {
            val upstreamFactory = DataSource.Factory { dataSource }
            val cacheDataSourceFactory = cache?.let {
                CacheDataSource.Factory()
                    .setCache(it)
                    .setUpstreamDataSourceFactory(upstreamFactory)
                    .setCacheWriteDataSinkFactory(null)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
            }

            val cacheDataSource = cacheDataSourceFactory?.createDataSource()
            val dataSpecUri = dataSpec?.uri ?: throw IllegalArgumentException("DataSpec URI is null")

            cacheDataSource?.open(DataSpec(dataSpecUri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}