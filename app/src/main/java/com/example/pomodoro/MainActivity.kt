package com.example.pomodoro

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoro.databinding.ActivityMainBinding
import com.example.pomodoro.databinding.DialogAddBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity(), StopwatchListener, LifecycleObserver {

    private lateinit var binding: ActivityMainBinding
    private val stopwatches = mutableListOf<Stopwatch>()
    private val stopwatchAdapter: StopwatchAdapter = StopwatchAdapter(this, this)
    private var nextId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.buttonAdd.setOnClickListener {
            showAddDialog(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        var startedTimerId: Int? = null
        for (i in stopwatches.indices)
            if (stopwatches[i].isStarted) startedTimerId = i
        if (startedTimerId != null) {
            val startIntent = Intent(this, ForegroundService::class.java)
            startIntent.putExtra(COMMAND_ID, COMMAND_START)
            startIntent.putExtra(STARTED_TIMER_TIME_MS, stopwatches[startedTimerId].stopMs)
            startIntent.putExtra(
                SYSTEM_ON_TIMER_START_TIME_MS,
                stopwatches[startedTimerId].systemStartMs
            )
            startService(startIntent)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.putExtra(COMMAND_ID, COMMAND_STOP)
        startService(stopIntent)
    }

    private fun showAddDialog(context: Context) {

        val dialog = Dialog(context)
        val dialogBinding = DialogAddBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.buttonOk.setOnClickListener {
            val sec = if (dialogBinding.sec.text.isEmpty()) 0L
                else dialogBinding.sec.text.toString().toLong()
            val min = if (dialogBinding.min.text.isEmpty()) 0L
                else dialogBinding.min.text.toString().toLong()
            val hour = if (dialogBinding.hour.text.isEmpty()) 0L
            else dialogBinding.hour.text.toString().toLong()
            if (sec == 0L && min == 0L && hour == 0L)
                Toast.makeText(context, "At least seconds must be more then zero!", Toast.LENGTH_SHORT).show()

            else if (sec >= 59L || min >= 59L || hour >= 99L)
                Toast.makeText(context, "The value is too big!", Toast.LENGTH_SHORT).show()
            else {
                val time: Long = hour * 3600000 + min * 60000 + sec * 1000
                stopwatches.add(Stopwatch(nextId++, time, time, 0L, time, false))
                stopwatchAdapter.submitList(stopwatches.toList())
                dialog.dismiss()
                binding.hint.visibility = View.GONE
            }
        }

        val color = ColorDrawable(Color.TRANSPARENT)

        dialog.window!!.setBackgroundDrawable(color)
        dialog.show()
        dialog.window!!.attributes = layoutParams
    }


    override fun start(id: Int) {
        changeStopwatch(id, isStarted = true)
    }

    override fun stop(id: Int) {
        changeStopwatch(id, isStarted = false)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
        if (stopwatches.isEmpty()) binding.hint.visibility = View.VISIBLE
    }

    private fun changeStopwatch(id: Int, isStarted: Boolean) {
        for (i in stopwatches.indices)
            if (stopwatches[i].id == id)
                stopwatches[i] = Stopwatch(
                    id,
                    stopwatches[i].startMs,
                    stopwatches[i].currentMs,
                    System.currentTimeMillis(),
                    stopwatches[i].currentMs,
                    isStarted
                )
            else if (stopwatches[i].isStarted)
                stopwatches[i] = Stopwatch(
                    stopwatches[i].id,
                    stopwatches[i].startMs,
                    stopwatches[i].stopMs,
                    stopwatches[i].systemStartMs,
                    stopwatches[i].currentMs,
                    false
                )
        stopwatchAdapter.submitList(stopwatches.toList())
    }
}