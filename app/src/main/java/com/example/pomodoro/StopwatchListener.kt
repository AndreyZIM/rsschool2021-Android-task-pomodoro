package com.example.pomodoro

interface StopwatchListener {
    fun start(id: Int)
    fun stop(id: Int)
    fun delete(id: Int)
}