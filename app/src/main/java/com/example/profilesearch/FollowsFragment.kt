package com.example.profilesearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.profilesearch.databinding.FragmentFollowsBinding
import com.example.profilesearch.model.FollowersViewModel
import com.example.profilesearch.model.FollowingViewModel
import com.google.android.material.snackbar.Snackbar

class FollowsFragment : Fragment() {

    private var _position: Int? = null
    private var _username: String? = null
    private var _binding: FragmentFollowsBinding? = null
    private val binding get() = _binding
    private val followersViewModel by viewModels<FollowersViewModel>()
    private val followingViewModel by viewModels<FollowingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            _position = it.getInt(ARG_POSITION)
            _username = it.getString(ARG_USERNAME)
        }
        val adapter = UserAdapter()

        followersViewModel.snackbarText.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(binding?.root?.rootView!!, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }

        followingViewModel.snackbarText.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { snackBarText ->
                Snackbar.make(binding?.root?.rootView!!, snackBarText, Snackbar.LENGTH_SHORT).show()
            }
        }

        setupRecyclerView()

        if (_position == 1) {
            showFollowersData(adapter)
        } else {
            showFollowingData(adapter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding?.recyclerViewFollows?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding?.recyclerViewFollows?.addItemDecoration(itemDecoration)
    }

    private fun showFollowersData(adapter: UserAdapter) {
        with(followersViewModel) {
            if (followers.value == null) {
                loadFollowers(_username!!)
            }
            followers.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding?.recyclerViewFollows?.adapter = adapter
            }
            isLoading.observe(viewLifecycleOwner) {
                showLoader(it)
            }
        }
    }

    private fun showFollowingData(adapter: UserAdapter) {
        with(followingViewModel) {
            loadFollowing(_username!!)
            following.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding?.recyclerViewFollows?.adapter = adapter
            }
            isLoading.observe(viewLifecycleOwner) {
                showLoader(it)
            }
        }
    }

    private fun showLoader(isLoadings: Boolean) {
        if (isLoadings) {
            binding?.progressBarFollows?.visibility = View.VISIBLE
            binding?.recyclerViewFollows?.visibility = View.INVISIBLE
        } else {
            binding?.progressBarFollows?.visibility = View.INVISIBLE
            binding?.recyclerViewFollows?.visibility = View.VISIBLE
        }
    }

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"
    }
}