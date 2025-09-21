package com.example.news.di

import android.content.Context
import androidx.room.Room
import com.example.news.data.local.NewsDao
import com.example.news.data.local.NewsDatabase
import com.example.news.data.remote.NewsApiService
import com.example.news.data.repository.NewsRepositoryImpl
import com.example.news.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

interface DataModule {

    @Binds
    @Singleton
    fun bindsRepository(repositoryImpl: NewsRepositoryImpl): NewsRepository


    companion object {
        @Provides
        @Singleton
        fun providesNewsDatabase(@ApplicationContext context: Context): NewsDatabase {
            return Room.databaseBuilder(
                context = context,
                klass = NewsDatabase::class.java,
                name = "news.db"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }

        @Provides
        @Singleton
        fun providesNewsDao(database: NewsDatabase): NewsDao {
            return database.newsDao()
        }


        @Provides
        @Singleton
        fun providesApiService(
            retrofit: Retrofit
        ): NewsApiService {

            return retrofit.create<NewsApiService>()

        }


        @Provides
        @Singleton
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        }

        @Provides
        @Singleton
        fun provideConverterFactory(
            json: Json
        ): Converter.Factory {
            return json.asConverterFactory("application/json".toMediaType())
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            converterFactory: Converter.Factory
        ): Retrofit {
            return Retrofit.Builder() //генерирует реализацию апи сервиса
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(converterFactory) //обьект который показывает каким образом преобразовывать полученный ответ
                // с сервера в экземпляры нашего класса
                .build()


        }


    }


}