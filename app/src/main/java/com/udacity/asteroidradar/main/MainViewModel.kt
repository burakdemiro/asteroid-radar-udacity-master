package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import com.udacity.asteroidradar.database.getDatabase

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)
    private val filter = MutableLiveData(FilterAsteroids.OfTheWeek)
    
    init {
        viewModelScope.launch { 
            asteroidsRepository.apply {
                refreshPictureOfTheDay()
                refreshAsteroids()
            }
        }
    }

    val listOfAsteroids = Transformations.switchMap(filter) { filter ->
        when (filter) {
            FilterAsteroids.OfTheDay -> asteroidsRepository.asteroidsOfTheDay
            else -> asteroidsRepository.asteroidsOfTheWeek
        }
    }

    val pictureOfTheDay = asteroidsRepository.pictureOfTheDay

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()
    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    fun onAsteroidClickedEvent(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun onNavigatedToAsteroidDetail() {
        _navigateToAsteroidDetail.value = null
    }

    fun updateFilter(filterValue: FilterAsteroids) {
        filter.value = filterValue
    }

    enum class FilterAsteroids {
        OfTheWeek,
        OfTheDay
    }


    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}