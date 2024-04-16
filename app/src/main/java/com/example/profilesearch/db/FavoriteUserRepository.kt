package com.example.profilesearch.db

import android.app.Application
import androidx.lifecycle.LiveData
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class FavoriteUserRepository(application: Application) {
    private val mFavrotieUserDao: FavoriteUserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = FavoriteUserRoomDatabase.getDatabase(application)
        mFavrotieUserDao = db.favoriteUserDao()
    }

    fun getAllFav(): LiveData<List<FavoriteUserEntity>> = mFavrotieUserDao.getAllFav()

    fun insert(favUser: FavoriteUserEntity) {
        executorService.execute { mFavrotieUserDao.insert(favUser) }
    }
    fun delete(favUser: FavoriteUserEntity) {
        executorService.execute { mFavrotieUserDao.delete(favUser) }
    }
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUserEntity?> {
        return mFavrotieUserDao.getFavoriteUserByUsername(username)
    }
}