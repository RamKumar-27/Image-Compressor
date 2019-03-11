package com.example.imagecompressor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v4.content.PermissionChecker
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.imagecompressor.helpers.RxJavaUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CAMERA = 10001
    private val CAMERA_REQ_ID = 1243
    private var selectedImageFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_camera.setOnClickListener { checkCameraPermission() }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (permissions[0] == Manifest.permission.CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && permissions[1] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            compressImage()
        }
    }

    private fun compressImage() {
        setGlideImage(imageView, selectedImageFile!!.absolutePath)
        tv_size.text = getSizeFromFile(selectedImageFile!!.length())
        CompressApplication.getInstanse()!!.getFileCompress()
            .compress(this, selectedImageFile!!)
            .compose(RxJavaUtils.applyObserverSchedulers<File>())
            .subscribe({ file ->
                setGlideImage(imageView2, file!!.absolutePath)
                tv_size2.text = getSizeFromFile(file.length())


            }, { throwable -> throwable.printStackTrace() })
    }

    private fun setGlideImage(view: ImageView?, path: String?) {
        Glide
            .with(this)
            .load(path)
            .into(view!!);
    }

    private fun openCamera() {
        val intent = Intent("android.media.action.IMAGE_CAPTURE")

        val folder = File(BuildConfig.ATTACHMENT_DOWNLOAD_FILES_FOLDER)
        if (!folder.exists())
            folder.mkdir()

        val photoFile =
            File(BuildConfig.ATTACHMENT_DOWNLOAD_FILES_FOLDER, System.currentTimeMillis().toString() + ".jpg")
        if (!photoFile.exists())
            photoFile.createNewFile()

        selectedImageFile = photoFile
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, photoFile)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA_REQ_ID)
    }


    private fun checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED || PermissionChecker.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CAMERA
                )
                return
            }
        }
        openCamera()

    }

    fun getSizeFromFile(size: Long): String {
        var hrSize: String? = null

        val b = size.toDouble()
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0

        val dec = DecimalFormat("0.00")

        if (t > 1) {
            hrSize = dec.format(t) + " TB"
        } else if (g > 1) {
            hrSize = dec.format(g) + " GB"
        } else if (m > 1) {
            hrSize = dec.format(m) + " MB"
        } else if (k > 1) {
            hrSize = dec.format(k) + " KB"
        } else {
            hrSize = dec.format(b) + " Bytes"
        }

        return hrSize
    }

}
