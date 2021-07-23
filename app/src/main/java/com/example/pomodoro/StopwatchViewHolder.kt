package com.example.pomodoro

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoro.databinding.ItemTimerBinding

class StopwatchViewHolder(
    private val binding: ItemTimerBinding,
    private val listener: StopwatchListener,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.timer.text = stopwatch.currentMs.displayTime()
        binding.progressBar.setPeriod(stopwatch.startMs)
        binding.progressBar.setCurrent(stopwatch.startMs - stopwatch.currentMs)
        initButtonsListeners(stopwatch)
        when {
            stopwatch.isStarted -> startTimer(stopwatch)
            stopwatch.currentMs <= 0L -> preResetTimer(stopwatch)
            else -> stopTimer()
        }
    }

    private fun initButtonsListeners(stopwatch: Stopwatch) {
        binding.startStopButton.setOnClickListener {
            if (stopwatch.isStarted) listener.stop(stopwatch.id)
            else listener.start(stopwatch.id)
        }

        binding.buttonDelete.setOnClickListener {
            listener.delete(stopwatch.id)
        }
    }

    private fun preResetTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setIconResource(R.drawable.ic_baseline_refresh_24)
        binding.cardView.setCardBackgroundColor(
            ContextCompat.getColor(context, R.color.grey_400)
        )
        binding.startStopButton.setBackgroundColor(
            ContextCompat.getColor(context, R.color.pink_black)
        )
        binding.startStopButton.setIconTintResource(R.color.pink_100)
        binding.progressBar.setCurrent(0L)
        stopwatch.isStarted = false
        timer?.cancel()
        binding.startStopButton.setOnClickListener {
            binding.startStopButton.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            binding.startStopButton.setIconTintResource(R.color.pink_black)
            binding.startStopButton.setBackgroundColor(
                ContextCompat.getColor(context, R.color.pink_400)
            )
            binding.cardView.setCardBackgroundColor(
                ContextCompat.getColor(context, R.color.pink_300)
            )
            stopwatch.currentMs = stopwatch.startMs
            binding.timer.text = stopwatch.currentMs.displayTime()
            binding.startStopButton.setOnClickListener {
                listener.start(stopwatch.id)
            }
        }
        binding.indicator.isVisible = false
        (binding.indicator.background as? AnimationDrawable)?.stop()
    }

    private fun stopTimer() {
        binding.startStopButton.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        timer?.cancel()
        binding.indicator.isVisible = false
        (binding.indicator.background as? AnimationDrawable)?.stop()
    }

    private fun startTimer(stopwatch: Stopwatch) {
        binding.startStopButton.setIconResource(R.drawable.ic_baseline_pause_24)
        binding.startStopButton.setIconTintResource(R.color.pink_black)
        binding.startStopButton.setBackgroundColor(
            ContextCompat.getColor(context, R.color.pink_400)
        )
        binding.cardView.setCardBackgroundColor(
            ContextCompat.getColor(context, R.color.pink_300)
        )
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.indicator.isVisible = true
        (binding.indicator.background as? AnimationDrawable)?.start()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch): CountDownTimer {
        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {

            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs = stopwatch.stopMs - (System.currentTimeMillis() - stopwatch.systemStartMs)
                binding.timer.text = stopwatch.currentMs.displayTime()
                binding.progressBar.setCurrent(stopwatch.startMs - stopwatch.currentMs)
                if (stopwatch.currentMs <= 0L) onFinish()
            }

            override fun onFinish() {
                binding.timer.text = stopwatch.currentMs.displayTime()
                preResetTimer(stopwatch)
            }
        }
    }

    companion object {

        private const val UNIT_TEN_MS = 15L
        private const val PERIOD = 1000L * 60L * 60L * 24L

    }
}
