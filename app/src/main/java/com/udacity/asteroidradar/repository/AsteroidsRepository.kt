package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.DEFAULT_END_DATE_DAYS
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import org.threeten.bp.LocalDate

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    val todaysDate = LocalDate.now()
    val endDate = todaysDate.plusDays(DEFAULT_END_DATE_DAYS.toLong()).toString()

    val pictureOfTheDay: LiveData<PictureOfDay> = Transformations
            .map(database.asteroidDao.getPictureOfTheDay()) {
        it?.asDomainModel()
    }

    val asteroidsOfTheDay: LiveData<List<Asteroid>> = Transformations
            .map(database.asteroidDao.getTodaysAsteroids(todaysDate.toString())) {
        it.asDomainModel()
    }

    val asteroidsOfTheWeek: LiveData<List<Asteroid>> = Transformations
            .map(database.asteroidDao.getWeekOfAsteroids(todaysDate.toString(), endDate)) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val jsonResult = Network.nasaApiService.loadAsteroids(todaysDate.toString())
                val asteroidList = parseAsteroidsJsonResult(jsonResult)
                val dbAsteroidList = mutableListOf<DatabaseAsteroid>()

                for (asteroid in asteroidList) {
                    val dbAsteroid = DatabaseAsteroid(
                            asteroid.id,
                            asteroid.codename,
                            asteroid.closeApproachDate,
                            asteroid.absoluteMagnitude,
                            asteroid.estimatedDiameter,
                            asteroid.relativeVelocity,
                            asteroid.distanceFromEarth,
                            asteroid.isPotentiallyHazardous
                    )
                    dbAsteroidList.add(dbAsteroid)
                }

                database.asteroidDao.insertAll(dbAsteroidList)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun refreshPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay = Network.nasaApiService.loadImageOfTheDay()
                database.asteroidDao.insertAsteroidPicture(pictureOfDay.asDatabaseModel())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}