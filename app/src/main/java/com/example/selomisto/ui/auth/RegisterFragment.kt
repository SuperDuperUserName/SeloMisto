package com.example.selomisto.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.selomisto.MainActivity
import com.example.selomisto.R
import com.example.selomisto.databinding.FragmentRegisterBinding
import com.example.selomisto.models.AuthRequest
import com.example.selomisto.models.RegisterRequest
import com.example.selomisto.utils.NetworkResult
import com.example.selomisto.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if (tokenManager.getToken() != null) {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener{
            val validationResult = validateRegisterInput()
            if (validationResult.first) {
                authViewModel.register(getRegisterData())
            } else {
                binding.txtError.text = validationResult.second
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

    private fun validateRegisterInput(): Pair<Boolean, String> {
        val registerData = getRegisterData()
        return authViewModel.validateCredentials(
            registerData.firstname,
            registerData.lastname,
            registerData.email,
            registerData.password,
            false
        )
    }

    private fun getRegisterData(): RegisterRequest {
        val password = binding.txtPassword.text.toString()
        val firstname = binding.txtFirstname.text.toString()
        val lastname = binding.txtLastname.text.toString()
        val email = binding.txtEmail.text.toString()
        return RegisterRequest(firstname, lastname, email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}