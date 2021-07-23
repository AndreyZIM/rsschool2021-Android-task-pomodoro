package com.example.pomodoro

class Stopwatch(
    val id: Int,
    val startMs: Long, // начальное время таймера
    val stopMs: Long, // время таймера на котором произошла остановка
    val systemStartMs: Long, // системное время, взятое на момента старта таймера
    var currentMs: Long, // текущее время таймера
    var isStarted: Boolean // флаг запуска таймера
)