package com.example.profilesearch.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.profilesearch.client.ApiConfig
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersViewModel() : ViewModel() {

    private val _followers = MutableLiveData<List<ProfileResponseItem>>()
    val followers: LiveData<List<ProfileResponseItem>> = _followers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<Event<String>>()
    val snackbarText: LiveData<Event<String>> = _snackbarText

    fun loadFollowers(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUserFollowers(username)
        client.enqueue(object : Callback<List<ProfileResponseItem>> {
            override fun onResponse(
                call: Call<List<ProfileResponseItem>>,
                response: Response<List<ProfileResponseItem>>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _followers.value = response.body()
                } else {
                    _snackbarText.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<List<ProfileResponseItem>>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = Event(t.message.toString())
            }

        })
    }
}