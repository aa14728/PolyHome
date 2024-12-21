package com.albert.polyhome

data class GrantAccesData(
    val userLogin: String
)

data class GrantedUserData(
    val userLogin: String,
    val owner: Int
)