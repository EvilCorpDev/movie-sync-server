package com.androidghost77.service

import com.androidghost77.model.User

interface UserService {
    fun addUser(user: User)
    fun getUser(name: String): User
    fun getAllUsers(): List<User>
    fun updateUser(user: User)
}
