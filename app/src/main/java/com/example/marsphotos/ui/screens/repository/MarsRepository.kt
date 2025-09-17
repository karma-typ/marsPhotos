package com.example.marsphotos.ui.screens.repository

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.marsphotos.ui.screens.network.MarsPhoto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

private const val DATABASE_NAME = "MarsDatabase"
@Dao
interface MarsDao{
    @Query("SELECT * FROM MarsPhoto")
    fun getAll(): List<MarsPhoto>

    @Query("SELECT * FROM MarsPhoto WHERE id = :id LIMIT 1")
    fun marsPhotoById(id: String): MarsPhoto

    @Insert
    fun put(marsPhoto: MarsPhoto)

    @Insert
    fun putAll(vararg marsPhoto: MarsPhoto)
}
@Database(entities = [MarsPhoto::class], version = 1)
abstract class MarsDatabase: RoomDatabase() {
    abstract fun getMarsDao(): MarsDao
}

@InstallIn(SingletonComponent::class)
@Module
class RoomModule(){

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): MarsDatabase{
         return Room.databaseBuilder(context, MarsDatabase::class.java, DATABASE_NAME)
            //.allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideDao(database: MarsDatabase): MarsDao{
        return database.getMarsDao()
    }

}

class MarsRepository @Inject constructor(private val marsDao : MarsDao) {
    private val marsPhotoMap = mutableMapOf<String, MarsPhoto>()

    private val marsPhotoSet = mutableSetOf<MarsPhoto>()

    suspend fun init() {
        marsDao.getAll().forEach { marsPhoto ->
            marsPhotoMap.put(marsPhoto.id, marsPhoto)
        }
    }

    fun getMarsPhotoList() = marsPhotoMap.values.toList()


    fun putMarsPhoto(marsPhoto: MarsPhoto){
        marsDao.put(marsPhoto)
    }
    fun putAllMarsPhoto(vararg marsPhoto: MarsPhoto){
        marsDao.putAll(marsPhoto = marsPhoto)
    }
    /**
     *  ???????? */
    fun putAll(vararg marsPhoto: MarsPhoto){
        marsPhoto.forEach { marsPhoto -> marsPhotoMap.put(marsPhoto.id, marsPhoto) }
    }

}

