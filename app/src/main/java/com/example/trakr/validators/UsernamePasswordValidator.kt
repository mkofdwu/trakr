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
        fun validateUsername(username: String): MyError? {
            if (username.isBlank()) return MyError(
                Field.USERNAME,
                "your username cannot be blank"
            )
            if (username.length < 2) return MyError(
                Field.USERNAME,
                "your username must be at least 2 characters"
            )
            return null
        }

        @JvmStatic
        fun validatePasswordConfirmPassword(
            password: String,
            confirmPassword: String
        ): List<MyError> {
            val errors = mutableListOf<MyError>()
            if (password.length < 6) errors.add(
                MyError(
                    Field.PASSWORD,
                    "your password must be at least 6 characters"
                )
            )
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