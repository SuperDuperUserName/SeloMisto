package com.example.selomisto.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.selomisto.MainActivity
import com.example.selomisto.R
import com.example.selomisto.databinding.FragmentRegisterBinding
import com.example.selomisto.models.RegisterRequest
import com.example.selomisto.utils.Constants.TAG
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
import kotlin.math.acos

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
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
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
        gsc = GoogleSignIn.getClient(authActivity, gso)

        if (tokenManager.getToken() != null) {
            navigateToMainPage()
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
                authViewModel.loginWithGoogle(getRegisterDataFromGoogleAuth(account))
            } catch (e: ApiException) {
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindObservers() {
        authViewModel.tokenLiveData.observe(viewLifecycleOwner, Observer {
//            binding.progressBar.isVisible = false TODO:
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    userDataManager.setUser(it.data.user)
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

    private fun navigateToMainPage() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
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

    private fun getRegisterDataFromGoogleAuth(account: GoogleSignInAccount): RegisterRequest {
        val password = ""
        val firstname = account.givenName.toString()
        val lastname = account.familyName.toString()
        val email = account.email.toString()
        return RegisterRequest(firstname, lastname, email, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}