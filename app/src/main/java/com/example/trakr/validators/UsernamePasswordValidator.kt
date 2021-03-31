package com.example.trakr.validators

enum class Field {
    USERNAME,
    PASSWORD,
    CONFIRM_PASSWORD
}

data class MyError(val field: Field, val message: String) {
}

class UsernamePasswordValidator {
    companion object {
        @JvmStatic
        fun validateUsernamePassword(username: String, password: String): List<MyError> {
            val errors = mutableListOf<MyError>()
            if (username.isBlank()) errors.add(
                MyError(
                    Field.USERNAME,
                    "your username cannot be blank"
                )
            )
            if (password.length < 6) errors.add(
                MyError(
                    Field.PASSWORD,
                    "your password must be at least 6 characters"
                )
            )
            return errors
        }

        @JvmStatic
        fun validateUsernamePasswordConfirmPassword(
            username: String,
            password: String,
            confirmPassword: String
        ): List<MyError> {
            val errors = validateUsernamePassword(username, password) as MutableList<MyError>
            if (confirmPassword != password) errors.add(
                MyError(
                    Field.CONFIRM_PASSWORD,
                    "the passwords entered do not match"
                )
            )
            return errors
        }
    }
}