package com.example.photoclassifier

import java.io.File

/**
 * 照片数据模型
 */
data class Photo(
    val file: File,
    val name: String,
    val path: String,
    val folderName: String,
    val dateModified: Long
)
