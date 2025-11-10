package com.example.photoclassifier

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photoclassifier.databinding.ActivityGalleryBinding
import java.io.File

/**
 * 相册Activity
 * 显示和管理照片
 */
class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var fileManager: FileManager
    private lateinit var photoAdapter: PhotoAdapter
    
    private val folders = mutableListOf<File>()
    private var selectedFolder: File? = null
    private var currentPhotos = mutableListOf<Photo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileManager = FileManager(this)

        setupRecyclerView()
        setupFolderSpinner()
        setupClickListeners()
        loadPhotos()
    }

    /**
     * 设置RecyclerView
     */
    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(
            photos = currentPhotos,
            onDeleteClick = { photo ->
                showDeleteConfirmDialog(photo)
            },
            onPhotoClick = { photo ->
                openPhotoView(photo)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 2)
            adapter = photoAdapter
        }
    }

    /**
     * 打开照片预览
     */
    private fun openPhotoView(photo: Photo) {
        val intent = Intent(this, PhotoViewActivity::class.java).apply {
            putExtra(PhotoViewActivity.EXTRA_PHOTO_PATH, photo.path)
            putExtra(PhotoViewActivity.EXTRA_PHOTO_NAME, photo.name)
        }
        startActivity(intent)
    }

    /**
     * 设置文件夹选择器
     */
    private fun setupFolderSpinner() {
        loadFolders()
        
        binding.spinnerFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // "所有照片"
                    selectedFolder = null
                } else {
                    selectedFolder = folders[position - 1]
                }
                loadPhotos()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedFolder = null
                loadPhotos()
            }
        }
    }

    /**
     * 加载文件夹列表
     */
    private fun loadFolders() {
        folders.clear()
        folders.addAll(fileManager.getAllFolders())
        
        val folderNames = mutableListOf(getString(R.string.all_photos))
        folderNames.addAll(folders.map { it.name })
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFolder.adapter = adapter
    }

    /**
     * 设置点击监听器
     */
    private fun setupClickListeners() {
        binding.btnCreateFolder.setOnClickListener {
            showCreateFolderDialog()
        }
    }

    /**
     * 加载照片
     */
    private fun loadPhotos() {
        currentPhotos.clear()
        
        val photos = if (selectedFolder == null) {
            fileManager.getAllPhotos()
        } else {
            fileManager.getPhotosInFolder(selectedFolder!!)
        }
        
        currentPhotos.addAll(photos)
        photoAdapter.updatePhotos(currentPhotos)
        
        // 显示/隐藏空状态
        if (currentPhotos.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    /**
     * 显示创建文件夹对话框
     */
    private fun showCreateFolderDialog() {
        val editText = EditText(this).apply {
            hint = getString(R.string.folder_name)
            setPadding(50, 20, 50, 20)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.create_folder)
            .setView(editText)
            .setPositiveButton(R.string.ok) { _, _ ->
                val folderName = editText.text.toString().trim()
                createFolder(folderName)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * 创建文件夹
     */
    private fun createFolder(folderName: String) {
        if (folderName.isBlank()) {
            Toast.makeText(this, R.string.invalid_folder_name, Toast.LENGTH_SHORT).show()
            return
        }

        val success = fileManager.createFolder(folderName)
        if (success) {
            Toast.makeText(this, R.string.folder_created, Toast.LENGTH_SHORT).show()
            loadFolders()
        } else {
            Toast.makeText(this, R.string.folder_exists, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 显示删除确认对话框
     */
    private fun showDeleteConfirmDialog(photo: Photo) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(R.string.ok) { _, _ ->
                deletePhoto(photo)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * 删除照片
     */
    private fun deletePhoto(photo: Photo) {
        val success = fileManager.deletePhoto(photo)
        if (success) {
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show()
            loadPhotos()
        } else {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // 从相机返回时刷新照片列表
        loadPhotos()
    }
}
