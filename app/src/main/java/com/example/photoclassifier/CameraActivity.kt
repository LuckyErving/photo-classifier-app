package com.example.photoclassifier

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.photoclassifier.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 相机Activity
 * 使用CameraX实现拍照功能
 */
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var fileManager: FileManager
    
    private var imageCapture: ImageCapture? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraExecutor: ExecutorService
    
    private var selectedFolder: File? = null
    private val folders = mutableListOf<File>()

    companion object {
        private const val TAG = "CameraActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileManager = FileManager(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        setupFolderSpinner()
        setupClickListeners()
        startCamera()
    }

    /**
     * 设置文件夹选择器
     */
    private fun setupFolderSpinner() {
        folders.clear()
        folders.addAll(fileManager.getAllFolders())
        
        val folderNames = folders.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFolder.adapter = adapter
        
        // 优先加载上次选择的文件夹，否则选择默认文件夹
        val lastFolder = fileManager.getLastSelectedFolder()
        val folderIndex = if (lastFolder != null) {
            folders.indexOfFirst { it.absolutePath == lastFolder.absolutePath }
        } else {
            -1
        }
        
        if (folderIndex >= 0) {
            binding.spinnerFolder.setSelection(folderIndex)
            selectedFolder = folders[folderIndex]
        } else if (folders.isNotEmpty()) {
            selectedFolder = folders[0]
        }
        
        binding.spinnerFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFolder = folders[position]
                // 保存用户选择的文件夹
                selectedFolder?.let { fileManager.saveLastSelectedFolder(it) }
                Toast.makeText(
                    this@CameraActivity,
                    "${getString(R.string.current_folder)}: ${folders[position].name}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedFolder = fileManager.getDefaultFolder()
            }
        }
    }

    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        binding.btnSwitchCamera.setOnClickListener {
            switchCamera()
        }

        binding.btnNewFolder.setOnClickListener {
            showCreateFolderDialog()
        }
    }

    /**
     * 显示创建文件夹对话框
     */
    private fun showCreateFolderDialog() {
        val editText = EditText(this)
        editText.hint = getString(R.string.folder_name)
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.create_folder))
            .setView(editText)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val folderName = editText.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    if (fileManager.createFolder(folderName)) {
                        Toast.makeText(this, getString(R.string.folder_created), Toast.LENGTH_SHORT).show()
                        // 刷新文件夹列表
                        setupFolderSpinner()
                        // 选择新创建的文件夹
                        val newFolderIndex = folders.indexOfFirst { it.name == folderName }
                        if (newFolderIndex >= 0) {
                            binding.spinnerFolder.setSelection(newFolderIndex)
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.folder_exists), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.invalid_folder_name), Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * 启动相机
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 预览
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // 图像捕获
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            try {
                // 解绑所有用例
                cameraProvider.unbindAll()

                // 绑定用例到相机
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (e: Exception) {
                Log.e(TAG, "Use case binding failed", e)
                Toast.makeText(this, "相机启动失败", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val folder = selectedFolder ?: fileManager.getDefaultFolder()
        val photoFile = fileManager.createPhotoFile(folder)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "${getString(R.string.photo_saved)}: ${photoFile.name}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    Toast.makeText(
                        baseContext,
                        getString(R.string.photo_save_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    /**
     * 切换前后摄像头
     */
    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
