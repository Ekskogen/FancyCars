package com.example.fancycars.ui.main

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.network.Result
import com.example.fancycars.data.repositories.CarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(
    private val pageListConfig: PagedList.Config,
    private val carRepository: CarRepository
): ViewModel() {

    val state : MutableLiveData<State> = MutableLiveData()
    val loading : MutableLiveData<Boolean> = MutableLiveData()
    private val sortType: MutableLiveData<Int?> = MutableLiveData()
    var carsPagedFeed: LiveData<PagedList<Car>> =
        Transformations.switchMap(sortType) {
            LivePagedListBuilder(carRepository.getCarSourceBy(it ?: NONE), pageListConfig)
                .setBoundaryCallback(object : PagedList.BoundaryCallback<Car>() {
                    override fun onZeroItemsLoaded() {
                        super.onZeroItemsLoaded()
//                        fetchCars()
                    }
                    override fun onItemAtEndLoaded(itemAtEnd: Car) {
                        super.onItemAtEndLoaded(itemAtEnd)
                        fetchCars(true)
                    }
                })
                .build()
        }

    fun initPagedListBy(type: Int = NONE) {
        sortType.postValue(type)
    }

    fun fetchCars(paging: Boolean = false) {
        loading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = carRepository.fetchCars(paging)
            loading.postValue(false)
            if(response is Result.Failure) {
                state.postValue(State.Error(response.throwable))
            } else if(response is Result.Success && response.result) {
                state.postValue(State.Done(true))
            } else {
                state.postValue(State.Done(false))
            }
        }
    }

    companion object {
        const val NONE = 0
        const val NAME = 1
        const val AVAIL = 2
    }
}

open class State {
    data class Error(val e: Throwable): State()
    data class Done(val endOfList: Boolean = false): State()
}