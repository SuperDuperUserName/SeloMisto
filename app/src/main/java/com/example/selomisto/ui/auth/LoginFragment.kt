package com.example.selomisto.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.selomisto.R
import com.example.selomisto.databinding.FragmentLoginBinding
import com.example.selomisto.models.AuthRequest
import com.example.selomisto.models.RegisterRequest
import com.example.selomisto.utils.NetworkResult
import com.example.selomisto.utils.TokenManager
import com.example.selomisto.utils.UserDataManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.sign

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient
    lateinit var authActivity: Activity

    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var userDataManager: UserDataManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authActivity = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(authActivity, gso)

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

        binding.btnGoogle.setOnClickListener {
            signIn()
        }

        bindObservers()
    }

    private fun signIn() {
        val signInIntent: Intent = gsc.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                authViewModel.loginWithGoogle(getAuthDataFormGoogle(account))
            } catch (e: ApiException) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAuthDataFormGoogle(account: GoogleSignInAccount): RegisterRequest {
        val password = ""
        val firstname = account.givenName.toString()
        val lastname = account.familyName.toString()
        val email = account.email.toString()
        return RegisterRequest(firstname, lastname, email, password)
    }

    private fun bindObservers() {
        authViewModel.tokenLiveData.observe(viewLifecycleOwner, Observer {
//            binding.progressBar.isVisible = false TODO:
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    userDataManager.setUser(it.data.user)
                    findNavController().navigate(R.id.action_loginFragment_to_chooseLocationFragment)
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