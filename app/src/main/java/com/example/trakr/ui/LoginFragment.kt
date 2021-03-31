package com.example.trakr.ui

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentLoginBinding
import com.example.trakr.validators.Field
import com.example.trakr.validators.UsernamePasswordValidator
import com.example.trakr.viewmodels.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, null, false)
        binding.fragment = this
        return binding.root
    }

    fun goToRegister() {
        requireView().findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun done() {
        val username = binding.usernameField.text.toString()
        val password = binding.passwordField.text.toString()
        val errors = UsernamePasswordValidator.validateUsernamePassword(username, password)
        if (errors.isNotEmpty()) {
            for (error in errors) {
                when (error.field) {
                    Field.USERNAME -> binding.usernameField.error = error.message
                    Field.PASSWORD -> binding.passwordField.error = error.message
                    else -> throw Error("invalid field: " + error.field)
                }
            }
        } else {
            authViewModel.login(username, password, object : AuthViewModel.CompleteListener {
                override fun onLoggedIn() {
                    requireView().findNavController()
                        .navigate(R.id.action_loginFragment_to_homeFragment)
                }

                override fun onException(exception: Exception?) {
                    AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setMessage(exception!!.message)
                        .setPositiveButton("Close") { _, _ -> }
                        .show()
                }
            })
        }
    }
}