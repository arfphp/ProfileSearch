package com.example.profilesearch.helper

import androidx.recyclerview.widget.DiffUtil
import com.example.profilesearch.db.FavoriteUserEntity

class FavUserDiffCallback(private val oldFavUserList: List<FavoriteUserEntity>, private val newFavUserList: List<FavoriteUserEntity>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldFavUserList.size
    override fun getNewListSize(): Int = newFavUserList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFavUserList[oldItemPosition].username == newFavUserList[newItemPosition].username
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldFavUserList[oldItemPosition]
        val newNote = newFavUserList[newItemPosition]
        return oldNote.username == newNote.username && oldNote.avatarUrl == newNote.avatarUrl
    }
}