package com.example.profilesearch.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.profilesearch.client.ApiConfig
import com.example.profilesearch.client.ProfileResponse
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.db.FavoriteUserEntity
import com.example.profilesearch.db.FavoriteUserRepository
import com.example.profilesearch.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteUserViewModel(application: Application) : ViewModel() {
    private val mFavUserRepository: FavoriteUserRepository = FavoriteUserRepository(application)

    fun getAllFavUser(): LiveData<List<FavoriteUserEntity>> = mFavUserRepository.getAllFav()

    private val _users = MutableLiveData<List<ProfileResponseItem>?>()
    val users: LiveData<List<ProfileResponseItem>?> = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    init {
        searchUserByUsername()
    }

    fun searchUserByUsername(username: String = "a") {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getSearchUser(username)
        client.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _users.value = response.body()?.items
                } else {
                    _snackbarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }

        })
    }
}