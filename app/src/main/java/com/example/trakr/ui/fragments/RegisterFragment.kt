package com.example.trakr.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentRegisterBinding
import com.example.trakr.models.User
import com.example.trakr.utils.Validator
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, null, false)
        binding.fragment = this
        return binding.root
    }

    fun goToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    fun done() {
        val username = binding.usernameField.text.toString()
        val password = binding.passwordField.text.toString()
        val confirmPassword = binding.confirmPasswordField.text.toString()
        val errors = mutableListOf<Validator.Error>()
        val usernameError = Validator.validateUsername(username)
        if (usernameError != null) errors.add(usernameError)
        errors.addAll(
            Validator.validatePasswordConfirmPassword(
                password,
                confirmPassword
            )
        )
        if (errors.isNotEmpty()) {
            for (error in errors) {
                when (error.field) {
                    Validator.Field.USERNAME -> binding.usernameField.error = error.message
                    Validator.Field.PASSWORD -> binding.passwordField.error = error.message
                    Validator.Field.CONFIRM_PASSWORD -> binding.confirmPasswordField.error = error.message
                }
            }
        } else {
            userViewModel.register(username, password, object : UserViewModel.AuthListener {
                override fun onAuthenticated(user: User) {
                    // FIXME: use a repository to store current user in the future
                    dbViewModel.setUserId(user.id)
                    findNavController()
                        .navigate(R.id.action_registerFragment_to_homeFragment)
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