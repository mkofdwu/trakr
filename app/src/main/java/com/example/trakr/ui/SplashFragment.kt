package com.example.trakr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.models.User
import com.example.trakr.viewmodels.DbViewModel
import com.example.trakr.viewmodels.UserViewModel

class SplashFragment : Fragment() {
    private val userViewModel: UserViewModel by activityViewModels()
    private val dbViewModel: DbViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userViewModel.isLoggedIn) {
            userViewModel.refreshCurrentUser(object : UserViewModel.AuthListener {
                override fun onAuthenticated(user: User) {
                    dbViewModel.setUserId(user.id)
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