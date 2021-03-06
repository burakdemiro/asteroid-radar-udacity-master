package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val application = requireNotNull(this.activity).application
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }

    private lateinit var adapter: MainAdapter

    private lateinit var binding: FragmentMainBinding // Lateinit to ensure availability throughout the entire class, initialised in onCreateView.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        adapter = MainAdapter(AsteroidClick { asteroid -> viewModel.onAsteroidClickedEvent(asteroid) })
        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.pictureOfTheDay.observe(viewLifecycleOwner, Observer {
            it?.let {
                Picasso.with(requireContext()) // Picasso used as per suggestion in the project instructions.
                        .load(it.url)
                        .into(binding.activityMainImageOfTheDay)
            }
        })

        showLoadingProgress()

        viewModel.listOfAsteroids.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)

            hideLoadingProgress()
        })

        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                val action = MainFragmentDirections.actionShowDetail(it)
                this.findNavController().navigate(action)
                viewModel.onNavigatedToAsteroidDetail()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_today_menu -> {
                viewModel.updateFilter(MainViewModel.FilterAsteroids.OfTheDay)
            }
            else -> {
                viewModel.updateFilter(MainViewModel.FilterAsteroids.OfTheWeek)
                binding.asteroidRecycler.smoothScrollToPosition(0)
            }
        }
        return true
    }

    private fun showLoadingProgress() {
        binding.asteroidRecycler.visibility = View.GONE
        binding.statusLoadingWheel.visibility = View.VISIBLE
    }

    private fun hideLoadingProgress() {
        binding.asteroidRecycler.visibility = View.VISIBLE
        binding.statusLoadingWheel.visibility = View.GONE
    }
}