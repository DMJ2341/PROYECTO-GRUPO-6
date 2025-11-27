package com.example.cyberlearnapp.repository

import com.example.cyberlearnapp.network.ApiService
import com.example.cyberlearnapp.network.models.GlossaryTerm
import javax.inject.Inject

class GlossaryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getTerms(query: String = ""): List<GlossaryTerm> {
        try {
            // ✅ CORRECCIÓN: Usar la función de API correcta (getTerms o searchTerms)
            val response = if (query.isNotEmpty()) {
                // Si hay query, usa la ruta de búsqueda
                apiService.searchGlossaryTerms(query)
            } else {
                // Si no hay query, usa la ruta de obtener todos (sin argumentos)
                apiService.getGlossaryTerms()
            }

            if (response.isSuccessful && response.body()?.success == true) {
                return response.body()?.terms ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }
}