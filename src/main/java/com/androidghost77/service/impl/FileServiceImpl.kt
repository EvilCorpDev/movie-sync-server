package com.androidghost77.service.impl

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import com.androidghost77.constants.ShowType
import com.androidghost77.model.MovieInfo
import com.androidghost77.service.ChecksumCalculator
import com.androidghost77.service.FileListHolderService
import com.androidghost77.service.FileService
import com.androidghost77.service.UserService
import java.io.File
import java.util.*


@ApplicationScoped
class FileServiceImpl @Inject constructor (
        private val usersService: UserService,
        private val store: FileListHolderService,
        private val checksumCalculator: ChecksumCalculator
) : FileService {

    override fun scanFolders() {
        usersService.getAllUsers().forEach {
            val movies = scanFolder(it.name, it.moviePath, ShowType.MOVIE)
            store.addFileInfos(it.name, movies, ShowType.MOVIE)
            val tvShows = scanFolder(it.name, it.tvPath, ShowType.TV_SHOW)
            store.addFileInfos(it.name, tvShows, ShowType.TV_SHOW)
        }
    }

    override fun getFileById(id: String): File {
        val movieInfoById = store.getMovieInfoById(id)
        val rootPath = when(movieInfoById.showType) {
            ShowType.MOVIE -> usersService.getUser(movieInfoById.owner).moviePath
            ShowType.TV_SHOW -> usersService.getUser(movieInfoById.owner).tvPath
        }
        return File("$rootPath${movieInfoById.folder}${movieInfoById.name}")
    }

    private fun scanFolder(userName: String, path: String, type: ShowType): List<MovieInfo> = File(path)
                .listFiles()
                .filter { !store.containsFile(userName, it.name) }
                .map { fileToMovieInfo(userName, it, type) }
                .flatten()

    private fun fileToMovieInfo(userName: String, file: File, type: ShowType, parentFolder: String = "/"): List<MovieInfo> {
        if (file.isFile) {
            val id = UUID.randomUUID().toString()
            checksumCalculator.addFileToQueue(userName, file, id, type)
            return listOf(MovieInfo(
                    name = file.name,
                    id = id,
                    folder = parentFolder,
                    showType = type,
                    size = file.length(),
                    hashSum = "",
                    owner = userName,
            ))
        }

        return file.listFiles()
                .map { fileToMovieInfo(userName, it, type, "${parentFolder}${file.name}/") }
                .flatten()
    }

}
