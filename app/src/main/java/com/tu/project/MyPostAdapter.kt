package com.tu.project

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tu.project.databinding.ItemMyPostBinding

class MyPostAdapter(private val postList: List<MyPost>) :
    RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder>() {

    inner class MyPostViewHolder(private val binding: ItemMyPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: MyPost) {
            binding.tvTitle.text = post.title
            binding.tvLikeCount.text = "❤️ ${post.likeCount}"
            binding.tvDate.text = post.updatedAt.substring(0, 10) // yyyy-MM-dd
            Glide.with(binding.root.context)
                .load(post.imageUrl)
                .into(binding.ivThumbnail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyPostBinding.inflate(inflater, parent, false)
        return MyPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyPostViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount() = postList.size
}