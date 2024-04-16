package com.example.profilesearch

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.profilesearch.client.ApiService
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.databinding.ActivityMainBinding
import com.example.profilesearch.model.MainViewModel
import com.example.profilesearch.model.MainViewModelFactory
import com.example.profilesearch.setPref.SettingPreferences
import com.example.profilesearch.setPref.dataStore
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var apiService: ApiService
    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(SettingPreferences.getInstance(application.dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Home"

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainviewModels = ViewModelProvider(this, MainViewModelFactory(pref)).get(
            MainViewModel::class.java
        )
        mainviewModels.themeSettings.observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        setupRecyclerView()

        viewModel.users.observe(this) { listUser ->
            if (listUser != null) {
                setListUser(listUser)
            }
        }

        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText?.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = textView.text?.toString() ?: ""
                    searchBar.setText(query)
                    searchView.hide()
                    if (query.isNullOrEmpty()) {
                        setupRecyclerView()
                    } else {
                        searchAction()
                    }
                    true
                } else {
                    false
                }
            }
        }

        searchAction()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerView.addItemDecoration(itemDecoration)
    }

    private fun setListUser(users: List<ProfileResponseItem>) {
        val adapter = UserAdapter()
        adapter.submitList(users)
        binding.recyclerView.adapter = adapter
    }

    private fun searchAction() {
        with(binding) {
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val usernameQuery =
                    if (searchView.text.isEmpty()) "a" else searchView.text.toString()
                searchView.hide()
                searchBar.setText(searchView.text)
                viewModel.searchUserByUsername(usernameQuery)
                false
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_fav -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_mode -> {
                viewModel.toggleThemeSetting()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val darkModeItem = menu?.findItem(R.id.action_mode)
        val darkModeItemFav = menu?.findItem(R.id.action_fav)
        val isDarkModeActive = viewModel.isDarkModeActive.value ?: false
        if (isDarkModeActive) {
            darkModeItem?.icon = ContextCompat.getDrawable(this, R.drawable.baseline_light_mode_24)
            darkModeItemFav?.icon = ContextCompat.getDrawable(this, R.drawable.baseline_favorite_white_24)
        } else {
            darkModeItem?.icon = ContextCompat.getDrawable(this, R.drawable.baseline_dark_mode_24)
            darkModeItemFav?.icon = ContextCompat.getDrawable(this, R.drawable.baseline_favorite_24)
        }
        return super.onPrepareOptionsMenu(menu)
    }
}