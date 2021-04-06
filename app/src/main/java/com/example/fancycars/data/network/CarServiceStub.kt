package com.example.fancycars.data.network

import android.util.Log
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.models.CarAvailability
import com.example.fancycars.data.repositories.CarRepositoryImpl.Companion.AV_IN
import com.example.fancycars.data.repositories.CarRepositoryImpl.Companion.AV_OUT
import com.example.fancycars.data.repositories.CarRepositoryImpl.Companion.AV_UNA
import retrofit2.Response

class CarServiceStub {

    suspend fun getCars(startIndex: Int?): Response<List<Car>> {
        Thread.sleep(4000)
        return Response.success(cars(startIndex))
    }

    suspend fun getAvailability(id: Int): Response<CarAvailability> {
        Thread.sleep(400)
        return Response.success(CarAvailability(avail(id)))
    }

    companion object {

        val names = arrayListOf("Beautiful car", "Sublime car", "Epic car", "My awesome car", "Ok car")
        val picture = arrayListOf("https://media.wired.com/photos/5e162edc7ecdd1000834110d/master/pass/Transpo_Ta11.jpg",
        "https://media.wired.com/photos/5d09594a62bcb0c9752779d9/master/pass/Transpo_G70_TA-518126.jpg,",
                "https://carwow-uk-wp-1.imgix.net/volvo-xc40-3.jpg?auto=format&cs=tinysrgb&fit=clip&ixlib=rb-1.1.0&q=60&w=750")
        val make = arrayListOf("Tesla", "Volvo", "Nissan", "Kia")
        val model = arrayListOf("K3", "Model 3", "Aventador", "Corolla", "Sun", "CX60")

        val av = arrayListOf(AV_IN, AV_OUT, AV_UNA)

        /**
         * First mode: 120 items come at once
         * Second mode: Stub response to 20 items per page and 120 limit
         */
        fun cars(startIndex: Int? = null): ArrayList<Car> {
            var start = 0
            var end = 120
            if(startIndex != null) {
                if (startIndex > 120) return arrayListOf()
                start = startIndex
                end = if (startIndex + 20 > 120) 120 else startIndex + 20
            }

            val cars = arrayListOf<Car>()
            for (i in start..end) {
                cars.add(Car(i, names[i%5], picture[i%3], make[i%4], model[i%6], 2021-i))
            }
            return cars
        }

        fun avail(id: Int) = av[id%3]
    }
}