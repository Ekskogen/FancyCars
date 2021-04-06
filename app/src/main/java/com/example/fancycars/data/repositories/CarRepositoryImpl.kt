package com.example.fancycars.data.repositories

import android.util.Log
import androidx.paging.DataSource
import com.example.fancycars.data.db.CarDao
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.network.CarServiceStub
import com.example.fancycars.data.network.NoInternetException
import com.example.fancycars.data.network.Result
import com.example.fancycars.pmap
import com.example.fancycars.ui.main.MainViewModel
import java.lang.Exception
import java.net.UnknownHostException

class CarRepositoryImpl(val service: CarServiceStub, val carDao: CarDao): CarRepository {

    companion object {
        const val AV_IN = "In Dealership"
        const val AV_OUT = "Out of Stock"
        const val AV_UNA = "Unavailable"
    }


    override fun getCarSourceBy(type: Int): DataSource.Factory<Int, Car> {
        return when(type) {
            MainViewModel.AVAIL -> carDao.dataSourceByAvail()
            MainViewModel.NAME -> carDao.dataSourceByName()
            else -> carDao.dataSource()
        }
    }

    /**
     * We are fetching the whole list of cars since api does not support paging for now.
     * To activate paging with service provide 'withPaging' as true which will
     * download items by index 20 by 20 for our stub system.
     * We are paging the list shown in recyclerview with data in DB.
     * Return true if it is end of list
     */
    override suspend fun fetchCars(withPaging: Boolean): Result<Boolean> {
        return try {
            var startIndex: Int? = null
            if(withPaging) {
                val items = carDao.getAll()
                startIndex = items.maxOf { it.id } + 1
            }
            val response = service.getCars(startIndex)
            if(response.isSuccessful) {
                val body = response.body()
                if(body.isNullOrEmpty())
                    Result.Success(true)
                else {
                    response.body()?.let {
                        carDao.insertAll(it)
                        fetchAvailabilityStatus(it)
                    }
                    Result.Success(false)
                }
            } else  {
                Result.Failure()
            }
        } catch (e: Exception) {
            if(e is UnknownHostException) Result.Failure(NoInternetException)
            else Result.Failure()
        }
    }

    /**
     * Fetching the status of the car will be made in parallel using a pmap function which
     * executes a map operator in the list with async.
     */
    private suspend fun fetchAvailabilityStatus(cars: List<Car>) {
        val mergedCars = cars.pmap { car ->
                val res = service.getAvailability(car.id)
                if(res.isSuccessful) {
                    res.body()?.available?.let {
                        car.availablity = it
                        car
                    }
                } else car
            }.filterNotNull()
        carDao.insertAll(mergedCars)
    }

    override suspend fun deleteLocalCars() {
        carDao.deleteAll()
    }

}