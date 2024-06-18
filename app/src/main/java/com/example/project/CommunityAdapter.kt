package com.example.project

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.databinding.CommunityItemsBinding

class PostDiffUtil: DiffUtil.ItemCallback<CommunityEntity>(){
    override fun areItemsTheSame(oldItem: CommunityEntity, newItem: CommunityEntity): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: CommunityEntity, newItem: CommunityEntity): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.name == newItem.name
    }
}

class CommunityAdapter(
    var onClickListener:((CommunityEntity)->Unit)? = null,
): ListAdapter<CommunityEntity, CommunityAdapter.ViewHolder>(PostDiffUtil()) {
    class ViewHolder(val binding: CommunityItemsBinding): RecyclerView.ViewHolder(binding.root){
        val communityName: TextView = binding.lblCommunityName
        val btnJoin: Button = binding.btnJoin
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:CommunityItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.community_items,
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community = getItem(position)
        holder.communityName.text = community.name

        holder.btnJoin.setOnClickListener {

        }
    }
}