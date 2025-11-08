package com.example.photoclassifier

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 文件管理工具类
 * 负责管理照片文件夹和文件操作
 */
class FileManager(private val context: Context) {

    companion object {
        private const val APP_FOLDER = "PhotoClassifier"
        private const val DEFAULT_FOLDER = "Default"
        private const val PREFS_NAME = "PhotoClassifierPrefs"
        private const val KEY_LAST_FOLDER = "last_selected_folder"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 获取应用的根目录（存储在公共DCIM目录下，可通过USB访问）
     */
    fun getAppRootDirectory(): File {
        // 使用公共的DCIM目录，这样用户可以通过USB数据线访问
        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val appDir = File(dcimDir, APP_FOLDER)
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        return appDir
    }

    /**
     * 保存最后选择的文件夹路径
     */
    fun saveLastSelectedFolder(folder: File) {
        prefs.edit().putString(KEY_LAST_FOLDER, folder.absolutePath).apply()
    }

    /**
     * 获取最后选择的文件夹
     */
    fun getLastSelectedFolder(): File? {
        val path = prefs.getString(KEY_LAST_FOLDER, null)
        return path?.let { 
            val folder = File(it)
            if (folder.exists() && folder.isDirectory) folder else null
        }
    }

    /**
     * 获取默认文件夹
     */
    fun getDefaultFolder(): File {
        val defaultFolder = File(getAppRootDirectory(), DEFAULT_FOLDER)
        if (!defaultFolder.exists()) {
            defaultFolder.mkdirs()
        }
        return defaultFolder
    }

    /**
     * 获取所有文件夹
     */
    fun getAllFolders(): List<File> {
        val rootDir = getAppRootDirectory()
        val folders = mutableListOf<File>()
        
        // 确保默认文件夹存在
        folders.add(getDefaultFolder())
        
        // 获取其他文件夹
        rootDir.listFiles()?.forEach { file ->
            if (file.isDirectory && file.name != DEFAULT_FOLDER) {
                folders.add(file)
            }
        }
        
        return folders.sortedBy { it.name }
    }

    /**
     * 创建新文件夹
     */
    fun createFolder(folderName: String): Boolean {
        if (folderName.isBlank() || folderName.contains("/") || folderName.contains("\\")) {
            return false
        }
        
        val newFolder = File(getAppRootDirectory(), folderName)
        return if (!newFolder.exists()) {
            newFolder.mkdirs()
        } else {
            false
        }
    }

    /**
     * 获取指定文件夹中的所有照片
     */
    fun getPhotosInFolder(folder: File): List<Photo> {
        val photos = mutableListOf<Photo>()
        
        folder.listFiles()?.forEach { file ->
            if (file.isFile && isImageFile(file)) {
                photos.add(
                    Photo(
                        file = file,
                        name = file.name,
                        path = file.absolutePath,
                        folderName = folder.name,
                        dateModified = file.lastModified()
                    )
                )
            }
        }
        
        return photos.sortedByDescending { it.dateModified }
    }

    /**
     * 获取所有照片
     */
    fun getAllPhotos(): List<Photo> {
        val allPhotos = mutableListOf<Photo>()
        getAllFolders().forEach { folder ->
            allPhotos.addAll(getPhotosInFolder(folder))
        }
        return allPhotos.sortedByDescending { it.dateModified }
    }

    /**
     * 生成新的照片文件名
     */
    fun generatePhotoFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "IMG_$timestamp.jpg"
    }

    /**
     * 创建照片文件
     */
    fun createPhotoFile(folder: File): File {
        return File(folder, generatePhotoFileName())
    }

    /**
     * 删除照片
     */
    fun deletePhoto(photo: Photo): Boolean {
        return photo.file.delete()
    }

    /**
     * 移动照片到另一个文件夹
     */
    fun movePhoto(photo: Photo, targetFolder: File): Boolean {
        val targetFile = File(targetFolder, photo.name)
        return try {
            photo.file.copyTo(targetFile, overwrite = false)
            photo.file.delete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 检查是否为图片文件
     */
    private fun isImageFile(file: File): Boolean {
        val extension = file.extension.lowercase()
        return extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    }
}
