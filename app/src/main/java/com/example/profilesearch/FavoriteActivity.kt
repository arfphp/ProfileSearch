package com.example.profilesearch

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.profilesearch.client.ProfileResponseItem
import com.example.profilesearch.databinding.ActivityMainBinding
import com.example.profilesearch.model.FavoriteUserViewModel
import com.example.profilesearch.model.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: FavoriteUserViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        supportActionBar?.title = "Favorite Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = UserAdapter()
        binding.recyclerView.adapter = adapter

        viewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.getAllFavUser().observe(this) { users ->
            val items = arrayListOf<ProfileResponseItem>()
            users.map {
                val item = ProfileResponseItem(login = it.username, avatarUrl = it.avatarUrl)
                items.add(item)
            }
            viewModel.isLoading.observe(this, Observer { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            })
            adapter.submitList(items)
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
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.recyclerView.addItemDecoration(itemDecoration)
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

    private fun setListUser(users: List<ProfileResponseItem>) {
        val adapter = UserAdapter()
        adapter.submitList(users)
        binding.recyclerView.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}