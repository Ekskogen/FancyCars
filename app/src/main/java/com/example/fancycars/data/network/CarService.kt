package com.example.fancycars.data.network

import com.example.fancycars.data.models.Car
import com.example.fancycars.data.models.CarAvailability
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CarService {

    @GET("cars")
    suspend fun getCars(): Response<List<Car>>


    @GET("availability")
    suspend fun getAvailability(@Query("id") id: Int): Response<CarAvailability>

}