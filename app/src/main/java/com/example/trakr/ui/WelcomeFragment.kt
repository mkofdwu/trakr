package com.example.trakr.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            getStartedBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
            }
            loginBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
            aboutBtn.setOnClickListener {
                it.findNavController().navigate(R.id.action_welcomeFragment_to_aboutFragment)
            }
        }
    }

}