package com.androidghost77.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.androidghost77.constants.ShowType
import com.androidghost77.exception.NotFoundException
import com.androidghost77.model.MovieInfo
import com.androidghost77.model.Storage
import com.androidghost77.model.UpdateEvent
import com.androidghost77.service.FileListHolderService
import com.androidghost77.service.UpdateQueueService
import java.io.FileOutputStream
import java.util.concurrent.Executors

class JsonBasedFileListHolderService(
        private val jsonStorePath: String,
        private val objectMapper: ObjectMapper,
        private val infoStore: MutableMap<String, Storage>,
        private val updateQueueService: UpdateQueueService
) : FileListHolderService {

    private val executor = Executors.newSingleThreadExecutor()
    private val fileNames: MutableMap<String, MutableSet<String>> = infoStore.entries
            .associate { it.key to it.value.movies.plus(it.value.tvShows).map(MovieInfo::name).toMutableSet() }
            .toMutableMap()

    override fun movies(userName: String): MutableList<MovieInfo> = infoStore[userName]?.movies
            ?: throw NotFoundException("Can't find movies for user $userName")

    override fun tvShows(userName: String): MutableList<MovieInfo> = infoStore[userName]?.tvShows
            ?: throw NotFoundException("Can't find tv shows for user $userName")

    override fun addFileInfos(userName: String, files: List<MovieInfo>, type: ShowType) {
        val names = fileNames[userName] ?: mutableSetOf()
        fileNames[userName] = names
        val storage: Storage = infoStore[userName] ?: Storage(
                movies = mutableListOf(),
                tvShows = mutableListOf(),
        )
        infoStore[userName] = storage
        val newFiles = files.filter { !names.contains(it.name) }
        when(type) {
            ShowType.MOVIE -> storage.movies.addAll(newFiles)
            ShowType.TV_SHOW -> storage.tvShows.addAll(newFiles)
        }
        names.addAll(newFiles.map(MovieInfo::name))
        updateJsonStorage(userName)

        newFiles.forEach { movieInfo -> updateQueueService.sendUpdateEvent(UpdateEvent(movieInfo.name, movieInfo.id,
            movieInfo.showType.name)) }
    }

    override fun removeFileInfo(userName: String, name: String, type: ShowType) {
        val names = fileNames[userName] ?: mutableSetOf()
        fileNames[userName] = names
        val storage: Storage = infoStore[userName] ?: throw NotFoundException("Can't find $type for user $userName")
        when(type) {
            ShowType.MOVIE -> storage.movies.removeIf { file -> file.name == name }
            ShowType.TV_SHOW -> storage.tvShows.removeIf { file -> file.name == name }
        }
        names.remove(name)
        updateJsonStorage(userName)
    }

    override fun containsFile(userName: String, name: String): Boolean = fileNames[userName]?.contains(name) ?: false

    override fun getMovieInfoById(id: String): MovieInfo =
            infoStore.values.flatMap { it.movies + it.tvShows}
                    .find { it.id == id  }
                    ?: throw NotFoundException("Can't find movieInfo with id: $id")

    override fun updateChecksum(userName: String, id: String, type: ShowType, checksum: String) {
        val infos = when(type) {
            ShowType.MOVIE -> infoStore[userName]?.movies
            ShowType.TV_SHOW -> infoStore[userName]?.tvShows
        }
        val foundItem = infos?.find { it.id == id }
        foundItem?.let {
            infos.remove(it)
            infos.add(MovieInfo(
                    name = it.name,
                    id = it.id,
                    folder = it.folder,
                    size = it.size,
                    showType = it.showType,
                    hashSum = checksum,
                    owner = userName,
            ))
        }
        updateJsonStorage(userName)
    }

    private fun updateJsonStorage(userName: String) {
        executor.run { objectMapper.writeValue(FileOutputStream("$userName-$jsonStorePath"), infoStore[userName]) }
    }
}
