package com.apulum.tenis.data.model

enum class UserRole(val apiValue: String) {
    CLIENT("client"),
    ADMIN("admin");

    companion object {
        fun fromApi(value: String?): UserRole =
            entries.find { it.apiValue == value } ?: CLIENT
    }
}
