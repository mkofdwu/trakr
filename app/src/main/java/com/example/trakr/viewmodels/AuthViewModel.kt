package com.example.trakr.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trakr.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime

class AuthViewModel : ViewModel() {
    private val usersRef = FirebaseFirestore.getInstance().collection("users")

    var isLoading = false
        private set
    private val currentUser: MutableLiveData<User> = MutableLiveData<User>()
    private val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val isLoggedIn: Boolean
        get() = fbAuth.currentUser != null

    fun getCurrentUser(): User? {
        return currentUser.value
    }

    fun refreshCurrentUser(completeListener: CompleteListener) {
        if (fbAuth.currentUser == null) return
        isLoading = true
        val uid = fbAuth.currentUser!!.uid
        val userTask = usersRef.document(uid).get()
        userTask.addOnCompleteListener { fetchUserTask ->
            if (fetchUserTask.isSuccessful) {
                val user = User.fromHashMap(fetchUserTask.result!!.data as HashMap<String, Any?>)
                currentUser.value = user
                completeListener.onLoggedIn()
            } else {
                completeListener.onException(fetchUserTask.exception)
            }
        }
    }

    fun login(username: String, password: String, completeListener: CompleteListener) {
        fbAuth.signInWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    refreshCurrentUser(completeListener)
                } else {
                    completeListener.onException(task.exception)
                }
            }
    }

    fun register(username: String, password: String, listener: CompleteListener) {
        fbAuth.createUserWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(username, "", LocalDateTime.now(), null)
                    usersRef
                        .document(task.result!!.user!!.uid)
                        .set(user.toHashMap()).addOnCompleteListener { insertUserTask ->
                            if (insertUserTask.isSuccessful) {
                                currentUser.value = user
                                listener.onLoggedIn()
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
    }

    interface CompleteListener {
        fun onLoggedIn()
        fun onException(exception: Exception?)
    }
}
