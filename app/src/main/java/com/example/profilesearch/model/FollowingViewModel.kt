package com.example.profilesearch.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.profilesearch.client.ApiConfig
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingViewModel() : ViewModel() {

    private val _following = MutableLiveData<List<ProfileResponseItem>>()
    val following: LiveData<List<ProfileResponseItem>> = _following

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun loadFollowing(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserFollowing(username)
        client.enqueue(object : Callback<List<ProfileResponseItem>> {
            override fun onResponse(
                call: Call<List<ProfileResponseItem>>,
                response: Response<List<ProfileResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _following.value = response.body()
                    Log.e("FollowingViewModel", "Berhasil Cuy")
                } else {
                    _snackbarText.value = Event(response.message())
                    Log.e("FollowingViewModel", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ProfileResponseItem>>, e: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(e.message.toString())
                Log.e("FollowingViewModel", "onFailure: ${e.message}")
            }

        })
    }
}
