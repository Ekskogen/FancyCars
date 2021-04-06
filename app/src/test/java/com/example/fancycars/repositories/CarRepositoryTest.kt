package com.example.fancycars.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.fancycars.data.db.CarDao
import com.example.fancycars.data.models.Car
import com.example.fancycars.data.models.CarAvailability
import com.example.fancycars.data.network.CarServiceStub
import com.example.fancycars.data.network.NoInternetException
import com.example.fancycars.data.network.Result
import com.example.fancycars.data.repositories.CarRepository
import com.example.fancycars.data.repositories.CarRepositoryImpl
import com.example.fancycars.ui.main.MainViewModel
import com.example.fancycars.ui.main.MainViewModel.Companion.AVAIL
import com.example.fancycars.ui.main.MainViewModel.Companion.NAME
import com.example.fancycars.ui.main.MainViewModel.Companion.NONE
import com.example.fancycars.ui.main.State
import io.mockk.*
import junit.framework.Assert
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CarRepositoryTest {

    @Rule
    @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var repositoryImpl: CarRepositoryImpl
    var service = mockk<CarServiceStub>()
    var dao = mockk<CarDao>(relaxed = true)

    var source: DataSource.Factory<Int, Car> = mockk(relaxed = true)

    val observer = mockk<Observer<State>> { every { onChanged(any()) } just Runs }
    val loadObserver = mockk<Observer<Boolean>> { every { onChanged(any()) } just Runs }
    val pagedFeedObserver = mockk<Observer<PagedList<Car>>> { every { onChanged(any()) } just Runs }

    @Before
    fun setUp() {
        repositoryImpl = CarRepositoryImpl(service, dao)
    }

    @Test
    fun getCarSourceBy_values_WillReturnDifferentSourceTypes() {
        every { dao.dataSource() } returns source

        repositoryImpl.getCarSourceBy(NONE)
        coVerify(exactly = 1) { dao.dataSource() }

        repositoryImpl.getCarSourceBy(AVAIL)
        coVerify(exactly = 1) { dao.dataSourceByAvail() }

        repositoryImpl.getCarSourceBy(NAME)
        coVerify(exactly = 1) { dao.dataSourceByName() }
    }


    @Test
    fun fetchCars_NoPagingAndFailed_WillCallServiceWithNullIndexAndReturnFailure() {
        coEvery { service.getCars(null) } returns Response.error(404, ResponseBody.Companion.create(null,""))
        runBlocking {
            val res = repositoryImpl.fetchCars(false)

            coVerify(exactly = 1) { service.getCars(null) }
            assert(res is Result.Failure)
        }
    }

    @Test
    fun fetchCars_PagingAndSuccessEmptyBody_WillCallServiceWithIndexAndReturnSuccessEnd() {
        coEvery { service.getCars(1) } returns Response.success(null)
        coEvery { dao.getAll() } returns listOf(Car(0,"","","","",2010))
        runBlocking {
            val res = repositoryImpl.fetchCars(true)

            coVerify(exactly = 1) { dao.getAll() }
            coVerify(exactly = 1) { service.getCars(1) }
            assert(res is Result.Success && res.result)
        }
    }

    @Test
    fun fetchCars_SuccessCars_WillReturnSuccessAndFetchAvailability() {
        val list = listOf(Car(0,"","","","",2010))
        coEvery { service.getCars(null) } returns Response.success(
            list
        )
        coEvery { service.getAvailability(0) } returns Response.success(
            CarAvailability("In Dealership")
        )
        coEvery { dao.insertAll(list) } just runs

        runBlocking {
            val res = repositoryImpl.fetchCars(false)

            coVerify(exactly = 1) { service.getCars(null) }
            coVerify(exactly = 2) { dao.insertAll(any()) }
            assert(res is Result.Success && !res.result)
        }
    }

    @After
    fun afterTests() {
        unmockkAll()
    }
}