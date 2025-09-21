package com.example.news.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [ArticleDbModel::class, ],
    version = 6,
    exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        private var instance: NewsDatabase? = null

        private val LOCK = Any()

        fun getInstance(context: Context): NewsDatabase {
            instance?.let { return it }
            synchronized(LOCK) {
                return Room.databaseBuilder(
                    context = context,
                    klass = NewsDatabase::class.java,
                    name = "news.db"
                ).build().also {
                    instance = it
                }
            }
        }
    }

}