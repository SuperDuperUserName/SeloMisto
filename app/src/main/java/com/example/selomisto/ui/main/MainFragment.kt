package com.example.selomisto.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.selomisto.AuthorizationActivity
import com.example.selomisto.MainActivity
import com.example.selomisto.R
import com.example.selomisto.databinding.FragmentMainBinding
import com.example.selomisto.utils.Constants.TAG
import com.example.selomisto.utils.NetworkResult
import com.example.selomisto.utils.TokenManager
import com.example.selomisto.utils.UserDataManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var userDataManager: UserDataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

//        mainViewModel.getUserById(1)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogout.setOnClickListener {
            tokenManager.clearToken()
            val intent = Intent(context, AuthorizationActivity::class.java)
            startActivity(intent)
        }

        val user = userDataManager.getUser()
        Log.d(TAG, Gson().toJson(user))

        binding.greetings.text = "Hello " + user!!.firstname + " " + user!!.lastname

        bindUserObserver()
    }

    private fun bindUserObserver() {
        mainViewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkResult.Success -> {
                    binding.greetings.text = "Hello " + it.data!!.firstname + " " + it.data!!.lastname
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, it.message.toString())
                }
                is NetworkResult.Loading ->{
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}