package com.example.profilesearch.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavoriteUserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favUser: FavoriteUserEntity)

    @Update
    fun update(favUser: FavoriteUserEntity)

    @Delete
    fun delete(favUser: FavoriteUserEntity)

    @Query("SELECT * from favoriteuserentity ORDER BY username ASC")
    fun getAllFav(): LiveData<List<FavoriteUserEntity>>

    @Query("SELECT * FROM favoriteuserentity WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUserEntity?>
}