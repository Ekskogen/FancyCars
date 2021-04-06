package com.example.fancycars.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fancycars.data.repositories.CarRepositoryImpl.Companion.AV_IN
import com.example.fancycars.data.repositories.CarRepositoryImpl.Companion.AV_UNA

@Entity
data class Car(@PrimaryKey val id: Int,
               val name: String,
               val pictureUrl: String,
               val make: String,
               val model: String,
               val year: Int,
               var availablity: String = AV_UNA)