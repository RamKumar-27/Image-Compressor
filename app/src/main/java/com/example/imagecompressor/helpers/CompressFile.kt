package com.example.imagecompressor.helpers


import android.content.Context
import android.util.Log

import java.io.File

import rx.Observable

import android.support.constraint.Constraints.TAG

class CompressFile {
    private var context: Context? = null
    private var originalFile: File? = null
    private var originalFileList: List<File>? = null

    val filename: String
        get() {
            val mImageName = "IMG_" + System.currentTimeMillis().toString() + ".jpg"
            return getPhotoCacheDir(context)!!.absolutePath + File.separator + mImageName
        }

    fun compress(context: Context, file: File): Observable<File> {
        this.context = context
        this.originalFile = file
        return asObservable(originalFile)
    }

    fun compress(context: Context, fileList: List<File>): Observable<List<File>> {
        this.context = context
        this.originalFileList = fileList
        return asListObservable(originalFileList)
    }

    fun asObservable(originalFile: File?): Observable<File> {
        val compresser = ImageCompressor()
        return compresser.singleAction(filename, originalFile!!)
    }

    fun asListObservable(originalFileList: List<File>?): Observable<List<File>> {
        val compresser = ImageCompressor()
        return compresser.multipleAction(filename, originalFileList!!)
    }

    private fun getPhotoCacheDir(context: Context?): File? {
        return getPhotoCacheDir(context!!, DEFAULT_DISK_CACHE_DIR)
    }

    companion object {
        private val DEFAULT_DISK_CACHE_DIR = "app_disk_cache"

        private fun getPhotoCacheDir(context: Context, cacheName: String): File? {
            val cacheDir = context.cacheDir
            if (cacheDir != null) {
                val result = File(cacheDir, cacheName)
                return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) {
                    // File wasn't able to create a directory, or the result exists but not a directory
                    null
                } else result
            }
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "default disk cache dir is null")
            }
            return null
        }
    }
}
