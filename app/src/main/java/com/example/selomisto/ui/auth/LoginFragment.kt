package com.example.selomisto.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.selomisto.R
import com.example.selomisto.databinding.FragmentLoginBinding
import com.example.selomisto.models.AuthRequest
import com.example.selomisto.utils.NetworkResult
import com.example.selomisto.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.btnLogin.setOnClickListener {
            val validationResult = validateLoginInputs()
            if (validationResult.first) {
                authViewModel.login(getLoginData())

            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()
    }

    private fun bindObservers() {
        authViewModel.tokenLiveData.observe(viewLifecycleOwner, Observer {
//            binding.progressBar.isVisible = false TODO:
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_chooseLocationFragment)
                }
                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }
                is NetworkResult.Loading ->{
//                    binding.progressBar.isVisible = true TODO:
                }
            }
        })
    }

    private fun showValidationErrors(error: String) {
        binding.txtError.text = String.format(resources.getString(R.string.txt_error_message, error))
    }

    private fun validateLoginInputs(): Pair<Boolean, String> {
        val authRequest = getLoginData()
        return authViewModel.validateCredentials("", "", authRequest.email, authRequest.password, true)
    }

    private fun getLoginData(): AuthRequest {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return AuthRequest(email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}