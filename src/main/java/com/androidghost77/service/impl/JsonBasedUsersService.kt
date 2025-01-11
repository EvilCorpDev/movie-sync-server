package com.androidghost77.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.androidghost77.exception.NotFoundException
import com.androidghost77.exception.UserAlreadyExistsException
import com.androidghost77.model.User
import com.androidghost77.service.UserService
import java.io.FileOutputStream
import java.util.concurrent.Executors

class JsonBasedUsersService(
        private val jsonStorePath: String,
        private val objectMapper: ObjectMapper,
        private val usersList: MutableList<User>,
) : UserService {
    private val executor = Executors.newSingleThreadExecutor()

    override fun addUser(user: User) {
        usersList.find { it.name == user.name }
                ?.let { throw UserAlreadyExistsException("User with name ${user.name} already exists") }
        usersList.add(user)
        updateJsonStorage()
    }

    override fun getUser(name: String): User =
            usersList.find { it.name == name }
                ?: throw NotFoundException("Can't find user with name $name")

    override fun getAllUsers(): List<User> = usersList

    override fun updateUser(user: User) {
        val existingUser = usersList.find { it.name == user.name }
                ?: throw NotFoundException("Can't find user with name ${user.name}")
        usersList.remove(existingUser)
        usersList.add(user)
        updateJsonStorage()
    }

    private fun updateJsonStorage() {
        executor.run { objectMapper.writeValue(FileOutputStream(jsonStorePath), usersList) }
    }
}
