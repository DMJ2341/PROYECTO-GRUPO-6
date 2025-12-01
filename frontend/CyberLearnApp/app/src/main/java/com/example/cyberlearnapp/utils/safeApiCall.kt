package com.example.cyberlearnapp.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(
    dispatcher: kotlinx.coroutines.CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): T {
    return withContext(dispatcher) {
        try {
            apiCall()
        } catch (e: HttpException) {
            throw Exception("Error HTTP ${e.code()}: ${e.message()}")
        } catch (e: IOException) {
            throw Exception("Error de red: verifica tu conexión")
        } catch (e: Exception) {
            throw Exception("Error inesperado: ${e.message}")
        }
    }
}