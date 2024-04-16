package com.example.profilesearch.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.profilesearch.client.ApiConfig
import com.example.profilesearch.client.ProfileResponse
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.setPref.SettingPreferences
import com.example.profilesearch.util.Event
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: SettingPreferences) : ViewModel() {

    // This LiveData will emit the current theme setting.
    val themeSettings: LiveData<Boolean> = pref.isDarkMode.asLiveData()
    private val _isDarkModeActive = MutableLiveData<Boolean>()
    val isDarkModeActive: LiveData<Boolean> get() = _isDarkModeActive

    init {
        // Initialize the LiveData with the current setting from SettingPreferences.
        viewModelScope.launch {
            pref.isDarkMode.collect { isDarkMode ->
                _isDarkModeActive.postValue(isDarkMode)
            }
        }
    }

    fun toggleThemeSetting() {
        viewModelScope.launch {
            // Toggle the theme setting in SettingPreferences.
            pref.toggleDarkMode()
            // After toggling, post the new value to LiveData to notify observers.
            pref.isDarkMode.collect { isDarkMode ->
                _isDarkModeActive.postValue(isDarkMode)
            }
        }
    }

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