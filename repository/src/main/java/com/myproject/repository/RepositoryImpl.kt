package com.myproject.repository

import android.content.SharedPreferences
import com.myproject.repository.`object`.initFirebase
import com.myproject.repository.api.ApiNewsRepository
import com.myproject.repository.model.Article
import com.myproject.repository.room.RoomNewsRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RepositoryImpl(
    private val roomRepository: RoomNewsRepository,
    private val apiRepository: ApiNewsRepository,
    private val sharedPreferences: SharedPreferences
) : Repository {
    init {
        initFirebase()
    }

    private val _news = MutableSharedFlow<List<Article>>(
        replay = 1,
        extraBufferCapacity = 0, onBufferOverflow = BufferOverflow.SUSPEND
    )
    val news = _news.asSharedFlow()

    override suspend fun searchNews(searchQuery: String, pageIndex: Int, pageSize: Int) =
        apiRepository.searchNews(searchQuery, pageIndex, pageSize)

    override suspend fun searchNewsFromSources(nameSource: String, pageIndex: Int, pageSize: Int) =
        apiRepository.loadNewsFromSources(nameSource, pageIndex, pageSize)

    override suspend fun getNewsBookmarks(): List<Article> {
        return if (sharedPreferences.getBoolean(
                com.myproject.repository.`object`.OFFLINE_MODE,
                false
            )
        )
            roomRepository.getBookmarks()
        else
            apiRepository.getBookmarks()
    }

    override suspend fun getNewsNotes() =
        apiRepository.getNotes()

    override suspend fun getNews(
        positionViewPager: Int,
        pageIndex: Int,
        pageSize: Int
    ): List<Article> {
        return if (sharedPreferences.getBoolean(
                com.myproject.repository.`object`.OFFLINE_MODE,
                false
            )
        ) {
            roomRepository.getAllPersons(pageSize, pageIndex * pageSize)
        } else {
            apiRepository.loadNews(positionViewPager, pageIndex, pageSize)
        }
    }

    override suspend fun updateElement(news: Article) {
        if (sharedPreferences.getBoolean(com.myproject.repository.`object`.OFFLINE_MODE, false))
            roomRepository.updateElement(news)
        else
            apiRepository.updateElement(news)
    }
}