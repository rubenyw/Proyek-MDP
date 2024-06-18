package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.databinding.PostItemsBinding
class PostDiffUtil: DiffUtil.ItemCallback<PostEntity>(){
    override fun areItemsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: PostEntity, newItem: PostEntity): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.owner == newItem.owner &&
                oldItem.date == newItem.date &&
                oldItem.context == newItem.context
    }
}

class CommunityPostAdapter(): ListAdapter<PostEntity, CommunityPostAdapter.ViewHolder>(PostDiffUtil()) {
    class ViewHolder(val binding: PostItemsBinding): RecyclerView.ViewHolder(binding.root){
        val lblOwner: TextView = binding.lblOwner
        val lblDate: TextView = binding.lblDate
        val tvContext: TextView = binding.tvContext
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:PostItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.post_items,
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)
        holder.lblOwner.text = post.owner
        holder.lblDate.text = post.date
        holder.tvContext.text = post.context

    }
}