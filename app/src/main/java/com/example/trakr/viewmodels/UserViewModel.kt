package com.example.trakr.viewmodels

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.trakr.models.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class UserViewModel : ViewModel() {
    private val usersRef = FirebaseFirestore.getInstance().collection("users")

    var isLoading = false
        private set
    private var currentUser: User? = null
    private val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val isLoggedIn: Boolean
        get() = fbAuth.currentUser != null

    fun getCurrentUser(): User = currentUser!!

    fun refreshCurrentUser(authListener: AuthListener) {
        if (fbAuth.currentUser == null) return
        isLoading = true
        val uid = fbAuth.currentUser!!.uid
        val userTask = usersRef.document(uid).get()
        userTask.addOnCompleteListener { fetchUserTask ->
            if (fetchUserTask.isSuccessful) {
                val user = User.fromDoc(fetchUserTask.result!!)
                currentUser = user
                authListener.onAuthenticated(user)
            } else {
                authListener.onException(fetchUserTask.exception)
            }
        }
    }

    fun login(username: String, password: String, authListener: AuthListener) {
        fbAuth.signInWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshCurrentUser(authListener)
                } else {
                    authListener.onException(task.exception)
                }
            }.addOnFailureListener { }
    }

    private val defaultColors = mutableListOf(
        Color.parseColor("#8636C6"), // purple
        Color.parseColor("#C68336"), // orange
        Color.parseColor("#C63636"), // red
    )

    fun register(username: String, password: String, listener: AuthListener) {
        fbAuth.createUserWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user =
                        User(
                            task.result!!.user!!.uid,
                            username,
                            null,
                            null,
                            defaultColors,
                            LocalDateTime.now()
                        )
                    usersRef
                        .document(user.id)
                        .set(user.toHashMap()).addOnCompleteListener { insertUserTask ->
                            if (insertUserTask.isSuccessful) {
                                currentUser = user
                                listener.onAuthenticated(user)
                            } else {
                                listener.onException(insertUserTask.exception)
                            }
                        }
                } else {
                    listener.onException(task.exception)
                }
            }
    }

    fun logout() {
        fbAuth.signOut()
        currentUser = null
    }

    fun deleteAccount(password: String, onComplete: (isSuccessful: Boolean) -> Unit) {
        val credential = EmailAuthProvider.getCredential(
            "${currentUser!!.username}@example.com",
            password
        )
        val fbUser = fbAuth.currentUser!!
        fbUser.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                usersRef.document(currentUser!!.id).delete()
                usersRef.document(currentUser!!.id).collection("timeEntries").get()
                    .addOnCompleteListener { getDocsTask ->
                        if (getDocsTask.isSuccessful) {
                            getDocsTask.result!!.documents.forEach { doc -> doc.reference.delete() }
                        }
                    }
                fbUser.delete()
                currentUser = null
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun updateUser(field: String, value: Any?) {
        usersRef.document(currentUser!!.id).update(field, value)
    }

    fun changeUsername(newUsername: String) {
        fbAuth.currentUser!!.updateEmail("$newUsername@example.com")
    }

    fun changePassword(currentPassword: String, newPassword: String, listener: AuthListener) {
        val credential = EmailAuthProvider.getCredential(
            "${currentUser!!.username}@example.com",
            currentPassword
        )
        val fbUser = fbAuth.currentUser!!
        fbUser.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                fbUser.updatePassword(newPassword)
                listener.onAuthenticated(currentUser!!)
            } else {
                listener.onException(it.exception)
            }
        }.addOnFailureListener {
            listener.onException(it)
        }
    }

    interface AuthListener {
        fun onAuthenticated(user: User)
        fun onException(exception: Exception?)
    }
}
