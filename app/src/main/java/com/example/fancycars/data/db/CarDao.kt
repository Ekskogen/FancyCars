package com.example.fancycars.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fancycars.data.models.Car

@Dao
interface CarDao {

    @Query("SELECT * FROM car")
    suspend fun getAll(): List<Car>

//    @Query("SELECT count(*) FROM photo")
//    suspend fun getAllCount(): Int

    @Query("SELECT * FROM car")
    fun dataSource(): DataSource.Factory<Int, Car>

    @Query("SELECT * FROM car order by name")
    fun dataSourceByName(): DataSource.Factory<Int, Car>

    @Query("SELECT * FROM car order by availablity")
    fun dataSourceByAvail(): DataSource.Factory<Int, Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<Car>)

    @Query("DELETE FROM car")
    suspend fun deleteAll()

}