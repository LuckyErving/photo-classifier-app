package com.example.photoclassifier

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.photoclassifier.databinding.ActivityPhotoViewBinding
import java.io.File

/**
 * 照片预览Activity
 * 全屏查看照片
 */
class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding

    companion object {
        const val EXTRA_PHOTO_PATH = "photo_path"
        const val EXTRA_PHOTO_NAME = "photo_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取照片路径
        val photoPath = intent.getStringExtra(EXTRA_PHOTO_PATH)
        val photoName = intent.getStringExtra(EXTRA_PHOTO_NAME)

        // 设置标题
        binding.tvPhotoName.text = photoName ?: "照片"

        // 加载照片
        if (photoPath != null) {
            Glide.with(this)
                .load(File(photoPath))
                .into(binding.photoView)
        }

        // 关闭按钮
        binding.btnClose.setOnClickListener {
            finish()
        }

        // 点击照片关闭
        binding.photoView.setOnClickListener {
            finish()
        }
    }
}
