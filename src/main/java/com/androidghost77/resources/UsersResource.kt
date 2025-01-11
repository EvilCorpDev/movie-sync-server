package com.androidghost77.resources

import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import com.androidghost77.model.User
import com.androidghost77.service.UserService

@Path("/users")
class UsersResource @Inject constructor(
        private val userService: UserService,
) {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    fun addUser(user: User) {
        userService.addUser(user)
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    fun updateUser(user: User) {
        userService.updateUser(user)
    }

    @GET
    @Path("/{userName}")
    fun getUser(@PathParam("userName") userName: String): User = userService.getUser(userName)
}
