package com.example.pomodoro

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoro.databinding.ItemTimerBinding

class StopwatchAdapter(
    private val listener: StopwatchListener,
    private val context: Context
) : ListAdapter<Stopwatch, StopwatchViewHolder>(itemComparator) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopwatchViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTimerBinding.inflate(layoutInflater, parent, false)
        return StopwatchViewHolder(binding, listener, context)
    }

    override fun onBindViewHolder(holder: StopwatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {

        private val itemComparator = object : DiffUtil.ItemCallback<Stopwatch>() {

            override fun areItemsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stopwatch, newItem: Stopwatch): Boolean {
                return oldItem.currentMs == newItem.currentMs &&
                        oldItem.isStarted == newItem.isStarted &&
                        oldItem.startMs == newItem.startMs &&
                        oldItem.stopMs == newItem.stopMs &&
                        oldItem.systemStartMs == newItem.systemStartMs
            }

            override fun getChangePayload(oldItem: Stopwatch, newItem: Stopwatch) = Any()
        }
    }

}