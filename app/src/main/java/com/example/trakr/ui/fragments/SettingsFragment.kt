package com.example.trakr.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.trakr.R
import com.example.trakr.databinding.FragmentSettingsBinding
import com.example.trakr.utils.Validator
import com.example.trakr.utils.loadPhotoURLToImageView
import com.example.trakr.viewmodels.StorageViewModel
import com.example.trakr.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.*


class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val storageViewModel: StorageViewModel by activityViewModels()
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, null, false)
        binding.fragment = this
        binding.user = userViewModel.getCurrentUser()
        prefs = requireActivity().getSharedPreferences("AppPrefs", 0)
        binding.darkMode = prefs.getBoolean("darkMode", false)
        refreshPhoto()
        return binding.root
    }

    fun back() {
        findNavController().navigateUp()
    }

    fun selectNewPhoto() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change photo")
        builder.setItems(
            arrayOf(
                "Capture photo now",
                "Select from gallery",
                "Remove photo"
            )
        ) { _, item ->
            when (item) {
                0 -> {
                    val capturePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(capturePhoto, 0)
                }
                1 -> {
                    val selectPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(selectPhoto, 1)
                }
                2 -> {
                    userViewModel.getCurrentUser().photoURL = null
                    userViewModel.updateUser("photoURL", null)
                    binding.invalidateAll()
                }
            }
        }
        builder.show()
    }

    fun changeUsername() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change username")
        val textField = EditText(requireContext())
        textField.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(textField)
        builder.setPositiveButton("Ok") { _, _ ->
            val newUsername = textField.text.toString()
            val error = Validator.validateUsername(newUsername)
            if (error == null) {
                userViewModel.getCurrentUser().username = newUsername
                userViewModel.updateUser("username", newUsername)
                userViewModel.changeUsername(newUsername) // change in fbAuth
                binding.invalidateAll()
            } else {
                Snackbar.make(requireView(), error.message, Snackbar.LENGTH_LONG).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    fun toggleDarkMode() {
        val prefsEdit = prefs.edit()
        val darkMode = !prefs.getBoolean("darkMode", false)
        prefsEdit.putBoolean("darkMode", darkMode)
        prefsEdit.apply()
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun goToColors() {
        findNavController()
            .navigate(R.id.action_settingsFragment_to_colorsSettingsFragment)
    }

    fun goToChangePassword() {
        findNavController()
            .navigate(R.id.action_settingsFragment_to_changePasswordFragment)
    }

    fun backupData() {

    }

    fun logout() {
        userViewModel.logout()
        findNavController().navigate(R.id.action_settingsFragment_to_welcomeFragment)
    }

    fun deleteAccount() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete account")
        builder.setMessage("Confirm your password to delete your account")
        val textField = EditText(requireContext())
        textField.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(textField)
        builder.setPositiveButton("Ok") { _, _ ->
            userViewModel.deleteAccount(textField.text.toString()) { isSuccessful ->
                if (isSuccessful) {
                    findNavController()
                        .navigate(R.id.action_settingsFragment_to_welcomeFragment)
                } else {
                    Snackbar.make(
                        requireView(),
                        "Failed to delete account :(",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    fun goToAbout() {
        findNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (resultCode == RESULT_OK && data != null) {
                changePhoto(data.extras!!.get("data") as Bitmap)
            }
            1 -> if (resultCode == RESULT_OK && data != null) {
                val selectedImage = data.data
                val filePathColumn = arrayOf("_data")
                if (selectedImage != null) {
                    val cursor = requireActivity().contentResolver.query(
                        selectedImage,
                        filePathColumn, null, null, null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cursor.getString(columnIndex)
                        changePhoto(BitmapFactory.decodeFile(picturePath))
                        cursor.close()
                    }
                }
            }
        }
    }

    private fun changePhoto(bitmap: Bitmap) {
        storageViewModel.uploadPhoto(bitmap, UUID.randomUUID().toString(), {
            it.storage.downloadUrl.addOnCompleteListener { downloadUriTask ->
                val photoURL = downloadUriTask.result.toString()
                userViewModel.getCurrentUser().photoURL = photoURL
                userViewModel.updateUser("photoURL", photoURL)
                // update photo displayed
                refreshPhoto()
                binding.invalidateAll()
                Snackbar.make(requireView(), "Successfully changed image", Snackbar.LENGTH_LONG)
                    .show()
            }
        }, {
            Snackbar.make(requireView(), "Failed to upload image :(", Snackbar.LENGTH_LONG)
                .show()
        })
    }

    private fun refreshPhoto() {
        userViewModel.getCurrentUser().photoURL?.let {
            loadPhotoURLToImageView(
                it,
                binding.editProfileContainer.photo
            )
        }
    }
}