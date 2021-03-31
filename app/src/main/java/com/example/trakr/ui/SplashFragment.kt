package com.example.trakr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.viewmodels.AuthViewModel

class SplashFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (authViewModel.isLoggedIn) {
            authViewModel.refreshCurrentUser(object : AuthViewModel.CompleteListener {
                override fun onLoggedIn() {
                    view.findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
                override fun onException(exception: Exception?) {
                }
            })
        } else {
            view.findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }
}