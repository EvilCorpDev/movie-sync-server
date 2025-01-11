package com.androidghost77.resources

import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import com.androidghost77.model.MovieInfo
import com.androidghost77.service.FileListHolderService
import com.androidghost77.service.FileService
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Path("/files")
class FilesResource @Inject constructor(
        private val fileListHolderService: FileListHolderService,
        private val fileService: FileService,
){

    @GET
    @Path("/scan")
    @Produces(MediaType.APPLICATION_JSON)
    fun scan() {
        fileService.scanFolders()
    }

    @GET
    @Path("/{userName}/movies")
    @Produces(MediaType.APPLICATION_JSON)
    fun movies(@PathParam("userName") userName: String): List<MovieInfo> = fileListHolderService.movies(userName)

    @GET
    @Path("/{userName}/tv")
    @Produces(MediaType.APPLICATION_JSON)
    fun tvShows(@PathParam("userName") userName: String): List<MovieInfo> = fileListHolderService.tvShows(userName)

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getFile(@PathParam("id") id: String): Uni<Response> {
        val fileById = fileService.getFileById(id)
        val response: Response.ResponseBuilder = Response.ok(fileById as Any)
        val fileName = URLEncoder.encode(fileById.name.replace(" ", "_"), StandardCharsets.UTF_8)
        response.header("Content-Disposition", "attachment;filename=$fileName")
        return Uni.createFrom().item(response.build())
    }
}
