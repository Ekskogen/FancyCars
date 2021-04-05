package com.example.fancycars.di

import android.content.Context
import androidx.paging.PagedList
import com.example.fancycars.data.db.AppDatabase
import com.example.fancycars.data.network.CarService
import com.example.fancycars.data.network.CarServiceStub
import com.example.fancycars.data.repositories.CarRepository
import com.example.fancycars.data.repositories.CarRepositoryImpl
import com.example.fancycars.ui.main.MainViewModel
import com.example.fancycars.ui.main.epoxy.CarFeedController
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    factory { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }
    single { provideRetrofit(get()) }
    single { provideOkHttpClient(get()) }
    single { provideCarServiceApi(get()) }
    factory { providePageListConfig() }

    single { CarServiceStub() }
    single { provideDatabase(androidContext()) }
    factory { provideCarDao(get()) }

    single { CarRepositoryImpl(get(),  get()) as CarRepository }

    viewModel { MainViewModel(get(),  get()) }

    factory { CarFeedController() }
}

val BASE_URL = ""

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideOkHttpClient(httpLogger: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient()
        .newBuilder()
        .addInterceptor(httpLogger)
        .build()
}

fun provideCarServiceApi(retrofit: Retrofit): CarService = retrofit.create(CarService::class.java)

fun providePageListConfig(): PagedList.Config = PagedList.Config.Builder()
    .setPageSize(20)
    .setInitialLoadSizeHint(20)
    .setPrefetchDistance(5)
    .setEnablePlaceholders(false)
    .build()

fun provideDatabase(context: Context) = AppDatabase.getDatabase(context)

fun provideCarDao(db: AppDatabase) = db.carDao()