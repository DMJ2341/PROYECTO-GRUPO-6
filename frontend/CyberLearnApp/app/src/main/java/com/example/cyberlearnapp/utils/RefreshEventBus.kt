package com.example.cyberlearnapp.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshEventBus @Inject constructor() {
    private val _refreshEvent = MutableSharedFlow<RefreshEvent>()
    val refreshEvent = _refreshEvent.asSharedFlow()

    suspend fun emit(event: RefreshEvent) {
        _refreshEvent.emit(event)
    }
}

sealed class RefreshEvent {
    object Dashboard : RefreshEvent()
    object Glossary : RefreshEvent()
    object Courses : RefreshEvent()
}