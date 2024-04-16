package com.example.profilesearch

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.databinding.UserItemBinding

class UserAdapter : ListAdapter<ProfileResponseItem, UserAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: UserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: ProfileResponseItem) {
            val bundle = Bundle()
            bundle.putString("username", user.login)

            Glide.with(binding.root)
                .load(user.avatarUrl)
                .placeholder(ColorDrawable(Color.LTGRAY))
                .circleCrop()
                .into(binding.avatarImageView)
            binding.usernameTextView.text = user.login
            binding.root.setOnClickListener { view ->
                Intent(view.context, UserDetailActivity::class.java).apply {
                    putExtras(bundle)
                    view.context.startActivity(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfileResponseItem>() {
            override fun areItemsTheSame(oldItem: ProfileResponseItem, newItem: ProfileResponseItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ProfileResponseItem, newItem: ProfileResponseItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}