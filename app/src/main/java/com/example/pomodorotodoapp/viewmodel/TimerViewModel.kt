package com.example.pomodorotodoapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    companion object {
        private const val DEFAULT_TIME = 10L
    }

    private val _timeLeft = MutableStateFlow(DEFAULT_TIME)
    val timeLeft: StateFlow<Long> = _timeLeft.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer() {
        if (_isRunning.value) return

        _isFinished.value = false
        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
            _isFinished.value = true
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resetTimer() {
        timerJob?.cancel()
        _isRunning.value = false
        _isFinished.value = false
        _timeLeft.value = DEFAULT_TIME
    }

    fun consumeFinishedEvent() {
        _isFinished.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}