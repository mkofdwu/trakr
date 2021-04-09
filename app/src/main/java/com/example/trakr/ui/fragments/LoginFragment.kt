package com.example.trakr.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentLoginBinding
import com.example.trakr.models.User
import com.example.trakr.validators.Field
import com.example.trakr.validators.UsernamePasswordValidator
import com.example.trakr.viewmodels.UserViewModel
import com.example.trakr.viewmodels.DbViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, null, false)
        binding.fragment = this
        return binding.root
    }

    fun goToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    fun done() {
        val username = binding.usernameField.text.toString()
        val password = binding.passwordField.text.toString()
        userViewModel.login(username, password, object : UserViewModel.AuthListener {
            override fun onAuthenticated(user: User) {
                dbViewModel.setUserId(user.id)
                findNavController()
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