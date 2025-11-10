package com.example.photoclassifier

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photoclassifier.databinding.ItemPhotoBinding

/**
 * 照片列表适配器
 */
class PhotoAdapter(
    private var photos: List<Photo>,
    private val onDeleteClick: (Photo) -> Unit,
    private val onPhotoClick: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            binding.tvFileName.text = photo.name

            // 使用Glide加载图片
            Glide.with(binding.root.context)
                .load(photo.file)
                .centerCrop()
                .into(binding.ivPhoto)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(photo)
            }

            // 点击图片预览
            binding.ivPhoto.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    /**
     * 更新照片列表
     */
    fun updatePhotos(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
