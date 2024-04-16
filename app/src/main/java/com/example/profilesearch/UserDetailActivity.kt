package com.example.profilesearch

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.profilesearch.client.DetailUserResponse
import com.example.profilesearch.databinding.ActivityUserDetailBinding
import com.example.profilesearch.db.FavoriteUserEntity
import com.example.profilesearch.model.UserDetailViewModel
import com.example.profilesearch.model.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private val viewModel by viewModels<UserDetailViewModel>(){
        ViewModelFactory.getInstance(application)
    }

    private var savedDetailUsername: String? = null
    private var savedDetailUrl: String? = null

    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val uname = intent.extras?.getString("username")

        if (viewModel.userDetails.value == null) {
            viewModel.getUserDetail(uname!!)
        }

        viewModel.userDetails.observe(this) {
            setUserDetail(it!!)
        }

        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.snackbarText.observe(this) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(window.decorView.rootView, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }

        val viewPagerAdapter = ViewPagerAdapter(this, uname!!)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = viewPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabLayout)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        val unames = intent.extras?.getString("username")
        viewModel.getFavoriteUserByUsername(unames ?: "").observe(this) { favoriteUser ->
            isFavorite = favoriteUser != null
            updateFavoriteIcon()
        }

        binding.fab.setOnClickListener {
            val favoriteUser = savedDetailUsername?.let { username ->
                savedDetailUrl?.let { avatarUrl ->
                    FavoriteUserEntity(username = username.substring(1), avatarUrl = avatarUrl)
                }
            }
            favoriteUser?.let { user ->
                if (isFavorite) {
                    viewModel.deleteFavoriteUser(user)
                    viewModel.insertResult.observe(this) { isSuccess ->
                        if (isSuccess) {
                            Snackbar.make(binding.root, "User deleted from favorites", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, "Failed to delete user from favorites", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    viewModel.insertFavoriteUser(user)
                    viewModel.insertResult.observe(this) { isSuccess ->
                        if (isSuccess) {
                            Snackbar.make(binding.root, "User added to favorites", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(binding.root, "Failed to add user to favorites", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            } ?: run {
                Snackbar.make(binding.root, "User data is incomplete", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFavoriteIcon() {
        binding.fab.setImageResource(
            if (isFavorite) R.drawable.baseline_favorite_24 else R.drawable.baseline_favorite_border_24
        )
    }

    private fun setUserDetail(user: DetailUserResponse) {
        val detailUrl = "${user.avatarUrl}"
        val detailUsername = "@${user.login}"
        with(binding) {
            Glide.with(root.context)
                .load(user.avatarUrl)
                .circleCrop()
                .into(avatarImageView)
            nameTextView.text = user.name ?: "-"
            usernameTextView.text = detailUsername
            followersTextView.text = getString(R.string._0_followers, user.followers)
            followingTextView.text = getString(R.string._0_following, user.following)
        }
        savedDetailUsername = detailUsername
        savedDetailUrl = detailUrl
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.followers_tab,
            R.string.following_tab
        )

        const val EXTRA_favUser = "extra_favUser"
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}