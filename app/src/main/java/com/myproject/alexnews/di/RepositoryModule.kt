package com.myproject.alexnews.di

import com.myproject.repository.RepositoryImpl
import com.myproject.repository.`object`.BASE_URL
import com.myproject.repository.`object`.DATABASE_NAME
import com.myproject.repository.api.ApiNewsRepository
import com.myproject.repository.api.ApiRepository
import com.myproject.repository.api.retrofit.ApiService
import com.myproject.repository.room.AppDataBase
import com.myproject.repository.room.RoomNewsRepository
import com.myproject.repository.room.RoomRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val repositoryModule = module {

    single {
        RepositoryImpl(
            get(),
            get(),
            get()
        )
    }

    single<ApiNewsRepository> { ApiRepository(get(), get(), get()) }
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiService::class.java)
    }

    single<RoomNewsRepository> { RoomRepository(get()) }
    single { AppDataBase.buildsDatabase(androidContext(), DATABASE_NAME).ArticleDao() }
}