package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate = :todaysDate ORDER BY closeApproachDate DESC")
    fun getTodaysAsteroids(todaysDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN :todaysDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getWeekOfAsteroids(todaysDate: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<DatabaseAsteroid>)

    @Query("SELECT * FROM picture_of_the_day")
    fun getPictureOfTheDay(): LiveData<DatabasePictureOfTheDay?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsteroidPicture(pictureOfTheDay: DatabasePictureOfTheDay)

}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfTheDay::class], version = 3, exportSchema = false)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                    .databaseBuilder(context.applicationContext, AsteroidsDatabase::class.java, "asteroids")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }
    return INSTANCE
}