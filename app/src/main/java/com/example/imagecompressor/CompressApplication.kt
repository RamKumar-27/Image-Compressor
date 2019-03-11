package com.example.imagecompressor

import android.app.Application
import com.example.imagecompressor.helpers.CompressFile

class CompressApplication : Application() {

    companion object {
        var compressApplication: CompressApplication? = null
        fun getInstanse(): CompressApplication? {
            return compressApplication
        }
    }
    private var fileCompress: CompressFile? = null


    override fun onCreate() {
        super.onCreate()

        compressApplication=this
        fileCompress = CompressFile()

    }

    fun getFileCompress(): CompressFile {
        return fileCompress!!
    }

}