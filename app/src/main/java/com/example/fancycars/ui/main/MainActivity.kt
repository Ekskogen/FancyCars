package com.example.fancycars.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fancycars.R
import com.example.fancycars.data.network.NoInternetException
import com.example.fancycars.databinding.ActivityMainBinding
import com.example.fancycars.ui.main.epoxy.CarFeedController
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel : MainViewModel by viewModel()

    lateinit var binding: ActivityMainBinding

    val feedController: CarFeedController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initObservers()
        initLogic()
    }

    private fun initLogic() {
        feedController.endReached = false
        viewModel.fetchCars()
    }

    private fun initObservers() {
        viewModel.initPagedListBy()
        viewModel.carsPagedFeed?.observe(this) {
            feedController.submitList(it)
        }

        viewModel.state.observe(this) {
            when(it) {
                is State.Done -> {
                    binding.warningTV.visibility = View.INVISIBLE
                    feedController.endReached = it.endOfList
                }
                is State.Error -> {
                    binding.warningTV.visibility = View.VISIBLE
                    if (it.e is NoInternetException) {
                        binding.warningTV.text = getString(R.string.error_internet_connection)
                    } else {
                        binding.warningTV.text = getString(R.string.error_loading_cars)
                    }
                }
            }
        }

        viewModel.loading.observe(this) {
            binding.progressBar.visibility = if(it) View.VISIBLE else View.GONE
        }
    }

    private fun initViews() {
        binding.carRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = feedController.adapter
        }

        viewModel.state.observe(this) {
            when(it) {
                is State.Done -> {
                    warningTV.visibility = View.GONE
                    feedController.endReached = it.endOfList
                }
                is State.Error -> {
                    warningTV.visibility = View.VISIBLE
                    if (it.e is NoInternetException) {
                        warningTV.text = getString(R.string.error_internet_connection)
                    } else {
                        warningTV.text = getString(R.string.error_loading_cars)
                    }
                }
            }
        }

        binding.sortBtn.setOnClickListener {
            showSortList()
        }
    }

    private fun showSortList() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sort_tile))
        val seasons = arrayOf(
            getString(R.string.sort_none),
            getString(R.string.sort_name),
            getString(R.string.sort_avail)
        )
        builder.setItems(seasons) { dialog, which ->
            carRecyclerView.smoothScrollToPosition(0)
            viewModel.initPagedListBy(which)
        }
        val dialog = builder.create()
        dialog.show()
    }
}