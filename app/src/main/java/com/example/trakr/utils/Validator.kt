package com.example.trakr.utils

class Validator {
    data class Error(val field: Field, val message: String) {
    }

    enum class Field {
        USERNAME,
        PASSWORD,
        CONFIRM_PASSWORD
    }

    companion object {
        @JvmStatic
        fun validateUsername(username: String): Error? {
            if (username.isBlank()) return Error(
                Field.USERNAME,
                "your username cannot be blank"
            )
            if (username.length < 2) return Error(
                Field.USERNAME,
                "your username must be at least 2 characters"
            )
            return null
        }

        @JvmStatic
        fun validatePasswordConfirmPassword(
            password: String,
            confirmPassword: String
        ): List<Error> {
            val errors = mutableListOf<Error>()
            if (password.isBlank()) errors.add(
                Error(
                    Field.PASSWORD,
                    "your password cannot be blank"
                )
            )
            if (password.length < 6) errors.add(
                Error(
                    Field.PASSWORD,
                    "your password must be at least 6 characters"
                )
            )
            if (confirmPassword != password) errors.add(
                Error(
                    Field.CONFIRM_PASSWORD,
                    "the passwords entered do not match"
                )
            )
            return errors
        }
    }
}