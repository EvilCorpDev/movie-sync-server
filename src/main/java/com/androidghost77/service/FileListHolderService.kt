package com.androidghost77.service

import com.androidghost77.constants.ShowType
import com.androidghost77.model.MovieInfo

interface FileListHolderService {
    fun movies(userName: String): MutableList<MovieInfo>
    fun tvShows(userName: String): MutableList<MovieInfo>
    fun addFileInfos(userName: String, files: List<MovieInfo>, type: ShowType)
    fun removeFileInfo(userName: String, name: String, type: ShowType)
    fun containsFile(userName: String, name: String): Boolean
    fun getMovieInfoById(id: String): MovieInfo
    fun updateChecksum(userName: String, id: String, type: ShowType, checksum: String)
}
