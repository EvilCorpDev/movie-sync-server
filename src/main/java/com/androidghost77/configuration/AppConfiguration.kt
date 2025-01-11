package com.androidghost77.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.enterprise.context.ApplicationScoped
import com.androidghost77.model.Storage
import com.androidghost77.model.User
import com.androidghost77.service.FileListHolderService
import com.androidghost77.service.UserService
import com.androidghost77.service.impl.JsonBasedFileListHolderService
import com.androidghost77.service.impl.JsonBasedUsersService
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.io.FileInputStream

@ApplicationScoped
class AppConfiguration {

    @ApplicationScoped
    fun getObjectMapper() = jacksonObjectMapper()

    @ApplicationScoped
    fun getUserService(
            @ConfigProperty(name = "json.store.user.path") jsonPath: String,
            objectMapper: ObjectMapper,
    ): UserService {
        val jsonFile = File(jsonPath)
        if (jsonFile.exists()) {
            val users: MutableList<User> = objectMapper.readValue(FileInputStream(jsonFile))
            return JsonBasedUsersService(jsonPath, objectMapper, users)
        }
        return JsonBasedUsersService(jsonPath, objectMapper, mutableListOf())
    }

    @ApplicationScoped
    fun getInfoStore(
            @ConfigProperty(name = "json.store.info.path") jsonPath: String,
            objectMapper: ObjectMapper,
            userService: UserService,
    ): MutableMap<String, Storage> {
        val infoStore: MutableMap<String, Storage> = mutableMapOf()
        userService.getAllUsers().forEach {
            val jsonFile = File("${it.name}-$jsonPath")
            if (jsonFile.exists()) {
                infoStore[it.name] = objectMapper.readValue(FileInputStream(jsonFile))
            } else {
                infoStore[it.name] = Storage(
                        movies = mutableListOf(),
                        tvShows = mutableListOf(),
                )
            }
        }

        return infoStore
    }

    @ApplicationScoped
    fun getFileListHolderService(
            @ConfigProperty(name = "json.store.info.path") jsonPath: String,
            objectMapper: ObjectMapper,
            infoStorage: MutableMap<String, Storage>,
    ): FileListHolderService = JsonBasedFileListHolderService(jsonPath, objectMapper, infoStorage)
}
