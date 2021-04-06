package com.example.fancycars.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.network.CommonException
import com.example.fancycars.data.network.NoInternetException
import com.example.fancycars.data.repositories.CarRepository
import com.example.fancycars.ui.main.MainViewModel
import com.example.fancycars.ui.main.State
import com.example.fancycars.data.network.Result
import com.example.fancycars.ui.main.MainViewModel.Companion.NONE
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel
    var repository: CarRepository = mockk()
    var listConfig: PagedList.Config = PagedList.Config.Builder()
        .setPageSize(20)
        .setInitialLoadSizeHint(20)
        .setPrefetchDistance(5)
        .setEnablePlaceholders(false)
        .build()
    var source: DataSource.Factory<Int, Car> = mockk(relaxed = true)

    val observer = mockk<Observer<State>> { every { onChanged(any()) } just Runs }
    val loadObserver = mockk<Observer<Boolean>> { every { onChanged(any()) } just Runs }
    val pagedFeedObserver = mockk<Observer<PagedList<Car>>> { every { onChanged(any()) } just Runs }

    @Before
    fun setUp() {
        viewModel = MainViewModel(listConfig, repository)

        viewModel.state.observeForever(observer)
        viewModel.loading.observeForever(loadObserver)
        viewModel.carsPagedFeed.observeForever(pagedFeedObserver)
    }

    @Test
    fun fetchCars_Failure_WillPostException() {
        coEvery { repository.fetchCars(false) }  returns Result.Failure(NoInternetException)

        viewModel.fetchCars()

        verifySequence {
            loadObserver.onChanged(true)
            loadObserver.onChanged(false)
            observer.onChanged(State.Error(NoInternetException))
        }
    }

    @Test
    fun fetchCars_SuccessEndOfList_WillPostEndOfListTrue() {
        coEvery { repository.fetchCars(false) }  returns Result.Success(true)

        viewModel.fetchCars()

        verifySequence {
            loadObserver.onChanged(true)
            loadObserver.onChanged(false)
            observer.onChanged(State.Done(true))
        }
    }

    @Test
    fun fetchCars_SuccessEndOfListFalse_WillPostEndOfListFalse() {
        coEvery { repository.fetchCars(true) }  returns Result.Success(false)

        viewModel.fetchCars(true)

        coVerifySequence {
            loadObserver.onChanged(true)
            repository.fetchCars(true)
            loadObserver.onChanged(false)
            observer.onChanged(State.Done(false))
        }
    }


    @After
    fun afterTests() {
        unmockkAll()
    }

}