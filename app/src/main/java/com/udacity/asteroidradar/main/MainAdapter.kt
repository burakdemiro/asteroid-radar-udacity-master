package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidsRvViewholderBinding

class MainAdapter(val callback: AsteroidClick): ListAdapter<Asteroid, ViewHolder>(ViewHolder.DiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AsteroidsRvViewholderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asteroidListItem = getItem(position)
        holder.bind(asteroidListItem)
        holder.itemView.setOnClickListener {
            callback.onClick(asteroidListItem)
        }
    }

}

class ViewHolder(val binding: AsteroidsRvViewholderBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(asteroidListItem: Asteroid) {
        binding.apply {
            asteroidCodeName.text = asteroidListItem.codename
            asteroidCloseApproachDate.text = asteroidListItem.closeApproachDate

            if (asteroidListItem.isPotentiallyHazardous) {
                asteroidHazardIndicator.setImageResource(R.drawable.ic_status_potentially_hazardous)
            } else {
                asteroidHazardIndicator.setImageResource(R.drawable.ic_status_normal)
            }
        }
    }

    object DiffCallback: DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class AsteroidClick(val asteroidItem: (Asteroid) -> Unit) { // Following the pattern learnt in DevBytes lesson
    fun onClick(asteroid: Asteroid) = asteroidItem(asteroid)
}