package com.example.trakr.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.adapters.ColorsRecyclerViewAdapter
import com.example.trakr.databinding.FragmentColorsSettingsBinding
import com.example.trakr.ui.misc.GridSpacingItemDecoration
import com.example.trakr.ui.misc.UnscrollableGridLayoutManager
import com.example.trakr.viewmodels.UserViewModel
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerPopup.ColorPickerObserver


class ColorsSettingsFragment : Fragment() {
    private lateinit var binding: FragmentColorsSettingsBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_colors_settings, null, false)
        binding.fragment = this
        binding.colorsList.apply {
            layoutManager = UnscrollableGridLayoutManager(context, 3)
            adapter = ColorsRecyclerViewAdapter(
                userViewModel.getCurrentUser().colors,
                this@ColorsSettingsFragment
            )
            addItemDecoration(GridSpacingItemDecoration(3, 64, false))
        }
        return binding.root
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun addColor() {
        ColorPickerPopup.Builder(context)
            .enableBrightness(true)
            .okTitle("Choose")
            .cancelTitle("Cancel")
            .showIndicator(true)
            .showValue(true)
            .build()
            .show(requireView(), object : ColorPickerObserver() {
                override fun onColorPicked(color: Int) {
                    val currentUser = userViewModel.getCurrentUser()
                    currentUser.colors.add(color)
                    userViewModel.updateUser("colors", currentUser.colors)
                    binding.colorsList.adapter!!.notifyDataSetChanged()
                }
            })
    }

    fun removeColor(index: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Remove this color?")
        builder.setPositiveButton("Ok") { _, _ ->
            val currentUser = userViewModel.getCurrentUser()
            currentUser.colors.removeAt(index)
            userViewModel.updateUser("colors", currentUser.colors)
            binding.colorsList.adapter!!.notifyDataSetChanged()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }
}