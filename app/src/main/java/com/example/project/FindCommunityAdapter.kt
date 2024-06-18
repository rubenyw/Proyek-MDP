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
import com.example.project.databinding.FindCommunitiesItemsBinding

class FindCommunityAdapter(
    var onClickListener:((String)->Unit)? = null,
): ListAdapter<CommunityEntity, FindCommunityAdapter.ViewHolder>(CommunityDiffUtil()) {
    class ViewHolder(val binding: FindCommunitiesItemsBinding): RecyclerView.ViewHolder(binding.root){
        val communityName: TextView = binding.lblFindCommunityName
        val btnJoin: Button = binding.btnJoin
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding:FindCommunitiesItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.find_communities_items,
            parent,
            false
        )
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val community = getItem(position)
        holder.communityName.text = community.name

        holder.btnJoin.setOnClickListener {
            onClickListener?.invoke(community.id)
        }
    }
}