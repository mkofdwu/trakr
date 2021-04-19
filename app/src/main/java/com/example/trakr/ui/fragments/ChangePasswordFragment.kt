package com.example.trakr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentChangePasswordBinding
import com.example.trakr.models.User
import com.example.trakr.utils.Validator
import com.example.trakr.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, null, false)
        binding.fragment = this
        return binding.root
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun confirm() {
        val currentPassword = binding.currentPasswordField.text.toString()
        val newPassword = binding.newPasswordField.text.toString()
        val confirmNewPassword = binding.confirmNewPasswordField.text.toString()
        val errors = Validator.validatePasswordConfirmPassword(
            newPassword,
            confirmNewPassword
        )
        if (errors.isNotEmpty()) {
            for (error in errors) {
                when (error.field) {
                    Validator.Field.PASSWORD -> binding.newPasswordField.error = error.message
                    Validator.Field.CONFIRM_PASSWORD -> binding.confirmNewPasswordField.error =
                        error.message
                    else -> throw Error("invalid error field: ${error.field}")
                }
            }
        } else {
            startLoading()
            userViewModel.changePassword(
                currentPassword,
                newPassword,
                object : UserViewModel.AuthListener {
                    override fun onAuthenticated(user: User) {
                        stopLoading()
                        findNavController().navigateUp()
                        Snackbar.make(
                            requireView(),
                            "Your password has been successfully changed",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    override fun onException(exception: Exception?) {
                        stopLoading()
                        binding.currentPasswordField.error = "Invalid password"
                    }
                })
        }
    }

    private fun startLoading() {
        binding.doneBtn.visibility = View.INVISIBLE
        binding.loadingIndicatorContainer.visibility = View.VISIBLE
    }

    private fun stopLoading() {
        binding.doneBtn.visibility = View.VISIBLE
        binding.loadingIndicatorContainer.visibility = View.INVISIBLE
    }
}