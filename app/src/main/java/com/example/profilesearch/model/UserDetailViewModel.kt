package com.example.profilesearch.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.profilesearch.client.ApiConfig
import com.example.profilesearch.client.DetailUserResponse
import com.example.profilesearch.db.FavoriteUserDao
import com.example.profilesearch.db.FavoriteUserEntity
import com.example.profilesearch.db.FavoriteUserRepository
import com.example.profilesearch.db.FavoriteUserRoomDatabase
import com.example.profilesearch.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailViewModel(application: Application) : ViewModel() {

    private val _userDetails = MutableLiveData<DetailUserResponse>()
    val userDetails: LiveData<DetailUserResponse> = _userDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    private val mFavUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    private val favoriteUserDao: FavoriteUserDao =
        FavoriteUserRoomDatabase.getDatabase(application).favoriteUserDao()

    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult: LiveData<Boolean> = _insertResult

    fun insertFavoriteUser(favUser: FavoriteUserEntity) {
        mFavUserRepository.insert(favUser)
        _insertResult.value = true
    }
    fun deleteFavoriteUser(favUser: FavoriteUserEntity) {
        mFavUserRepository.delete(favUser)
    }

    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUserEntity?> {
        return favoriteUserDao.getFavoriteUserByUsername(username)
    }

    fun getUserDetail(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserDetail(username)
        client.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _userDetails.value = response.body()
                    Log.d("UserDetails", "User Detail loaded successfully Cuy")
                }else {
                    _snackbarText.value = Event(response.message())
                    Log.e("UserDetails", "Error loading User Detail: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
                Log.e("UserDetails", "onFailure: ${t.message}")
            }

        })
    }
}