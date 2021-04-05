package com.example.fancycars.data.repositories

import androidx.paging.DataSource
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.network.Result

interface CarRepository {

    fun getCarSourceBy(type: Int): DataSource.Factory<Int, Car>
    suspend fun fetchCars(withPaging: Boolean): Result<Boolean>
    suspend fun deleteLocalCars()

}