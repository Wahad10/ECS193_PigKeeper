package com.ucdavis.pigkeeper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.ImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SettingsActivity : AppCompatActivity() {

    lateinit var globalVariable: GlobalData
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var picture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        globalVariable = GlobalData.instance
        picture = findViewById(R.id.Picture)

        //Back button, returns to Main Menu Screen
        val buttonBack = findViewById<Button>(R.id.backToMain)
        buttonBack.setOnClickListener{startActivity(Intent(this@SettingsActivity, MainActivity::class.java))}

        val buttonTakePicture = findViewById<Button>(R.id.TakePicture)
        buttonTakePicture.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            } else {
                startCamera()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        val imageCapture = ImageCapture.Builder()
            .build()

        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)

        // Capture an image when the button is clicked
        takePhoto(imageCapture)
    }

    private fun takePhoto(imageCapture: ImageCapture) {
        val photoFile = File(
            externalMediaDirs.firstOrNull(),
            "${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Load the captured image into the ImageView
                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)

                    // Rotate the bitmap to portrait mode
                    val rotatedBitmap = rotateBitmap(bitmap, 90f)

                    picture.setImageBitmap(rotatedBitmap)
                }
            })
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                // Handle permission denied
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 101
    }

    override fun onPause() {
        super.onPause()
        globalVariable.saveData()
    }
}